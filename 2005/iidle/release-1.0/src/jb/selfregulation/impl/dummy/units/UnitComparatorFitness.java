
package jb.selfregulation.impl.dummy.units;

import java.util.Comparator;

import jb.selfregulation.Unit;

/**
 * Type: UnitComparatorFitness<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class UnitComparatorFitness implements Comparator<Unit>
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
        DummyUnit d1 = (DummyUnit) o1; 
        DummyUnit d2 = (DummyUnit) o2;
        
        if(d1.getFitness() < d2.getFitness())
        {
            return -1;
        }
        else if(d1.getFitness() > d2.getFitness())
        {
            return +1;
        }
        
        return 0;
    }

}
