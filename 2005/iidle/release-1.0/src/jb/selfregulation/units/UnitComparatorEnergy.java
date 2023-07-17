
package jb.selfregulation.units;

import java.util.Comparator;

import jb.selfregulation.Unit;

public class UnitComparatorEnergy implements Comparator<Unit>
{
    /**
     * 
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(Unit o1, Unit o2)
    {
        if(o1.getEnergy() < o2.getEnergy())
        {
            return -1;
        }
        else if(o1.getEnergy() > o2.getEnergy())
        {
            return +1;
        }
        
        return 0;
    }

}
