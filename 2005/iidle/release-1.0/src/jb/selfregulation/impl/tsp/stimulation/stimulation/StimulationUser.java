
package jb.selfregulation.impl.tsp.stimulation.stimulation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.tsp.drawing.TSPFeedbackPanel;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.stimulation.UnitComparatorUserStimulation;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * Type: VisualFeedbackFactory<br/>
 * Date: 7/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationUser extends StimulationStrategy
{        
    protected final static String NAME = "User";
    
    protected final TSPFeedbackPanel visualFeedback;
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
            if(tspUnit.isHasUserStimulation())
            {
                meanScore += tspUnit.getUserStimulation();
                count++;
            }
        }
        
        if(count == 0.0)
        {
            return Double.NaN;
        }
        
        return meanScore / count;
    }
        
    public StimulationUser()
    {
        visualFeedback = new TSPFeedbackPanel();
    }
    
    
    public String getBase()
    {
        return super.getBase() + ".user";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + getBase();
        // load the display
        visualFeedback.loadConfig(b, prop);
    }
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (TSPProblem) aState.problem;
        // prepare interface for stimulation
        visualFeedback.setup(aState);
    }
    
    protected boolean getIsMinimisation()
    {
        return false; // maximise user feedback!
    }
    
    
    protected void stimulateUnits(Cell aCell, LinkedList<Unit> aUnits)
    {        
        // do the user selection thing
        visualFeedback.runFeedbackCycle(aCell);
        // do the normal thing
        super.stimulateUnits(aCell, aUnits);
    }

    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {}
    
    public String getName()
    {
        return NAME;
    }
    
    public boolean isBetter(Unit a, Unit b)
    {
        TSPUnit a1 = (TSPUnit) a;
        TSPUnit b1 = (TSPUnit) b;
        
        return a1.getUserStimulation() > b1.getUserStimulation();
    }
    
    public Comparator prepareComparator()
    {
        return new UnitComparatorUserStimulation();
    }
}
