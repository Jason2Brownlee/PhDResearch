
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
 * Type: ParallelHillclimbingAlgorithm<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description: A Comparison of Parallel and Sequential Niching Methods (1995)
 * <br/>
 * @author Jason Brownlee
 */
public class ParallelHillclimbingAlgorithm extends Algorithm
{    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField ratioField;

    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 100;
    public final static double DEFAULT_RATIO = 0.1;
    
    protected Random r;
    
    protected long seed;
    protected int popsize;
    protected double initialStepSizeRatio;
    protected double [] stepSize;

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
        int j = 0;
        int changes = 0;
        int direction = 0;
        while(p.isReamainingEvaluations())
        {
            LinkedList<Solution> children = generate(pop, direction, j, p);
            p.cost(children);
            bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
            LinkedList<Solution> n = new LinkedList<Solution>();
            for (int i = 0; i < children.size(); i++)
            {
                if(p.isBetter(children.get(i), pop.get(i)))
                {
                    // only accept improvements
                    n.add(children.get(i));
                    changes++;
                }
                else
                {
                    n.add(pop.get(i));
                }
            }
            pop = n;
            notifyListeners(p,pop);
            
            // positive and negative for this axis
            if(++direction >= 2)
            {
                // both directions have been done for this axis
                direction = 0;
                // check if all axis have been processed
                if(++j >= p.getDimensions())
                {
                    // check for any changes
                    if(changes == 0)
                    {
                        // adjust step sizes - current has reached its usefulness
                        for (int i = 0; i < stepSize.length; i++)
                        {
                            stepSize[i] /= 2.0;
                        }
                    }
                    j = 0;
                    changes = 0;
                }
            }
        }
        
        return bestEver;
    }
    
    
    protected LinkedList<Solution> generate(LinkedList<Solution> pop, int dir, int axis, Problem p)
    {
        LinkedList<Solution> n = new LinkedList<Solution>();
        for(Solution s : pop)
        {
            double [] coord = s.getCoordinate();
            double [] nCoord = new double[coord.length];
            System.arraycopy(coord, 0, nCoord, 0, nCoord.length);
            if(dir == 0)
            {
                nCoord[axis] += stepSize[axis];
            }
            else
            {
                nCoord[axis] -= stepSize[axis];
            }
            PopulationAlgorithmUtils.bounceCoord(nCoord, p);
            Solution ns = new Solution(nCoord);
            n.add(ns);
        }
        
        return n;
    }
    
    
    protected void prepareStepSize(Problem p)
    {
        stepSize = new double[p.getDimensions()];
        double [][] minmax = p.getMinmax();
        for (int i = 0; i < stepSize.length; i++)
        {
            stepSize[i] = (minmax[i][0]-minmax[i][1]) * initialStepSizeRatio;
        }
    }

    @Override
    public String getName()
    {
        return "Parallel Hillclimbing";
    }
        
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        popsize = DEFAULT_POPSIZE;
        initialStepSizeRatio = DEFAULT_RATIO;        
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel ratioLabel = new JLabel("Initial step size ratio:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        ratioField = new JTextField(Double.toString(initialStepSizeRatio), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(ratioLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(ratioField);

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
    protected int getNumParameters()
    {
        return 3;
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
        // initialStepSizeRatio
        try
        {
            initialStepSizeRatio = Double.parseDouble(ratioField.getText());
        }
        catch (Exception e)
        {
            initialStepSizeRatio = DEFAULT_RATIO;
            ratioField.setText("" + initialStepSizeRatio);
        }
        finally
        {
            if(initialStepSizeRatio>1||initialStepSizeRatio<0)
            {
                initialStepSizeRatio = DEFAULT_RATIO;
                ratioField.setText("" + initialStepSizeRatio);
            }            
            // prepare the step size
            prepareStepSize(p);
        }
    }

}
