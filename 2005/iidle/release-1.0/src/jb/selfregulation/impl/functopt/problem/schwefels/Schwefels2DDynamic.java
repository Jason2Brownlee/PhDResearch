
package jb.selfregulation.impl.functopt.problem.schwefels;

import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.drawing.InterpolatedFunctionPlot;


/**
 * 
 * Type: Schwefels2DDynamic<br/>
 * Date: 15/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Schwefels2DDynamic extends Schwefels
{
   
//    public static void main(String[] args)
//    {
//        try
//        {
//            Random r = new Random(1);            
//            Schwefels2DDynamic f = new Schwefels2DDynamic(r);
////            f.setJitterPercentage(0.01);
//            f.setCycleLength(100);            
//            InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(f, 50);
//            JFrame frame = new JFrame("Test");
//            frame.setSize(640, 640);
//            frame.getContentPane().add(plot);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setVisible(true); 
//            if(f.getCycleLength() > 0)
//            {
//                while(true)
//                {
//                    f.updateCyclePosition();
//                    plot.latticeChangedEvent();                    
//                    Thread.sleep(50);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    
  
//    public Schwefels2DDynamic(Random aRand)
//    {
//        super(2, aRand);
//        // prepare best coord
//        bestCoord = new double[]{-420.9687, +420.9687};
//        // prepare best fitness
//        bestFitness = evaluate(bestCoord);
//    }        
  
    public void setup(SystemState aState)
    {
        super.setup(aState);
        
        if(numDimensions != 2)
        {
            throw new RuntimeException("Unsuuported dimensionaility " + numDimensions);
        }
        
        // prepare best coord
        bestCoord = new double[]{-420.9687, +420.9687};
        // prepare best fitness
        bestFitness = evaluate(bestCoord);
    }
    
    
    public boolean supportsDynamic()
    {
        return true;
    }   
    public double evaluate(double[] v)
    {                  
        totalEvaluations++;
        // do the alpha magic
        double c1 = (alpha * v[0] + (1.0-alpha) * (-v[0]));
        double c2 = (alpha * v[1] + (1.0-alpha) * (-v[1]));
        // get bits
        double x = evaluate(0, c1);
        double y = -evaluate(1, c2); // put the minima in bottom right        
        // combine
        return (x + y); 
    }  
}
