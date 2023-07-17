
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
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
 * Type: SimulatedAnnealing<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimulatedAnnealing extends Algorithm
{
    // HACK 
    public final static int DEFAULT_DIMENSIONS = 2;
    
    public final static int DEFAULT_INIT_POPSIZE = 100;
    public final static double DEFAULT_TEMP = 5; // TODO - what is a good default???
    public final static double DEFAULT_STEPSIZE_WINDOW = 20;
    public final static double DEFAULT_STEP_ADJUST = 2;
    public final static double DEFAULT_TEMP_WINDOW = Math.max(100, 5 * DEFAULT_DIMENSIONS);
    public final static double DEFAULT_TEMP_ADJUST = 0.85;
    
    protected JTextField seedField;
    protected JTextField initialpopsizeField;
    protected JTextField tempField;
    protected JTextField NsField;
    protected JTextField cField;
    protected JTextField NtField;
    protected JTextField rTField;
    
    protected Random r;
    
    protected long seed;
    protected int initialpopsize;   
    
    protected double [] v; // step size vector
    protected double temp; // current temperature
    protected double Ns; // number of steps before changing step (step window)
    protected double c; // step adjustment parameter
    protected double Nt; // temperature step size (Ns*Nt)
    protected double rT; // temp adjustement coefficient

    @Override
    public Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < initialpopsize)
        {
            Solution s = RandomUtils.randomSolutionRange(p, r);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        
        Solution current = bestEver;
        
        // run algorithm until there are no evaluations left
        int h = 0;
        int j = 0;
        int m = 0;
        int [] accepted = new int[p.getDimensions()];
        while(p.isReamainingEvaluations())
        {
            // generate sample
            Solution newCurrent = generateNextSample(current, p, h);
            // evaluate
            p.cost(newCurrent);
            // check for new best ever
            if(p.isBetter(newCurrent, bestEver))
            {
                bestEver = newCurrent;
            }
            // check for acceptance (better or Metropolis)
            if(p.isBetter(newCurrent, current) || shouldAcceptMetropolis(current, newCurrent))
            {
                current = newCurrent;                
                accepted[h]++;
            }
            if(++h >= p.getDimensions())
            {
                h = 0; // reset
                j++;
                // check for step variation
                if(j >= Ns)
                {
                    // update step size
                    updateStepSize(accepted, p);                    
                    j = 0; // reset
                    Arrays.fill(accepted, 0); // reset
                    // check for temp adjustment
                    if(++m >= Nt)
                    {
                        // update temperature
                        updateTemperature();
                        m = 0; // reset
                        
                        // no termination criterion - using all the evals
                    }
                }
            }
        }
        
        return bestEver;
    }

    
    protected Solution generateNextSample(Solution aCurrentSample, Problem p, int axis)
    {
        // duplicate the current coordinate
        double [] parent = aCurrentSample.getCoordinate(); 
        double [] coord = new double[parent.length];
        System.arraycopy(parent, 0, coord, 0, parent.length);        
        
        // generate for a single axis
        coord[axis] = coord[axis] + r.nextGaussian() * v[axis];
        // reflect
        PopulationAlgorithmUtils.fixCoordBounds(coord, p);
        
        return new Solution(coord);
    }
    
    protected void updateStepSize(int [] n, Problem p)
    {
        for (int i = 0; i < v.length; i++)
        {
            if(n[i] > 0.6*Ns)
            {
                v[i] = v[i] * (1.0 + c * (((n[i]/Ns)-0.6)/0.4));
            }
            else if(n[i] < 0.4*Ns)
            {
                v[i] = v[i] / (1.0 + c * ((0.4-(n[i]/Ns))/0.4));
            }
            // otherwise no change
        }
        // ensure stepsize is not too large
        double [][] minmax = p.getMinmax();
        for (int i = 0; i < v.length; i++)
        {
            if(v[i] > minmax[i][1])
            {
                v[i] = minmax[i][1];
            }
            else if(v[i] < minmax[i][0])
            {
                v[i] = minmax[i][0];
            }
        }
        
    }
    
    protected void updateTemperature()
    {
        temp = rT * temp;
    }
    
    protected boolean shouldAcceptMetropolis(Solution current, Solution newCurrent)
    {
        // exp(-delta f / T)
        double diff = Math.abs(current.getScore() - newCurrent.getScore());
        double p = Math.exp(-diff / temp);
        if(r.nextDouble() < p)
        {
            return true;
        }        
        
        return false;
    }
    
    
    
    @Override
    public String getName()
    {
        return "Simulated Annealing (SA)";
    }


    
    @Override
    protected JPanel getConfigurationPane()
    {        
        seed = System.currentTimeMillis();
        initialpopsize = DEFAULT_INIT_POPSIZE;   
        temp = DEFAULT_TEMP; // current temperature
        Ns = DEFAULT_STEPSIZE_WINDOW; // number of steps before changing step (step window)
        c = DEFAULT_STEP_ADJUST; // step adjustment parameter
        Nt = DEFAULT_TEMP_WINDOW; // temperature step size (Ns*Nt)
        rT = DEFAULT_TEMP_ADJUST; // temp adjustement coefficient
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel initialpopsizeLabel = new JLabel("Initial Population size:");
        JLabel tempLabel = new JLabel("Initial temperature:");
        JLabel NsLabel = new JLabel("Step-size window");
        JLabel cLabel = new JLabel("Step-size adjustment:");
        JLabel NtLabel = new JLabel("Temperature window:");
        JLabel rTLabel = new JLabel("Temperature adjustment:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        initialpopsizeField = new JTextField(Integer.toString(initialpopsize), 10);
        tempField = new JTextField(Double.toString(temp), 10);
        NsField = new JTextField(Double.toString(Ns), 10);
        cField = new JTextField(Double.toString(c), 10);
        NtField = new JTextField(Double.toString(Nt), 10);
        rTField = new JTextField(Double.toString(rT), 10);

        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(initialpopsizeLabel);
        labelPane.add(tempLabel);
        labelPane.add(NsLabel);
        labelPane.add(cLabel);
        labelPane.add(NtLabel);
        labelPane.add(rTLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(initialpopsizeField);
        fieldPane.add(tempField);
        fieldPane.add(NsField);
        fieldPane.add(cField);
        fieldPane.add(NtField);
        fieldPane.add(rTField);

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
        return 7;
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
        // popsize
        try
        {
            initialpopsize = Integer.parseInt(initialpopsizeField.getText());
        }
        catch (Exception e)
        {
            initialpopsize = DEFAULT_INIT_POPSIZE;
            initialpopsizeField.setText("" + initialpopsizeField);
        }
        finally
        {
            if(initialpopsize>p.getMaxEvaluations()||initialpopsize<=0)
            {
                initialpopsize = DEFAULT_INIT_POPSIZE;
                initialpopsizeField.setText("" + initialpopsizeField);
            }
        }
        // tempField
        try
        {
            temp = Double.parseDouble(tempField.getText());
        }
        catch (Exception e)
        {
            temp = DEFAULT_TEMP;
            tempField.setText("" + temp);
        }
        finally
        {
            if(temp<0)
            {
                temp = DEFAULT_TEMP;
                tempField.setText("" + temp);
            }
        }
        // Ns
        try
        {
            Ns = Double.parseDouble(NsField.getText());
        }
        catch (Exception e)
        {
            Ns = DEFAULT_STEPSIZE_WINDOW;
            NsField.setText("" + Ns);
        }
        finally
        {
            if(Ns<0)
            {
                Ns = DEFAULT_STEPSIZE_WINDOW;
                NsField.setText("" + Ns);
            }
        }
        // c
        try
        {
            c = Double.parseDouble(cField.getText());
        }
        catch (Exception e)
        {
            c = DEFAULT_STEP_ADJUST;
            cField.setText("" + c);
        }
        finally
        {
            if(c<0)
            {
                c = DEFAULT_STEP_ADJUST;
                cField.setText("" + c);
            }
        }
        // Nt
        try
        {
            Nt = Double.parseDouble(NtField.getText());
        }
        catch (Exception e)
        {
            Nt = DEFAULT_TEMP_WINDOW;
            NtField.setText("" + Nt);
        }
        finally
        {
            if(Nt<0)
            {
                Nt = DEFAULT_TEMP_WINDOW;
                NtField.setText("" + Nt);
            }
        }
        // rT
        try
        {
            rT = Double.parseDouble(rTField.getText());
        }
        catch (Exception e)
        {
            rT = DEFAULT_TEMP_ADJUST;
            rTField.setText("" + rT);
        }
        finally
        {
            if(rT<0 || rT>1)
            {
                rT = DEFAULT_TEMP_ADJUST;
                rTField.setText("" + rT);
            }
        }
        
        v = new double[p.getDimensions()]; // step size vector
        for (int i = 0; i < v.length; i++)
        {
            v[i] = r.nextDouble(); // TODO - what value to initialise???
        }
    }
}
