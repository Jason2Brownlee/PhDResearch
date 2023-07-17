
package jb.selfregulation.processes.work;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ProcessWork;

/**
 * Type: SimpleMovementProcess<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessMovement extends ProcessWork
{
    protected final CutVisitor cutVisitor;
    protected final PasteVisitor pasteVisitor;
    
    protected Random rand;   
    protected Lattice lattice;
    protected HashMap<Long, ProcessExpansion> expansions;
    
    protected volatile boolean automatic;
    protected volatile double probability;
    
    

    /**
     * 
     */
    public ProcessMovement()
    {
        cutVisitor = new CutVisitor();
        pasteVisitor = new PasteVisitor();
    }
    
    
    public String getBase()
    {
        return ".movement";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        // load movement
        probability = Double.parseDouble(prop.getProperty(aBase + getBase() + ".probability"));
        automatic = Boolean.parseBoolean(prop.getProperty(aBase + getBase() + ".automatic"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
        lattice = aState.lattice;
        expansions = (HashMap<Long, ProcessExpansion>) aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION);
    }

    
    protected void prepareMovementProbability()
    {
        if(automatic)
        {
            // count total expected stimulations
            int stimulationCount = 0;            
            for(ProcessExpansion p : expansions.values())
            {
                stimulationCount += p.getNumProcessIterations();
            }            
            // calculate the probability of being stimulated
            double stimProb = ((double)stimulationCount / (double)lattice.getInitialTotalCells());
            // set the probability of movement to the inverse of being selected
            // the more stimulation the less movement
            // the less stimulation the more movement 
            probability = 1.0 - stimProb;
        }            
    }
    
  
    protected void executeProcessRun(Lattice aLattice)
    {
        // prepare the movement probability
        prepareMovementProbability();
        
        if(probability == 0)
        {
            return;
        }
        
        LinkedList<Cell> allCells = aLattice.getDuplicateCellList();
        for(Cell c : allCells)
        {
            if(probability>=1 || rand.nextDouble() < probability)
            {
                // perform the cut
                c.visitCell(cutVisitor, Constants.LOCK_WAIT_TIME);
                // ensure cut was successful
                if(cutVisitor.cutWasSuccesful)
                {
                    // perform the paste 
                    // there is a chance that the paste will fail and the unit will be lost
                    cutVisitor.selectedNeighbour.visitCell(pasteVisitor, Constants.LOCK_WAIT_TIME*2);
                }
            }
        }
    }
    
    
    protected class CutVisitor implements CellVisitor
    {
        protected Unit unitCut;
        protected Cell selectedNeighbour;
        protected boolean cutWasSuccesful;
        
        public void visit(Cell aCell)
        {
            // ensure the cell has units
            if(aCell.getTail().getUnits().isEmpty())
            {
                cutWasSuccesful = false;
                return;
            }
            // ensure the cell has neighbours
            else if(aCell.getNeighbours().isEmpty())
            {
                cutWasSuccesful = false;
                return;
            }
            
            // select a unit to cut
            int selectedUnit = selectUnit(aCell);
            unitCut = aCell.getTail().removeUnit(selectedUnit);
            // select a neighbour to paste it in
            int index = selectNeighbour(aCell);
            
            // check for failure
            if(index < 0)
            {
                selectedNeighbour = null;
                cutWasSuccesful = false;
            }
            // otherwise all went well
            else
            {            
                selectedNeighbour = aCell.getNeighbours().get(index);
                cutWasSuccesful = true;
            }
        }
    }
    
    protected int selectUnit(Cell aCell)
    {
        // very simple random unit selection
        return rand.nextInt(aCell.getTail().getUnits().size());
    }
    
    protected int selectNeighbour(Cell aCell)
    {
        // very simple random selection
        // can be replaced with more advanced approaches
        return rand.nextInt(aCell.getNeighbours().size());
    }
    
    
    
    
    protected class PasteVisitor implements CellVisitor
    {
        public void visit(Cell aCell)
        {
            // check that the tail is not full
            if(aCell.getTail().isFull())
            {
                return;
            }
            // add the unit to the tail
            aCell.getTail().addUnit(cutVisitor.unitCut);
            // update statistics
            ((MovementLastRunStats)lastRunStats).totalMovementsLastRun++;
        }
    }
    
    
    protected LastRunStats prepareLastRunStats()
    {
        return new MovementLastRunStats();
    }
    
    protected class MovementLastRunStats extends LastRunStats
    {
        protected int totalMovementsLastRun;
        
        public void rest()
        {
            totalMovementsLastRun = 0;
        }
    }
    
    public int getTotalMovementsLastRun()
    {
        return ((MovementLastRunStats)lastRunStats).totalMovementsLastRun;
    }


    public double getProbability()
    {
        return probability;
    }


    public void setProbability(double probability)
    {
        this.probability = probability;
    }
    
    
}
