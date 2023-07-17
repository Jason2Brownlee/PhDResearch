
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: CrowdingGeneticAlgorithm<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class CrowdingGeneticAlgorithm extends Algorithm implements ActionListener
{
    public enum Mode 
    {
        Deterministic_Crowding, 
        Simple_Crowding
    }
    
    
    public final static long DEFAULT_SEED = 1;
    public final static double DEFAULT_MUTATION = 0.85;
    public final static int DEFAULT_POPSIZE = 100;
    public final static double DEFAULT_STDEV = 0.01;
    public final static Mode DEFAULT_MODE = Mode.Simple_Crowding;
    
    protected Random r;
    
    protected long seed;
    protected double mutation;
    protected int popsize;
    protected double stdev;
    protected Mode mode;
    
    protected JTextField seedField;
    protected JTextField mutationField;
    protected JTextField popsizeField;
    protected JTextField stdevField;
    protected JComboBox modeField;
    
    
    

    @Override
    protected Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            Solution s = RandomUtils.randomSolutionRange(p, r);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {       
            // reproduce
            LinkedList<Solution> children = reproduce(pop, popsize, p);            
            // evaluate
            p.cost(children);
            // test with clone so children order is not disrupted
            LinkedList<Solution> clone = (LinkedList<Solution>) children.clone();
            bestEver = PopulationAlgorithmUtils.getBest(clone, p, bestEver);
            if(p.isReamainingEvaluations())
            {
                // replacements
                pop = replacements(pop, children, p);
            }
        }
        
        return bestEver;
    }
    
    protected LinkedList<Solution> replacements(LinkedList<Solution> pop, LinkedList<Solution> children, Problem p)
    {
        LinkedList<Solution> np = null;
        
        switch(mode)
        {
            case Deterministic_Crowding:
            {
                np = deterministicCrowding(pop, children, p);
                break;
            }
            case Simple_Crowding:
            {
                np = simpleCrowding(pop, children, p);
                break;
            }
            default:
            {
                throw new RuntimeException("Unknown mode: " + mode);
            }
        }
        
        return np;
    }
    
    protected LinkedList<Solution> simpleCrowding(LinkedList<Solution> pop, LinkedList<Solution> children, Problem p)
    {
        LinkedList<Solution> np = new LinkedList<Solution>();
        np.addAll(pop);
        
        // replacements
        for (int i = 0; i < children.size(); i++)
        {
            Solution child = children.get(i);
            Solution bestMatch = getBestMatch(child, np);
            if(p.isBetter(child, bestMatch))
            {
                np.remove(bestMatch);
                np.add(child);
            }
        }
        
        return np;
    }
    protected LinkedList<Solution> deterministicCrowding(LinkedList<Solution> pop, LinkedList<Solution> children, Problem p)
    {
        LinkedList<Solution> np = new LinkedList<Solution>();        
        
        // replacements
        for (int i = 0; i < children.size(); i+=2)
        {
            Solution c1 = children.get(i);
            Solution c2 = children.get(i+1);
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);
            
            if(distance(p1,c1)+distance(p2,c2) <= distance(p1,c2)+distance(p2,c1))
            {
                if(p.isBetter(c1, p1))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p1);
                }
                if(p.isBetter(c2, p2))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p2);
                }
            }
            else
            {
                if(p.isBetter(c2, p1))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p1);
                }
                if(p.isBetter(c1, p2))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p2);
                }
            }
        }
        
        return np;
    }
    
    protected Solution getBestMatch(Solution s, LinkedList<Solution> pop)
    {
        Solution best = pop.get(0);
        double bestD = distance(s, pop.get(0));
        for (int i = 1; i < pop.size(); i++)
        {
            double d = distance(s, pop.get(i));
            if(d < bestD)
            {
                bestD = d;
                best = pop.get(i);
            }
        }
        return best;
    }
    
    
    protected double distance(Solution s1, Solution s2)
    {
        double [] c1 = s1.getCoordinate();
        double [] c2 = s2.getCoordinate();
        
        // root of the sum of the squared differences (Euclidean)
        
        double sum = 0.0;
        for (int i = 0; i < c2.length; i++)
        {
            sum += Math.pow((c1[i] - c2[i]), 2);
        }
        return Math.sqrt(sum);
    }
    
    public LinkedList<Solution> reproduce(LinkedList<Solution> pop, int totalChildren, Problem p)
    {
        // random pairings
        Collections.shuffle(pop, r);
        
        LinkedList<Solution> children = new LinkedList<Solution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);
            Solution [] c = uniformCrossover(p1, p2);
            for (int j = 0; children.size()<totalChildren && j < c.length; j++)
            {
                globalGaussianMutate(c[j], p);
                children.add(c[j]);
            }
            
        }
        return children;
    }
    
    public Solution [] uniformCrossover(Solution s1, Solution s2)
    {
        double [] p1 = s1.getCoordinate();
        double [] p2 = s2.getCoordinate();
        double [] child1 = new double[p1.length];
        double [] child2 = new double[p1.length];
        for (int i = 0; i < p1.length; i++)
        {
            child1[i] = r.nextBoolean() ? p1[i] : p2[i];
            child2[i] = r.nextBoolean() ? p1[i] : p2[i];
        }        
        return new Solution[]{new Solution(child1), new Solution(child2)};       
    }
    public void globalGaussianMutate(Solution s, Problem a)
    {   
        double [][] minmax = a.getMinmax();
        double [] p = s.getCoordinate();
        for (int i = 0; i < p.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                double range = (minmax[i][1] - minmax[i][0]); 
                p[i] += (r.nextGaussian() * (range * stdev));
            }
        }
        PopulationAlgorithmUtils.bounceCoord(s.getCoordinate(), a);       
    }
     
    

    @Override
    public String getName()
    {
        return "Crowding Genetic Algorithm (CGA)";
    }


    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == modeField)
        {
            mode = (Mode) modeField.getSelectedItem();
        }        
    }
    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        mutation = DEFAULT_MUTATION;
        popsize = DEFAULT_POPSIZE;
        stdev = DEFAULT_STDEV;
        mode = DEFAULT_MODE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel mutationLabel = new JLabel("Mutation percentage:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel stdevLabel = new JLabel("Mutation stdev:");
        JLabel modevLabel = new JLabel("Replacement Algorithm:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        mutationField = new JTextField(Double.toString(mutation), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        stdevField = new JTextField(Double.toString(stdev), 10);
        modeField = new JComboBox(Mode.values());
        modeField.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(mutationLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(stdevLabel);
        labelPane.add(modevLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(mutationField);
        fieldPane.add(popsizeField);
        fieldPane.add(stdevField);
        fieldPane.add(modeField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }
    
    @Override
    public void initialise(Problem p)
    {        
        // seed
        try
        {
            seed = Long.parseLong(seedField.getText());
        }
        catch (Exception e)
        {
            seed = DEFAULT_SEED;
            seedField.setText(Long.toString(seed));
        }
        r = new Random(seed);
        // mutation
        try
        {
            mutation = Double.parseDouble(mutationField.getText());
        }
        catch (Exception e)
        {
            mutation = DEFAULT_MUTATION;
            mutationField.setText("" + mutation);
        }
        finally
        {
            if(mutation>1||mutation<0)
            {
                mutation = DEFAULT_MUTATION;
                mutationField.setText("" + mutation);
            }
        }
        // popsize
        try
        {
            popsize = Integer.parseInt(popsizeField.getText());
        }
        catch (Exception e)
        {
            popsize = DEFAULT_POPSIZE;
            popsizeField.setText("" + popsize);
        }
        finally
        {
            // pop size MUST be even
            if((popsize%2)!=0)
            {
                popsize++;
            }
            
            if(popsize>p.getMaxEvaluations()||popsize<0)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
            }
        }
        // stdev
        try
        {
            stdev = Double.parseDouble(stdevField.getText());
        }
        catch (Exception e)
        {
            stdev = DEFAULT_STDEV;
            stdevField.setText("" + stdev);
        }
        finally
        {
            if(stdev<0)
            {
                stdev = DEFAULT_STDEV;
                stdevField.setText("" + stdev);
            }
        }
    }

    @Override
    protected int getNumParameters()
    {
        return 5;
    }
}


