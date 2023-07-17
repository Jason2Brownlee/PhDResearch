
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * Type: DifferentialEvolution<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DifferentialEvolution extends Algorithm implements ActionListener
{
    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 100;
    public final static double DEFAULT_CROSSOVER = 0.8;
    public final static double DEFAULT_SCALE_FACTOR = 0.8;
    public final static Mode DEFAULT_MODE = Mode.DE_RAND_1_BIN;
    
    public enum Mode {
        DE_RAND_1_BIN, 
        DE_CURRENT_TO_RAND, 
        DE_RAND_1_EXP, 
        DE_CURRENT_TO_RAND_1_BIN}
    
    protected Random r;
    protected double K;
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField crossoverField;
    protected JTextField scalefactorField;
    protected JComboBox modeField;
    
    protected long seed;
    protected int popsize;
    protected double CR;
    protected double F;
    protected Mode mode;    

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
            LinkedList<Solution> children = generateChildren(pop, p);
            // evaluate
            p.cost(children);               
            // build the next pop
            LinkedList<Solution> n = new LinkedList<Solution>();
            for (int i = 0; i < children.size(); i++)
            {
                Solution s1 = pop.get(i);
                Solution s2 = children.get(i);             
                
                if(p.isBetter(s1,s2))
                {
                    n.add(s1);
                }
                else
                {
                    n.add(s2);
                }
            }             
            pop = n;      
            bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        }
        
        return bestEver;
    }
    
    public LinkedList<Solution> generateChildren(LinkedList<Solution> pop, Problem p)
    {
        int NP = pop.size();
        int D = p.getDimensions();
        LinkedList<Solution> children = new LinkedList<Solution>();
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3 = -1;
            do{r1=r.nextInt(NP);}while(r1==i);
            do{r2=r.nextInt(NP);}while(r2==i||r2==r1);
            do{r3=r.nextInt(NP);}while(r3==i||r3==r1||r3==r2);
            
            double [] p0 = pop.get(i).getCoordinate();
            double [] p1 = pop.get(r1).getCoordinate();
            double [] p2 = pop.get(r2).getCoordinate();
            double [] p3 = pop.get(r3).getCoordinate();     
            double [] child = null;
            
            switch(mode)
            {
                case DE_RAND_1_BIN:
                {
                    child = DE_RAND_1_BIN(p0, p1, p2, p3, D);
                    break;
                }        
                case DE_CURRENT_TO_RAND:
                {
                    child = DE_CURRENT_TO_RAND(p0, p1, p2, p3, D);
                    break;
                }
                case DE_RAND_1_EXP:
                {
                    child = DE_RAND_1_EXP(p0, p1, p2, p3, D);
                    break;
                }
                case DE_CURRENT_TO_RAND_1_BIN:
                {
                    child = DE_CURRENT_TO_RAND_1_BIN(p0, p1, p2, p3, D);
                    break;
                }
                default:
                {
                    throw new RuntimeException("Unknown mode: " + mode);
                }
            }
            
            // add child to list
            PopulationAlgorithmUtils.bounceCoord(child, p);
            Solution s = new Solution(child);
            children.add(s);
        }
        
        return children;
    }
    
    protected double [] DE_RAND_1_BIN(double [] p0, double [] p1, double [] p2, double [] p3, int D)
    {
        double [] child = new double[D];
        
        int j = (int) (r.nextDouble() * D); // random starting point
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                child[j] = p3[j] + F * (p1[j] - p2[j]);
            }
            else
            {
                child[j] = p0[j];
            }               
            
            // check bounds
            j = (j + 1) % D; // wrap
        }
        
        return child;
    }
    
    protected double [] DE_CURRENT_TO_RAND(double [] p0, double [] p1, double [] p2, double [] p3, int D)
    {
        double [] child = new double[D];
        
        for (int j = 0; j < D; j++)
        {
            // randomise K
            K = r.nextDouble();            
            child[j] = p0[j] + (K * (p3[j] - p0[j])) + (F * (p1[j] - p2[j]));
        }
        
        return child;
    }
    
    protected double [] DE_RAND_1_EXP(double [] p0, double [] p1, double [] p2, double [] p3, int D)
    {
        double [] child = new double[D];   
        
        int j = (int) (r.nextDouble() * D); // random starting point
        int flag = 0;
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                flag = 1;
            }
            if(flag == 1)
            {
                child[j] = p3[j] + F * (p1[j] - p2[j]);
            }
            else
            {
                child[j] = p0[j];
            }             
            
            // check bounds
            j = (j + 1) % D; // wrap
        }
        
        return child;
    }
    
    
    protected double [] DE_CURRENT_TO_RAND_1_BIN(double [] p0, double [] p1, double [] p2, double [] p3, int D)
    {
        double [] child = new double[D];
        
        int j = (int) (r.nextDouble() * D); // random starting point
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                // randomise K
                K = r.nextDouble();                
                child[j] = p0[j] +  (K * (p3[j] - p0[j])) + (F * (p1[j] - p2[j]));
            }
            else
            {
                child[j] = p0[j];
            }  
            
            // check bounds
            j = (j + 1) % D; // wrap                
        }
        
        return child;
    }
    
    
    @Override
    public String getName()
    {
        return "Differential Evolution (DE)";
    }

    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        popsize = DEFAULT_POPSIZE;
        CR = DEFAULT_CROSSOVER;
        F = DEFAULT_SCALE_FACTOR;
        mode = DEFAULT_MODE; 
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel crossoverLabel = new JLabel("Crossover factor:");
        JLabel scalefactorLabel = new JLabel("Scale factor:");
        JLabel modeLabel = new JLabel("Combination mode:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        crossoverField = new JTextField(Double.toString(CR), 10);
        scalefactorField = new JTextField(Double.toString(F), 10);
        modeField = new JComboBox(Mode.values());
        modeField.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(crossoverLabel);
        labelPane.add(scalefactorLabel);
        labelPane.add(modeLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(crossoverField);
        fieldPane.add(scalefactorField);
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

    
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == modeField)
        {
            mode = (Mode) modeField.getSelectedItem();
        }        
    }

    @Override
    protected int getNumParameters()
    {
        return 5;
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
        // crossover
        try
        {
            CR = Double.parseDouble(crossoverField.getText());
        }
        catch (Exception e)
        {
            CR = DEFAULT_CROSSOVER;
            crossoverField.setText("" + CR);
        }
        finally
        {
            if(CR>1||CR<0)
            {
                CR = DEFAULT_CROSSOVER;
                crossoverField.setText("" + CR);
            }
        }
        // scalefactor
        try
        {
            F = Double.parseDouble(scalefactorField.getText());
        }
        catch (Exception e)
        {
            F = DEFAULT_SCALE_FACTOR;
            scalefactorField.setText("" + F);
        }
        finally
        {
            if(CR<0)
            {
                F = DEFAULT_SCALE_FACTOR;
                scalefactorField.setText("" + F);
            }
        }
    }

}
