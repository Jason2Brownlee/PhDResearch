
package jb.selfregulation.impl.functopt.problem.rastrigins;

import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.drawing.InterpolatedFunctionPlot;
import jb.selfregulation.impl.functopt.problem.Function;




/**
 * Type: Rastrigins<br/>
 * Date: 20/06/2005<br/>
 * <br/>
 * Description:
 * 
 * http://www.geatbx.com/docu/fcnfun6.html
 * f6(x)=10·n+sum(x(i)^2-10·cos(2·pi·x(i))), i=1:n;
 *  -5.12<=x(i)<=5.12.
 *  global minimum: f(x)=0; x(i)=0, i=1:n.
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class Rastrigins extends Function
{
//    protected static int BITS_PER_VARIATE = 32;
        
    protected double [][] range;
    protected double [] bestCoord;
    protected double bestFitness;
    
   
    /*
    public Rastrigins(int aNumDimensions, Random aRand)
    {
        super(BITS_PER_VARIATE, aNumDimensions, aRand);        
        
        // prepare the range
        range = new double[aNumDimensions][2];
        for (int i = 0; i < aNumDimensions; i++)
        {
            range[i][0] = -5.12;
            range[i][1] = 5.12;
        }
        // prepare best coord
        bestCoord = new double[aNumDimensions];
        for (int i = 0; i < bestCoord.length; i++)
        {
            bestCoord[i] = 0;
        }
        // prepare best fitness
        bestFitness = evaluate(bestCoord);
    }    
    */
    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
    
        // prepare the range
        range = new double[numDimensions][2];
        for (int i = 0; i < numDimensions; i++)
        {
            range[i][0] = -5.12;
            range[i][1] = 5.12;
        }
        // prepare best coord
        bestCoord = new double[numDimensions];
        for (int i = 0; i < bestCoord.length; i++)
        {
            bestCoord[i] = 0;
        }
        // prepare best fitness
        bestFitness = evaluate(bestCoord);    
    }
    
    
    
    public boolean supportsJitter()
    {
        return true;
    }
    public boolean supportsDynamic()
    {
        return false;
    }    
    public boolean isMinimisation()
    {
        return true;
    }    
    public double evaluate(double[] v)
    {
        totalEvaluations++;
        double sum = 0;        
        for (int i = 0; i < numDimensions; i++)
        {
            sum += evaluate(i, v[i]);
        }
        return 10 * numDimensions * sum;
    }        
    protected double evaluate(int i, double v)
    {
        // get jittered version
        v = jitterVariate(i, v);
        // calculate value        
        return (v*v) - 10.0 * Math.cos(2.0 * Math.PI * v);
    }
    
    public double getBestFitness()
    {
        return bestFitness;
    }
    public double[][] getGenotypeMinMax()
    {
        return range;
    }
    public double [] getBestCoord()
    {
        return bestCoord;
    }
    
    /*
    public static void main(String[] args)
    {
        try
        {
            Random r = new Random(1);            
            Rastrigins f = new Rastrigins(2, r);
//            f.setJitterPercentage(0.01); // jitter
            InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(f, 50);
            JFrame frame = new JFrame("Test");
            frame.setSize(640, 640);
            frame.getContentPane().add(plot);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}
