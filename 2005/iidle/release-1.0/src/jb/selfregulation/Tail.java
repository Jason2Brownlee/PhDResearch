
package jb.selfregulation;

import java.util.LinkedList;

/**
 * Type: Tail<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Tail
{        
    protected final LinkedList<Unit> units;    
    protected int maxTailLength;

      

    /**
     * 
     */
    public Tail(int aMaxTailLength)
    {
        units = new LinkedList<Unit>();
        maxTailLength = aMaxTailLength;
    }
    
    
    
    public boolean isFull()
    {
        return units.size() >= maxTailLength;
    }
    

    public void addUnit(Unit aUnit)
    {
        if(isFull())
        {
            throw new RuntimeException("Unable to add unit, tail is full. Size["+units.size()+"]");
        }
        
        units.add(aUnit);
    }
    
    
    public Unit removeUnit(int index)
    {
        return units.remove(index);
    }
    
    /**
     * @return Returns the units.
     */
    public LinkedList<Unit> getUnits()
    {
        return units;
    }


    public int getMaxTailLength()
    {
        return maxTailLength;
    }


    public void setMaxTailLength(int maxTailLength)
    {
        this.maxTailLength = maxTailLength;
    }       
}
