
package swsom.algorithm.problem;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Random;

import org.apache.commons.math.stat.descriptive.rank.Max;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.Problem;

/**
 * Type: GenericShapeProblem<br/>
 * Date: 26/02/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class GenericShapeProblem extends Problem
{
    public final static int OFFSET = 20;
    public final static int SHAPE_SIZE = 400;
    public final static Rectangle shapeSpace = new Rectangle(OFFSET,OFFSET,SHAPE_SIZE,SHAPE_SIZE);
    
    protected double [][] worldBounds;    
    protected static Max maximum = new Max();
    
    
    /**
     * 
     * @param aRand
     * @param domainWidth
     * @param domainHeight
     */
    public GenericShapeProblem()
    {
        prepareShapeArea();
        worldBounds = new double[][]
        {
                {0, domain.width-1},
                {0, domain.height-1}
        };
    }
    
    public double getAreaShapeSpace()
    {
        return SHAPE_SIZE*SHAPE_SIZE; 
    }
    
    
    public double [][] calculateDistributionMap(
            int aInterval, 
            double [][] aSamplePoints)
    {
        // must be an even breakdown
        if((SHAPE_SIZE%aInterval) != 0)
        {
            throw new IllegalArgumentException("The specified resolution does not break up the domain evenly: " + aInterval);
        }
        
        int resolution = SHAPE_SIZE / aInterval;
        double [][] map = new double[resolution][resolution];
        
        // process all points into the histogram
        for (int i = 0; i < aSamplePoints.length; i++)
        {
            // remap the points
            // the points are first reduced to the range [0,399] (initially 100<->499)
            // then the points are divided by the interval to determine which 
            // unit they belong to
            int x = (int) Math.ceil((aSamplePoints[i][0] - 100.0) / aInterval) - 1;
            x = ((x <= -1) ? 0 : x);
            int y = (int) Math.ceil((aSamplePoints[i][1] - 100.0) / aInterval) - 1;
            y = ((y <= -1) ? 0 : y);
            
            if(x<0 || x>=resolution || y<0 || y>=resolution)
            {
                throw new RuntimeException("Coordinates are invalid: ["+aSamplePoints[i][0]+","+aSamplePoints[i][1]+"] => ["+x+","+y+"].");
            }
            
            // increment the histogram 
            map[y][x]++;
        }        
        
        return map;
    }
    
    public double [][] calculateDistributionMap(
            int aInterval, 
            ExemplarVector [] vectors)
    {
        // must be an even breakdown
        if((SHAPE_SIZE%aInterval) != 0)
        {
            throw new IllegalArgumentException("The specified resolution does not break up the domain evenly: " + aInterval);
        }
        
        int resolution = SHAPE_SIZE / aInterval;
        double [][] map = new double[resolution][resolution];
        
        // process all points into the histogram
        for (int i = 0; i < vectors.length; i++)
        {
            // remap the points
            // the points are first reduced to the range [0,399] (initially 100<->499)
            // then the points are divided by the interval to determine which 
            // unit they belong to
            double [] data = vectors[i].getData();
            int x = (int) Math.ceil((Math.round(data[0]) - 100.0) / aInterval) - 1;
            int y = (int) Math.ceil((Math.round(data[1]) - 100.0) / aInterval) - 1;
            
            x = ((x<=-1) ? 0 : x);
            y = ((y<=-1) ? 0 : y);
            
            if(x<0 || x>=resolution || y<0 || y>=resolution)
            {
                throw new RuntimeException("Coordinates are invalid: ["+data[0]+","+data[1]+"] => ["+x+","+y+"].");
            }
            
            // increment the histogram 
            map[y][x]++;
        }        
        
        return map;
    }
    
    
    public double [][] convertHistogramToProbabilityMap(double [][] aHistogram)
    {
        double [][] map = new double[aHistogram.length][aHistogram[0].length];
        // find the max
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < map.length; i++)
        {
            double m = maximum.evaluate(aHistogram[i]);
            if(m > max)
            {
                max = m;
            }
        }
        
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map[y].length; x++)
            {
                if(aHistogram[y][x] != 0)
                {
                    map[y][x] = (aHistogram[y][x] / max);
                }
            }
        }
        
        return map;        
    }
    
    public String histogramToString(double [][] aHistogram)
    {
        StringBuffer b = new StringBuffer(1024);
        
        for (int y = 0; y < aHistogram.length; y++)
        {
            for (int x = 0; x < aHistogram[y].length; x++)
            {
                b.append(aHistogram[y][x]);
                if(x!=aHistogram[y].length-1)
                {
                    b.append("\t");
                }
            }            
            if(y!=aHistogram.length-1)
            {
                b.append("\n");
            }
        }
        
        return b.toString();
    }

   
    protected void prepareShapeArea()
    {           
        // specific shape
        prepareShapeProblem();
    }
    
    
    public double [][] getDomainBounds()
    {
        return worldBounds;
    }
    
    
    public Shape getShapeSpace()
    {
        return shapeSpace;
    }
    
    
    protected double getRandomInteger(Random rand, int max)
    {
        if(isNormalDistribution())
        {
            // normal
            return ( ((max*0.25) * rand.nextGaussian()) + (max*0.5)); 
        }
        
        // uniform
        return rand.nextInt(domain.width);               
    }
    
    
    public double[] getRandomPointInDomain(Random rand)
    {
        double [] vector = new double[2];
        vector[0] = getRandomInteger(rand, domain.width);
        vector[1] = getRandomInteger(rand, domain.height);
        return vector;
    }
    
    public double[] getRandomPointInProblemSpace(Random rand)
    {
        double [] vector = new double[2];               
        
        do
        {
            vector[0] = OFFSET + getRandomInteger(rand, SHAPE_SIZE);
            vector[1] = OFFSET + getRandomInteger(rand, SHAPE_SIZE);
        }
        while(!isCoordInProblemSpace((int)vector[0], (int)vector[1]));
        
        return vector;
    }
    
    public abstract boolean isCoordInProblemSpace(int x, int y);
    
    protected abstract void prepareShapeProblem();
}
