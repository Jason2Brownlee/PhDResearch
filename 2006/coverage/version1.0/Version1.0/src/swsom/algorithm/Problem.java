
package swsom.algorithm;

import java.awt.Dimension;
import java.awt.Shape;
import java.util.Random;

import swsom.algorithm.stats.GraphStatistics;

/**
 * Type: Problem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public abstract class Problem
{
    public final static int DOMAIN_SIZE = 440;    
    public final static Dimension domain = new Dimension(DOMAIN_SIZE, DOMAIN_SIZE);    
    
    public Problem()
    {}
    
    public abstract double [] getRandomPointInDomain(Random rand);    
    public abstract double [] getRandomPointInProblemSpace(Random rand);
    public abstract double [][] getDomainBounds();    
    public abstract Shape getProblem();
    
    public double [][] generateSampleWithReplacement(Random aRand, int sampleSize)
    {
        double [][] sample = new double[sampleSize][];
        for (int i = 0; i < sample.length; i++)
        {
            sample[i] = getRandomPointInProblemSpace(aRand);
        }
        return sample;
    }
    
    public abstract boolean isNormalDistribution();
    
    
    public double getAreaUniverse()
    {
        return DOMAIN_SIZE*DOMAIN_SIZE;
    }
    
    public abstract double getAreaProblem();
    
    public abstract double getAreaShapeSpace();
    
    
    
    public String getProblemReport()
    {
        StringBuffer b = new StringBuffer(1024);
        
        String n = getClass().getName();
        n = n.substring(n.lastIndexOf('.')+1);
        
        b.append("Problem:............" + n + "\n");
        b.append("Area Universe:......" + GraphStatistics.format.format(getAreaUniverse()) + "\n");
        b.append("Area Shape Space:..." + GraphStatistics.format.format(getAreaShapeSpace()) + "\n");
        b.append("Area Problem:......." + GraphStatistics.format.format(getAreaProblem()) + "\n");
        
        return b.toString();
    }
    
    public double [][] generateSampleWithoutReplacement(Random aRand, int sampleSize)
    {
        double [][] sample = new double[sampleSize][];
        for (int i = 0; i < sample.length; i++)
        {
            boolean finished = false;
            double [] data = null;
            do
            {
                // generate
                data = getRandomPointInProblemSpace(aRand);
                // check for duplicate
                finished = true;
                for (int j = 0; j < i; j++)
                {
                    if(sample[j][0]==data[0] && sample[j][1]==data[1])
                    {
                        finished = false;
                        break;
                    }
                }                
            }
            while(!finished);
            
            sample[i] = data; 
        }
        return sample;
    }
    
    
    /**
     * @return Returns the domain.
     */
    public Dimension getDomain()
    {
        return domain;
    }
}
