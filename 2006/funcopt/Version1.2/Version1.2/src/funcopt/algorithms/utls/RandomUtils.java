
package funcopt.algorithms.utls;

import java.util.LinkedList;
import java.util.Random;

import funcopt.Problem;
import funcopt.Solution;

/**
 * 
 * Type: RandomUtils<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RandomUtils
{
    /**
     * Generate a random coordinate within the bounds of the problem domain 
     * 
     * @param p
     * @param r
     * @return
     */
    public static Solution randomSolutionRange(Problem p, Random r)
    {
        return new Solution(randomPointInRange(p,r));
    }
    
    public static double [] randomPointInRange(Problem p, Random r)
    {
        double [] coord = new double[p.getDimensions()];
        double [][] minmax = p.getMinmax();         
        for (int i = 0; i < coord.length; i++)
        {
            double range = minmax[i][1] - minmax[i][0];
            coord[i] = minmax[i][0] + (range * r.nextDouble());
        }        
        return coord;
    }
    
    public static boolean [] randomBitString(Random r, Problem p)
    {
        int totalBits = p.getDimensions() * (p.getBitPrecision()-1);        
        boolean [] b = new boolean[totalBits];        
        for (int i = 0; i < b.length; i++)
        {
            b[i] = r.nextBoolean();
        }        
        return b;
    }
    
    
    public static LinkedList<Solution> generateUniform2DPattern(int aTotalPoints, Problem p)
    {
        double [][] minmax = p.getMinmax();
        LinkedList<Solution> solutions = new LinkedList<Solution>();
        
        double xRange = (minmax[0][1] - minmax[0][0]);
        double yRange = (minmax[1][1] - minmax[1][0]);
        
        int numPerDim = (int) Math.ceil(Math.sqrt(aTotalPoints));
        double xIncrement = (1.0 / numPerDim) * xRange;
        double yIncrement = (1.0 / numPerDim) * yRange;
        int count = 0;
        for (int yy = 0; yy < numPerDim; yy++)
        {
            for (int xx = 0; xx < numPerDim && count < aTotalPoints; xx++)
            {
                double [] coord = new double[2];
                coord[0] = minmax[0][0] + ((xx * xIncrement) + (xIncrement/2));
                coord[1] = minmax[1][0] + ((yy * yIncrement) + (yIncrement/2));
                solutions.add(new Solution(coord));
                count++;
            }
        }
        
        return solutions;
    }
}
