
package jb.selfregulation.impl.functopt.problem;

import java.util.Properties;
import java.util.Random;

import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;


/**
 * Type: Function<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Function extends Problem
{        
    // internal
    protected long internalCyclePosition;
    protected double alpha;
    protected boolean inverted;
    protected long totalEvaluations;
  
    // setup
    protected Random rand;
    
    // config
    protected int bitsPerVariate;
    protected int numDimensions;
    protected long cycleLength;
    protected double jitterPercentage;
    
    
//    public Function(
//            int aNumBitsPerVariate, 
//            int aNumDimensions,
//            Random aRand)
//    {
//        bitsPerVariate = aNumBitsPerVariate;
//        numDimensions = aNumDimensions;
//        rand = aRand;
//    }    
    
    
    public void resetTotalEvaluations()
    {
        totalEvaluations = 0;
    }
    
    public boolean isOutOfBounds(double [] v)
    {
        for (int i = 0; i < v.length; i++)
        {
            if(isOutOfBounds(i, v[i]))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean isOutOfBounds(int dimension, double v)
    {
        double [][] minmax = getGenotypeMinMax();
        
        if(v < minmax[dimension][0] || v > minmax[dimension][1])
        {
            return true;
        }
        return false;
    }
    
    
    public long getTotalEvaluations()
    {
        return totalEvaluations;
    }
    
    public String getBase()
    {
        return ".problem.function";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".problem.function";
        bitsPerVariate = Integer.parseInt(prop.getProperty(b + ".bits"));
        numDimensions = Integer.parseInt(prop.getProperty(b + ".dimensions"));
        cycleLength = Long.parseLong(prop.getProperty(b + ".cyclelength"));
        jitterPercentage = Double.parseDouble(prop.getProperty(b + ".jitter"));
    }
    
    public void setup(SystemState aState)
    {
        rand = aState.rand;
    }
    
    
    
    
    public abstract double [][] getGenotypeMinMax();
    public abstract double evaluate(double [] v);    
    public abstract double getBestFitness();
    public abstract boolean isMinimisation();
    public abstract double [] getBestCoord();
    public abstract boolean supportsJitter();
    public abstract boolean supportsDynamic();
    
    
    public int getTotalBits()
    {
        return bitsPerVariate * numDimensions;
    }
    
    public double [] calculateGenotype(boolean[] aBitString)
    {
        return toGenotype(aBitString, getGenotypeMinMax(), numDimensions);
    }
    
    
    public static double [] toGenotype(
            boolean[] aBitString, 
            double [][] ranges, 
            int numDimensions)
    {
        double [] phenotype = new double[numDimensions]; 
        
        if(numDimensions>1 && (aBitString.length % numDimensions) != 0)
        {
            throw new RuntimeException("Unable to evenly divide "+aBitString.length+" bits into "+ numDimensions);
        }
        
        // determine the even division of bits
        int division = aBitString.length / numDimensions;
        
        // process each phenotypic value
        for (int i = 0, offset = 0; i < phenotype.length; i++, offset+=division)
        {
            // convert to double
            phenotype[i] = BitStringCommonUtils.bitsToDouble(aBitString, offset, division);
            // scale to required range
            phenotype[i] = (phenotype[i] * (ranges[i][1]-ranges[i][0])) + ranges[i][0];
        }
        
        return phenotype;
    }
    

    public int getBitsPerVariate()
    {
        return bitsPerVariate;
    }

    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    
    public double jitterVariate(int dimension, double value)
    {
        double j = value; 
        
        if(jitterPercentage > 0)
        {
            double [][] minmax = getGenotypeMinMax();            
            double range = minmax[dimension][1] - minmax[dimension][0];
            double jitter = (jitterPercentage * range);
            double min = Math.max(value - jitter, minmax[dimension][0]);
            double max = Math.min(value + jitter, minmax[dimension][1]);
            j = min + (rand.nextDouble() * (max-min));
        }
       
        return j;
    }
    
    public double getJitterPercentage()
    {
        return jitterPercentage;
    }
    public void setJitterPercentage(double jitterPercentage)
    {
        this.jitterPercentage = jitterPercentage;
    }
    
    public void updateCyclePosition()
    {
        if(cycleLength == 0)
        {
            return;
        }
       
        if(++internalCyclePosition >= cycleLength)
        {
            internalCyclePosition = 0;
            inverted = !inverted;
        }            
        
        alpha = ((double)internalCyclePosition / (double)cycleLength);
        
        if(inverted)
        {
            alpha = (1.0 - alpha);
        }        
    }


    public long getCycleLength()
    {
        return cycleLength;
    }


    public void setCycleLength(long c)
    {
        this.cycleLength = c;
        
        if(cycleLength > 0)
        {
            // update position if required
            while(internalCyclePosition > cycleLength)
            {
                internalCyclePosition -= cycleLength;            
            }        
            
            // update alpha
            alpha = ((double)internalCyclePosition / (double)cycleLength);
        }
        else
        {
            internalCyclePosition = 0;
            alpha = 1.0;
            inverted = true;
        }
    }

    public double getAlpha()
    {
        return alpha;
    }

    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
    }

    public long getInternalCyclePosition()
    {
        return internalCyclePosition;
    }

    public void setInternalCyclePosition(long internalCyclePosition)
    {
        this.internalCyclePosition = internalCyclePosition;
    }

    public boolean isInverted()
    {
        return inverted;
    }

    public void setInverted(boolean inverted)
    {
        this.inverted = inverted;
    }

    public Random getRand()
    {
        return rand;
    }

    public void setRand(Random rand)
    {
        this.rand = rand;
    }

    public void setBitsPerVariate(int bitsPerVariate)
    {
        this.bitsPerVariate = bitsPerVariate;
    }

    public void setNumDimensions(int numDimensions)
    {
        this.numDimensions = numDimensions;
    }
    
    
    
}
