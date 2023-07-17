
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
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: RandomSearch<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RandomSearch extends Algorithm
{
    
    protected JTextField seedField;
    
    protected Random r;
    protected long seed;
    
    
    
    @Override
    protected int getNumParameters()
    {
        return 1;
    }
    
    @Override
    public void initialise(Problem p)
    {        
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
    }
    
    
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = System.currentTimeMillis();
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);

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
    public Solution executeAlgorithm(Problem p)
    {        
        int totalPoints = p.remainingFunctionEvaluations();
        LinkedList<Solution> pop = new  LinkedList<Solution>();
        while(pop.size() < totalPoints)
        {
            pop.add(RandomUtils.randomSolutionRange(p, r));
        }
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);         
        return bestEver;
    }


    @Override
    public String getName()
    {
        return "Random Search";
    }
    
    
}
