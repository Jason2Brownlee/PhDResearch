
package jb.selfregulation.impl.tsp.stimulation.stimulation;

import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.stimulation.TourComparable;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * 
 * Type: StimulationTourLength<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationTourLength extends StimulationStrategy
{
    protected final static String NAME = "Tour Length";
    protected TSPProblem problem;    
    
    
    @Override
    public double calculateMeanScore(Cell aCell)
    {
        LinkedList<Unit> list = aCell.getDuplicateTailList();
        double meanScore = 0.0;
        double count = 0;
        
        for(Unit u : list)
        {
            TSPUnit tspUnit = (TSPUnit) u;
            if(tspUnit.isHasTourLength())
            {
                meanScore += tspUnit.getTourLength();
                count++;
            }
        }
        
        if(count == 0.0)
        {
            return Double.NaN;
        }
        
        return meanScore / count;
    }

    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup
        problem = (TSPProblem) aState.problem;
    }
    
    
    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        TSPUnit u = (TSPUnit) aUnit;
        
        // only calculate once
        if(u.isHasTourLength())
        {
            return;
        }
        
        // calculate and storestore
        u.setTourLength(problem.calculateTourLength(u.getData()));
        u.setHasTourLength(true);        
    }
    public String getName()
    {
        return NAME;
    }
    
    public boolean isBetter(Unit a, Unit b)
    {
        TSPUnit a1 = (TSPUnit) a;
        TSPUnit b1 = (TSPUnit) b;
        
        return a1.getTourLength() < b1.getTourLength();
    }
    
    public Comparator prepareComparator()
    {
        return new TourComparable();
    }
    
    protected boolean getIsMinimisation()
    {
        return true;
    }
}
