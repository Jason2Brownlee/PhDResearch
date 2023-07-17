
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.BitStringCommonUtils;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: SimpleGeneticAlgorithm<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimpleGeneticAlgorithm extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    public final static double DEFAULT_CROSSOVER = 0.95;
    public final static double DEFAULT_MUTATION = 0.005;
    public final static int DEFAULT_POPSIZE = 100;
    public final static int DEFAULT_BOUTSIZE = 2;
    public final static int DEFAULT_ELITES = 1;
    
    protected Random r;
    
    protected long seed;
    protected double crossover;
    protected double mutation;
    protected int popsize;
    protected int boutSize;
    protected int elitism;
    
    protected JTextField seedField;
    protected JTextField crossoverField;
    protected JTextField mutationField;
    protected JTextField popsizeField;
    protected JTextField boutsizeField;
    protected JTextField elitismField;
    
    
    protected static class BinarySolution extends Solution
    {
        boolean [] bitString;
        
        public BinarySolution(){}
        public BinarySolution(boolean[] abitString)
        {
            setBitString(abitString);
        }

        public boolean[] getBitString()
        {
            return bitString;
        }

        public void setBitString(boolean[] bitString)
        {
            this.bitString = bitString;
        }
    }
    

    @Override
    protected Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            boolean [] b = RandomUtils.randomBitString(r, p);
            BinarySolution s = new BinarySolution();
            s.setBitString(b);
            s.setCoordinate(BitStringCommonUtils.bitsToCoord(b,p));
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // select
            LinkedList<Solution> selected = tournamentSelection(pop, popsize, p);            
            // reproduce
            LinkedList<Solution> children = reproduce(selected, popsize, p);            
            // evaluate
            p.cost(children);
            // test
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver);
            // elitism
            elitism(pop, children, p);
            pop = children;
        }
        
        return bestEver;
    }
    
    /**
     * Assumed to be sorted
     * @param pop
     * @param children
     * @param p
     */
    protected void elitism(
            LinkedList<Solution> pop, 
            LinkedList<Solution> children, 
            Problem p)
    {   
        for (int i = 0; i < elitism; i++)
        {
            children.removeFirst();
            if(p.isMinimise())
            {
                children.addLast(pop.get(i));
            }
            else
            {
                children.addLast(pop.get(pop.size()-1-i));
            }
        }
    }
    
    

    
    public LinkedList<Solution> reproduce(LinkedList<Solution> pop, int totalChildren, Problem p)
    {
        LinkedList<Solution> children = new LinkedList<Solution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);            
            Solution [] c = crossover((BinarySolution)p1, (BinarySolution)p2);
            for (int j = 0; children.size()<totalChildren && j < c.length; j++)
            {
                mutate((BinarySolution)c[j]);
                c[j].setCoordinate(BitStringCommonUtils.bitsToCoord(((BinarySolution)c[j]).getBitString(),p));
                children.add(c[j]);
            }
            
        }
        return children;
    }
    
    protected void mutate(BinarySolution s)
    {
        boolean [] string = s.getBitString();
        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }
    
    protected Solution [] crossover(BinarySolution p1, BinarySolution p2)
    {
        Solution [] children = new Solution[2];        
        // make a cut - or no cut as it were
        int cutPoint = (r.nextDouble()<crossover) ? r.nextInt(p1.getBitString().length) : 0;
        // create vectors
        boolean [] v1 = new boolean[p1.getBitString().length];
        boolean [] v2 = new boolean[p1.getBitString().length];
        // prepare vectors
        prepareVector(v1, p1.getBitString(), p2.getBitString(), cutPoint); // normal
        prepareVector(v2, p2.getBitString(), p1.getBitString(), cutPoint); // reversed
        // store children
        children[0] = new BinarySolution(v1);        
        children[1] = new BinarySolution(v2);
        return children;
    }
    
    protected void prepareVector(boolean [] v, boolean [] p1, boolean [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }
    
    public LinkedList<Solution> tournamentSelection(
            LinkedList<Solution> pop, 
            int numToSelection,
            Problem p)
    {
        LinkedList<Solution> selected = new LinkedList<Solution>();
        
        if((numToSelection%2) != 0)
        {
            numToSelection++; // need one more for luck (even)
        }
        
        // permits reselection!!!
        while(selected.size() < numToSelection)
        {
            Solution best = pop.get(r.nextInt(pop.size()));
            for (int i = 1; i < boutSize; i++)
            {
                Solution s = pop.get(r.nextInt(pop.size()));
                if(p.isBetter(s, best))
                {
                    best = s;
                }
            }
            selected.add(best);
        }
        
        return selected;
    }    
    

    @Override
    public String getName()
    {
        return "Simple Genetic Algorithm (SGA)";
    }


    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        crossover = DEFAULT_CROSSOVER;
        mutation = DEFAULT_MUTATION;
        popsize = DEFAULT_POPSIZE;
        boutSize = DEFAULT_BOUTSIZE;
        elitism = DEFAULT_ELITES;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel crossoverLabel = new JLabel("Crossover percentage:");
        JLabel mutationLabel = new JLabel("Mutation percentage:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel boutSizeLabel = new JLabel("Tournament bout size:");
        JLabel elitismLabel = new JLabel("Total elites:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        crossoverField = new JTextField(Double.toString(crossover), 10);
        mutationField = new JTextField(Double.toString(mutation), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        boutsizeField = new JTextField(Integer.toString(boutSize), 10);
        elitismField = new JTextField(Integer.toString(elitism), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(crossoverLabel);
        labelPane.add(mutationLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(boutSizeLabel);
        labelPane.add(elitismLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(crossoverField);
        fieldPane.add(mutationField);
        fieldPane.add(popsizeField);
        fieldPane.add(boutsizeField);
        fieldPane.add(elitismField);

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
        // crossover
        try
        {
            crossover = Double.parseDouble(crossoverField.getText());
        }
        catch (Exception e)
        {
            crossover = DEFAULT_CROSSOVER;
            crossoverField.setText("" + crossover);
        }
        finally
        {
            if(crossover>1||crossover<0)
            {
                crossover = DEFAULT_CROSSOVER;
                crossoverField.setText("" + crossover);
            }
        }
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
            if(popsize>p.getMaxEvaluations()||popsize<0)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
            }
        }
        // boutsize
        try
        {
            boutSize = Integer.parseInt(boutsizeField.getText());
        }
        catch (Exception e)
        {
            boutSize = DEFAULT_BOUTSIZE;
            boutsizeField.setText("" + boutSize);
        }
        finally
        {
            if(boutSize>popsize||popsize<0)
            {
                boutSize = DEFAULT_BOUTSIZE;
                boutsizeField.setText("" + boutSize);
            }
        }
        // elitism
        try
        {
            elitism = Integer.parseInt(elitismField.getText());
        }
        catch (Exception e)
        {
            elitism = DEFAULT_ELITES;
            elitismField.setText("" + elitism);
        }
        finally
        {
            if(elitism>popsize||elitism<0)
            {
                elitism = DEFAULT_ELITES;
                elitismField.setText("" + elitism);
            }
        }
    }

    @Override
    protected int getNumParameters()
    {
        return 6;
    }
}

