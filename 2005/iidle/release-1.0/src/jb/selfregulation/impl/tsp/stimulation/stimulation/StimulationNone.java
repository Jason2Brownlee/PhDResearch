
package jb.selfregulation.impl.tsp.stimulation.stimulation;

import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.tsp.stimulation.TourComparable;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * Type: StimulationNone<br/>
 * Date: 15/07/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationNone extends StimulationStrategy
{
//    public StimulationNone(long aId)
//    {
//        super(aId, true);
//    }       
    
    protected boolean getIsMinimisation()
    {
        return true;
    }
    
    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {} // do nothing

    @Override
    public String getName()
    {
        return "No Stimulation";
    }
    
    @Override
    public double calculateMeanScore(Cell aCell)
    {
        return Double.NaN;
    }

    @Override
    protected Comparator prepareComparator()
    {        
//        return new TourComparable(); // not needed
        throw new RuntimeException("Unspported");
    }

    @Override
    public boolean isBetter(Unit a, Unit b)
    {        
//        return false; // no reordering
        
        throw new RuntimeException("Unspported");
    }

}
