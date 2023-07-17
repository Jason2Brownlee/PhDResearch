
package jb.selfregulation.processes.work;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.expansion.selection.SelectionStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.processes.ProcessWork;

/**
 * Type: ProcessExpansion<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessExpansion extends ProcessWork
    implements CellVisitor
{
    protected Random rand; 
    
    protected int numPartitions;
    protected int partitionAllocation;

    protected volatile StimulationStrategy stimulation;
    protected volatile SelectionStrategy selection;
    protected volatile ProgenyStrategy progeny;
    
    
    public String getBase()
    {
        return ".expansion";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        // load configuration
        String b = aBase + getBase();
        numPartitions = Integer.parseInt(prop.getProperty(b + ".partitions.total"));
        partitionAllocation = Integer.parseInt(prop.getProperty(b + ".partitions.allocation"));
        try
        {
            // create classes
            stimulation = (StimulationStrategy) Class.forName(prop.getProperty(b + ".stimulation.classname")).newInstance();
            selection = (SelectionStrategy) Class.forName(prop.getProperty(b + ".selection.classname")).newInstance();
            progeny = (ProgenyStrategy) Class.forName(prop.getProperty(b + ".proliforation.classname")).newInstance();
            // load configuration
            stimulation.loadConfig(b, prop);
            selection.loadConfig(b, prop);
            progeny.loadConfig(b, prop);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to prepare expansion.", e);
        }
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        
        rand = aState.rand;
        stimulation.setup(aState);
        selection.setup(aState);
        progeny.setup(aState);
        
        // add this stimulation to the set of stimulators
        ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).put(stimulation.getId(), this);
    }
    
    
    
    
    
    protected Cell selectCell(Lattice aLattice)
    {        
        LinkedList<Cell> cells = aLattice.getCells();
        int s = -1;
        
        // determine the partition size
        int partitionSize = (cells.size() / numPartitions);
        
        // check that a choice can be made
        if(cells.size() > 1 && partitionSize > 0)
        {
            // make a selection in the range
            s = rand.nextInt(partitionSize);
            // rescale the selection into the correct partition
            s += (partitionAllocation * partitionSize);
        }
        else
        {
            s = 0;
        }
        
        Cell cell = cells.get(s);
        return cell;
    }    
  
    protected void executeProcessRun(Lattice aLattice)
    {              
        // select a cell to operate on
        Cell selectedCell = selectCell(aLattice);
        // visit the cell and do the expansion
        selectedCell.visitCell(this, Constants.LOCK_WAIT_TIME);
        
        // update count on the cell
        selectedCell.updateStimulationCount();
    }

    public void visit(Cell aCell)
    {
        // ensure the cell is not a portal
//        if(aCell.isPortal())
//        {
//            return;
//        }
//        // check if the cell has no units
//        else 
            if(aCell.getTail().getUnits().isEmpty())
        {
            return;
        }
        
        // perform the expansion
        performExpansion(aCell);
    } 
    
    public void performExpansion(Cell aCell)
    {
        // perform stimulation
        stimulation.stimulate(aCell);
        // perform selection
        LinkedList<Unit> selectedBest = selection.select(stimulation, aCell);
        // perform proliforation
        int total = progeny.progeny(aCell, selectedBest, stimulation);
        // update statistics
        updateStatistics(aCell.getTail().getUnits().size(), selectedBest.size(), total);
    }

    protected void updateStatistics(
            int totalEvaluated, 
            int totalSelected, 
            int totalProgeny)    
    {
        ((FeedbackRunStats)lastRunStats).totalEvaluations += totalEvaluated;
        ((FeedbackRunStats)lastRunStats).totalSelections += totalSelected;
        ((FeedbackRunStats)lastRunStats).totalProgeny += totalProgeny;
    }
    
    
    protected LastRunStats prepareLastRunStats()
    {
        return new FeedbackRunStats();
    }
    
    protected class FeedbackRunStats extends LastRunStats
    {
        protected int totalSelections;
        protected int totalEvaluations;
        protected int totalProgeny;
        
        public void rest()
        {
            totalSelections = 0;
            totalEvaluations = 0;
            totalProgeny = 0;
        }
    }
    
    
    public int getTotalEvaluationsLastRun()
    {
        return ((FeedbackRunStats)lastRunStats).totalEvaluations;
    }
    public int getTotalSelectionsLastRun()
    {
        return ((FeedbackRunStats)lastRunStats).totalSelections;
    }
    public int getTotalProgenyLastRun()
    {
        return ((FeedbackRunStats)lastRunStats).totalProgeny;
    }

    
    public void setPartitionDetails(int aNumPartitions, int aAllocation)
    {
        numPartitions = aNumPartitions;
        partitionAllocation = aAllocation;
    }

    
    public int getNumPartitions()
    {
        return numPartitions;
    }
    public int getPartitionAllocation()
    {
        return partitionAllocation;
    }


    public ProgenyStrategy getProgeny()
    {
        return progeny;
    }


    public Random getRand()
    {
        return rand;
    }


    public SelectionStrategy getSelection()
    {
        return selection;
    }


    public StimulationStrategy getStimulation()
    {
        return stimulation;
    }
    
    public void setSelectionStrategy(SelectionStrategy s)
    {
        selection = s;
    }
    public void setProgenyStrategy(ProgenyStrategy s)
    {
        progeny = s;
    }
    
}
