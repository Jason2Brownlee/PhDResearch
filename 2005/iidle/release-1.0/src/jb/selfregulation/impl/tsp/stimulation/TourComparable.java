
package jb.selfregulation.impl.tsp.stimulation;

import java.util.Comparator;

import jb.selfregulation.impl.tsp.units.TSPUnit;

/**
 * Type: TourComparable<br/>
 * Date: 5/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TourComparable implements Comparator<TSPUnit>
{
    /**
     * 
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * 
     * Sort ascending
     * 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(TSPUnit o1, TSPUnit o2)
    {
        if(o1.getTourLength() < o2.getTourLength())
        {
            return +1;
        }
        else if(o1.getTourLength() > o2.getTourLength())
        {
            return -1;
        }
        
        return 0;
    }


}
