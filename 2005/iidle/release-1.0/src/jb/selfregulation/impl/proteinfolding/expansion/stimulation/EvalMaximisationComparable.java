
package jb.selfregulation.impl.proteinfolding.expansion.stimulation;

import java.util.Comparator;

import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;

/**
 * Type: EvalMaximisationComparable<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class EvalMaximisationComparable implements Comparator<ProteinFoldingUnit>
{
    /**
     * 
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * 
     * Sorts decending
     * 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(ProteinFoldingUnit o1, ProteinFoldingUnit o2)
    {
        if(o1.getScore() < o2.getScore())
        {
            return -1;
        }
        else if(o1.getScore() > o2.getScore())
        {
            return +1;
        }
        
        return 0;
    }


}
