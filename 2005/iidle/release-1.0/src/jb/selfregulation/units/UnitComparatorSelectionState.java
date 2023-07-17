
package jb.selfregulation.units;

import java.util.Comparator;

import jb.selfregulation.Unit;

public class UnitComparatorSelectionState implements Comparator<Unit>
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
        int first = getScore(o1);
        int second = getScore(o2);

        if(first < second)
        {
            return -1;
        }
        else if(first > second)
        {
            return +1;
        }
        
        return 0;
    }

    protected int getScore(Unit aUnit)
    {
        int score = 0;
        
        if(aUnit.isSelected())
        {
            score = 1;
        }
        else if(aUnit.isEvaluated())
        {
            score = 2;
        }       
        else
        {
            score = 3;
        }
        
        return score;
    }
}
