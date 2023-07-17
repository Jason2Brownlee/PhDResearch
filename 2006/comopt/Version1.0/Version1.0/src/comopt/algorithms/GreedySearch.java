
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
 * Type: RandomSearch<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GreedySearch extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 100;
    public final static double DEFAULT_MUTATION = 0.005;
    
    protected JTextField seedField;
    protected JTextField mutationField;
    protected JTextField popsizeField;
    
    protected Random r;
    protected long seed;    
    protected double mutation;
    protected int popsize;
    
    
    @Override
    protected int getNumParameters()
    {
        return 3;
    }
    
    @Override
    public void initialise(Problem p)
    {        
        try
        {
            seed = Long.parseLong(seedField.getText());
        }
        catch(Exception e)
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
            if(popsize>p.getMaxEvaluations()||popsize<=0)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
            }
        }
    }
    
    
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = DEFAULT_SEED;
        mutation = DEFAULT_MUTATION;
        popsize = DEFAULT_POPSIZE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population suze:");
        JLabel mutationLabel = new JLabel("Mutation probability:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        mutationField = new JTextField(Double.toString(mutation), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(mutationLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(mutationField);

        // Put the panels in another panel, labels on left,
        // text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }
    
    
    
    
    @Override
    protected Solution executeAlgorithm(Problem p)
    {        
        // initial solution is a greedy solution
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
        p.cost(pop);
        bestEver = AlgorithmUtils.getBest(pop, p, bestEver);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            LinkedList<Solution> tmp = new LinkedList<Solution>();
            for (int i = 0; i < pop.size(); i++)
            {
                Solution s = new Solution(pop.get(i));
                AlgorithmUtils.mutatePermutation(s,r,mutation);
                //AlgorithmUtils.twoOpt(s,r,mutation);
                tmp.add(s);
            }
            p.cost(tmp);
            bestEver = AlgorithmUtils.getBest(tmp, p, bestEver);
            // union
            pop.addAll(tmp);
            Collections.sort(pop);
            // greedy
            while(pop.size() > popsize)
            {
                pop.removeLast();
            }
            notifyListeners(p,pop,bestEver);
        }
        
        return bestEver;
    }


    @Override
    public String getName()
    {
        return "Greedy Search";
    }    
    
}
