
package jb.selfregulation.impl.functopt.expansion.stimulation;

import java.util.Comparator;

import jb.selfregulation.impl.functopt.units.FuncOptUnit;

/**
 * Type: EvalMinimisationComparable<br/>
 * Date: 5/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class EvalMinimisationComparable implements Comparator<FuncOptUnit>
{
    /**
     * 
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * 
     * Sort's ascending 
     * 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(FuncOptUnit o1, FuncOptUnit o2)
    {        
        if(o1.getFunctionEvaluation() < o2.getFunctionEvaluation())
        {
            return +1;
        }
        else if(o1.getFunctionEvaluation() > o2.getFunctionEvaluation())
        {
            return -1;
        }
        
        return 0;
    }


}
