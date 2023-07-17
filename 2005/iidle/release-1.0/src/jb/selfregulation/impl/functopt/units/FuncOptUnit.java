
package jb.selfregulation.impl.functopt.units;

import jb.selfregulation.Unit;

/**
 * Type: FuncOptUnit<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FuncOptUnit extends Unit
{
    protected boolean [] bitString;
    protected double [] vectorData;
    
    // HACK: for PSO TODO - extend this and do it proper!
    protected double [] velocity;    
    
    protected double [] personalBestPos;
    protected boolean hasPersonalBest;
    protected double personalBest;
    
    protected double functionEvaluation;    
    protected boolean hasFunctionEvaluation;
    
    
//    public void easySetInternalData(boolean [] b)
//    {
//        bitString = b;
//    }
    
    
    
    

    public double[] getVelocity()
    {
        return velocity;
    }

    public void setVelocity(double[] velocity)
    {
        this.velocity = velocity;
    }

    public void setBitString(boolean[] bitString)
    {
        this.bitString = bitString;
    }

    public double[] getVectorData()
    {
        return vectorData;
    }

    public void setVectorData(double[] vectorData)
    {
        this.vectorData = vectorData;
    }

    public boolean[] getBitString()
    {
        return bitString;
    }

    public double getFunctionEvaluation()
    {
        return functionEvaluation;
    }

    public void setFunctionEvaluation(double functionEvaluation)
    {
        this.functionEvaluation = functionEvaluation;
    }

    public boolean isHasFunctionEvaluation()
    {
        return hasFunctionEvaluation;
    }

    public void setHasFunctionEvaluation(boolean hasFunctionEvaluation)
    {
        this.hasFunctionEvaluation = hasFunctionEvaluation;
    }

    public boolean isHasPersonalBest()
    {
        return hasPersonalBest;
    }

    public void setHasPersonalBest(boolean hasPersonalBest)
    {
        this.hasPersonalBest = hasPersonalBest;
    }

    public double getPersonalBest()
    {
        return personalBest;
    }

    public void setPersonalBest(double personalBest)
    {
        this.personalBest = personalBest;
    }

    public double[] getPersonalBestPos()
    {
        return personalBestPos;
    }

    public void setPersonalBestPos(double[] personalBestPos)
    {
        this.personalBestPos = personalBestPos;
    }
}
