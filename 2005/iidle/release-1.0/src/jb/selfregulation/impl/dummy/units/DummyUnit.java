
package jb.selfregulation.impl.dummy.units;

import jb.selfregulation.Unit;

/**
 * Type: DummyUnit<br/>
 * Date: 17/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DummyUnit extends Unit
{
    protected double fitness;
    protected boolean isSpecial;
    
    protected long createdId;
    

    public double getFitness()
    {
        return fitness;
    }

    public void setFitness(double fitness)
    {
        this.fitness = fitness;
    }

    public boolean isSpecial()
    {
        return isSpecial;
    }

    public void setSpecial(boolean isSpecial)
    {
        this.isSpecial = isSpecial;
    }

    public long getCreatedId()
    {
        return createdId;
    }

    public void setCreatedId(long createdId)
    {
        this.createdId = createdId;
    }
    
    
}
