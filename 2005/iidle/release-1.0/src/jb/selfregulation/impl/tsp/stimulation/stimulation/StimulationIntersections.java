
package jb.selfregulation.impl.tsp.stimulation.stimulation;

import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.stimulation.CrossesComparable;
import jb.selfregulation.impl.tsp.units.TSPUnit;



/**
 * 
 * Type: StimulationIntersections<br/>
 * Date: 5/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationIntersections extends StimulationStrategy
{
    protected final static String NAME = "Intersections";
    protected TSPProblem problem;
    
//    public StimulationIntersections(long aId, TSPProblem aProblem)
//    {
//        super(aId, true);
//        problem = aProblem;
//    }   
    
    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup
        problem = (TSPProblem) aState.problem;
    }
    
    protected boolean getIsMinimisation()
    {
        return true;
    }
    
    
    

    @Override
    public double calculateMeanScore(Cell aCell)
    {
        LinkedList<Unit> list = aCell.getDuplicateTailList();
        double meanScore = 0.0;
        double count = 0;
        
        for(Unit u : list)
        {
            TSPUnit tspUnit = (TSPUnit) u;
            if(tspUnit.isHasTotalCrosses())
            {
                meanScore += tspUnit.getTotalCrosses();
                count++;
            }
        }
        
        if(count == 0.0)
        {
            return Double.NaN;
        }
        
        return meanScore / count;
    }
    

    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        TSPUnit u = (TSPUnit) aUnit;
        
        // only calculate once
        if(u.isHasTotalCrosses())
        {
            return;
        }       
        
        // calculate and storestore
        u.setTotalCrosses(problem.calculateTotalCrossesInTour(u.getData()));
        u.setHasTotalCrosses(true);         
    }
    public String getName()
    {
        return NAME;
    }
    
    public boolean isBetter(Unit a, Unit b)
    {
        TSPUnit a1 = (TSPUnit) a;
        TSPUnit b1 = (TSPUnit) b;
        
        return a1.getTotalCrosses() < b1.getTotalCrosses();
    }
    
    public Comparator prepareComparator()
    {
        return new CrossesComparable();
    }
}
