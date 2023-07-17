
package jb.selfregulation.views;

import java.util.Random;

import jb.selfregulation.impl.functopt.problem.BitStringCommonUtils;
import jb.selfregulation.impl.functopt.problem.Function;

/**
 * Type: ViewFunction<br/>
 * Date: 20/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ViewFunction extends Function
{
    protected double [] centroid;
    protected double score;
    protected Function function;
    protected double [][] view;
    
    public ViewFunction(Function f, double [][] aView, double [] aCentroid, double aScore)
    {
        score = aScore;
        centroid = aCentroid;
        function = f;
        view = aView;
    }
    
    

    public double getScore()
    {
        return score;
    }



    public void setScore(double score)
    {
        this.score = score;
    }



    @Override
    public double[][] getGenotypeMinMax()
    {
        return view;
    }

    public double [] getCentroid()
    {
        return centroid;
    }

    @Override
    public double evaluate(double[] v)
    {
        return function.evaluate(v);
    }

    @Override
    public double[] getBestCoord()
    {  
        return function.getBestCoord();
    }

    @Override
    public double getBestFitness()
    {
        return function.getBestFitness();
    }

    @Override
    public boolean isMinimisation()
    {
        return function.isMinimisation();
    }

    @Override
    public boolean supportsDynamic()
    {
        return function.supportsDynamic();
    }

    @Override
    public boolean supportsJitter()
    {
        return function.supportsJitter();
    }    
    
    
    
    
    public int getTotalBits()
    {
        return function.getTotalBits();
    }
    
    public double [] calculateGenotype(boolean[] aBitString)
    {
//        return function.calculateGenotype(aBitString);
        // must use this version to get correct values
        return toGenotype(aBitString, getGenotypeMinMax(), function.getNumDimensions());
    }

    public int getBitsPerVariate()
    {
        return function.getBitsPerVariate();
    }

    public int getNumDimensions()
    {
        return function.getNumDimensions();
    }
    
    
    public double jitterVariate(int dimension, double value)
    {
        return function.jitterVariate(dimension, value);
    }
    
    public double getJitterPercentage()
    {
        return function.getJitterPercentage();
    }
    public void setJitterPercentage(double jitterPercentage)
    {
        function.setJitterPercentage(jitterPercentage);
    }
    
    public void updateCyclePosition()
    {
        function.updateCyclePosition();       
    }


    public long getCycleLength()
    {
        return function.getCycleLength();
    }


    public void setCycleLength(long c)
    {
        function.setCycleLength(c);
    }

    public double getAlpha()
    {
        return function.getAlpha();
    }

    public void setAlpha(double alpha)
    {
        function.setAlpha(alpha);
    }

    public long getInternalCyclePosition()
    {
        return function.getInternalCyclePosition();
    }

    public void setInternalCyclePosition(long internalCyclePosition)
    {
        function.setInternalCyclePosition(internalCyclePosition);
    }

    public boolean isInverted()
    {
        return function.isInverted();
    }

    public void setInverted(boolean inverted)
    {
        function.setInverted(inverted);
    }

    public Random getRand()
    {
        return function.getRand();
    }

    public void setRand(Random rand)
    {
        function.setRand(rand);
    }

    public void setBitsPerVariate(int bitsPerVariate)
    {
        function.setBitsPerVariate(bitsPerVariate);
    }

    public void setNumDimensions(int numDimensions)
    {
        function.setNumDimensions(numDimensions);
    }
}
