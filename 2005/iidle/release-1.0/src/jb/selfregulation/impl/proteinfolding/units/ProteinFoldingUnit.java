
package jb.selfregulation.impl.proteinfolding.units;

import jb.selfregulation.Unit;

/**
 * Type: ProteinFoldingUnit<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProteinFoldingUnit extends Unit
{
    protected byte [] model;
    protected float score;
    
    public ProteinFoldingUnit(byte [] b)
    {
        model = b;
    }
    
    public String toString()
    {
        return "HP Model ["+score+"]";
    }
    
    public byte [] getModel()
    {
        return model;
    }

    public float getScore()
    {
        return score;
    }

    public void setScore(float score)
    {
        this.score = score;
    }
}
