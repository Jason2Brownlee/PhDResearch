
package jb.selfregulation.impl.functopt.problem.schwefels;

import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.drawing.InterpolatedFunctionPlot;
import jb.selfregulation.impl.functopt.problem.Function;



/**
 * Type: Schwefels2D<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * 
 * http://www.geatbx.com/docu/fcnfun7.html
 * f7(x)=sum(-x(i)·sin(sqrt(abs(x(i))))), i=1:n;
 * -500<=x(i)<=500.        
 * Global Minimum: f(x)=-n·418.9829; x(i)=420.9687, i=1:n.
 * <br/>
 * @author Jason Brownlee
 */
public class Schwefels extends Function
{
//    protected static int BITS_PER_VARIATE = 32;
        
    protected double [][] range;
    protected double [] bestCoord;
    protected double bestFitness;
    
   
//    public Schwefels(int aNumDimensions, Random aRand)
//        {
//        super(BITS_PER_VARIATE, aNumDimensions, aRand);   
//    }    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        prep();
    }
    
    public void prep()
    {
        
        // prepare the range
        range = new double[numDimensions][2];
        for (int i = 0; i < numDimensions; i++)
        {
            range[i][0] = -500.0;
            range[i][1] = +500.0;
        }
        // prepare best coord
        bestCoord = new double[numDimensions];
        for (int i = 0; i < bestCoord.length; i++)
        {
            bestCoord[i] = 420.9687;
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
        double sum = 0;        
        for (int i = 0; i < numDimensions; i++)
        {
            sum += evaluate(i, v[i]);
        }
        totalEvaluations++;
        return sum;
    }        
    protected double evaluate(int i, double v)
    {        
        // get jittered version
        v = jitterVariate(i, v);
        // calculate value
        return (-v) * Math.sin(Math.sqrt(Math.abs(v)));
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
    
    
    public static void main(String[] args)
    {
        try
        {
            Random r = new Random(1);            
            Schwefels f = new Schwefels();
            f.bitsPerVariate = 32;
            f.numDimensions = 5;
            f.cycleLength = 0;
            f.jitterPercentage = 0.0;
            f.prep();

//            InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(f, 50);
//            JFrame frame = new JFrame("Test");
//            frame.setSize(640, 640);
//            frame.getContentPane().add(plot);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setVisible(true);
            
            System.out.println(f.getBestFitness());
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
