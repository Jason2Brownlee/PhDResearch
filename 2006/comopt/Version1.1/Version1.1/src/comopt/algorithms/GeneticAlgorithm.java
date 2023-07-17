
package comopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import comopt.Algorithm;
import comopt.Problem;
import comopt.Solution;
import comopt.algorithms.utils.AlgorithmUtils;

/**
 * Type: GeneticAlgorithm<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GeneticAlgorithm extends Algorithm
{
    public final static double DEFAULT_CROSSOVER = 0.97;
    public final static double DEFAULT_MUTATION = 0.50;
    public final static int DEFAULT_POPSIZE = 100;
    public final static int DEFAULT_BOUTSIZE = 7;
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
    

    @Override
    protected Solution executeAlgorithm(Problem p)
    {              
        Solution start = AlgorithmUtils.generateNearestNeighbourSolution(p, r);
        LinkedList<Solution> pop = new  LinkedList<Solution>();
        pop.add(start);
        // generate initial population
        while(pop.size() < popsize)
        {
            Solution s = new Solution(start);
            AlgorithmUtils.mutatePermutation(s,r,mutation);
            pop.add(s);
        } 
        
        // evaluate
        p.cost(pop);
        bestEver = AlgorithmUtils.getBest(pop, p, bestEver);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // select
            LinkedList<Solution> selected = AlgorithmUtils.tournamentSelection(pop, popsize, p, r, boutSize);        
            // reproduce
            LinkedList<Solution> children = reproduce(selected, popsize-elitism, p);            
            // evaluate
            p.cost(children);
            // test
            bestEver = AlgorithmUtils.getBest(children, p, bestEver);    
            elitism(pop, children, p);
            pop = children;
            notifyListeners(p, pop, bestEver);
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
        Collections.sort(pop);       
        for (int i = 0; i < elitism; i++)
        {
            children.add(pop.get(i));
        }        
    }
    
    public LinkedList<Solution> reproduce(LinkedList<Solution> pop, int totalChildren, Problem p)
    {             
        LinkedList<Solution> children = new LinkedList<Solution>();
        
        // always expect two children from two parents
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);
            
            for (int j = 0; children.size()<totalChildren && j < 2; j++)
            {
                Solution c = AlgorithmUtils.edgeRecombination(p1, p2, r, crossover);
                AlgorithmUtils.twoOpt(c, r, mutation);
                children.add(c);
            }
        }
        
        return children;
    }    

    @Override
    public String getName()
    {
        return "Genetic Algorithm";
    }
    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = System.currentTimeMillis();
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
        seedField = new JTextField("SYSTIME", 10);
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
            if(seedField.getText().equalsIgnoreCase("SYSTIME"))
            {
                seed = System.currentTimeMillis();
            }
            else
            {
                seed = Long.parseLong(seedField.getText());
            }
        }
        catch(Exception e)
        {
            seed = System.currentTimeMillis();
            seedField.setText("SYSTIME");
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
            if(popsize>p.getMaxEvaluations()||popsize<=0)
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

