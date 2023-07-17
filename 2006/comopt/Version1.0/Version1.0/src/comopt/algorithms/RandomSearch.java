
package comopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import comopt.algorithms.utils.RandomUtils;

/**
 * Type: RandomSearch<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RandomSearch extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    
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
            seed = Long.parseLong(seedField.getText());
        }
        catch(Exception e)
        {
            seed = DEFAULT_SEED;
            seedField.setText(Long.toString(seed));
        }
        
        r = new Random(seed);        
    }
    
    
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = DEFAULT_SEED;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        
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
    protected Solution executeAlgorithm(Problem p)
    {        
        int totalPoints = p.remainingFunctionEvaluations();
        LinkedList<Solution> pop = new  LinkedList<Solution>();
        while(pop.size() < totalPoints)
        {
            pop.add(RandomUtils.randomSolutionRange(p, r));
        }
        p.cost(pop);
        bestEver = AlgorithmUtils.getBest(pop, p, bestEver);  
        notifyListeners(p, pop, bestEver);
        return bestEver;
    }


    @Override
    public String getName()
    {
        return "Random Search";
    }    
    
}
