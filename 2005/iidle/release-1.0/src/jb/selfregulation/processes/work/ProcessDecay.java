
package jb.selfregulation.processes.work;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Tail;
import jb.selfregulation.Unit;
import jb.selfregulation.processes.ProcessWork;

/**
 * Type: SimpleDecayProcess<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessDecay extends ProcessWork implements CellVisitor
{
    protected volatile boolean automatic;
    protected volatile int idealEnergy;
    protected volatile double decayAmount;
    
    public ProcessDecay()
    {}

    public String getBase()
    {
        return ".decay";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop); 
        decayAmount = Double.parseDouble(prop.getProperty(aBase + getBase() + ".amount"));
        automatic = Boolean.parseBoolean(prop.getProperty(aBase + getBase() + ".automatic"));
        idealEnergy = Integer.parseInt(prop.getProperty(aBase + getBase() + ".idealenergy"));
    }
    
    protected void updateDecayAmount(Lattice aLattice)
    {
        if(automatic)
        {
            // determine system energy
            double systemEnergy = aLattice.getRoughSystemEnergy();
            // system state
            int [] state = aLattice.getRoughSystemState();
            // check if change is required
            if(systemEnergy > idealEnergy)
            {
                // decay enough to reach ideal
                double diff = systemEnergy - idealEnergy;
                decayAmount = diff / state[0]; 
            }
            else
            {
                decayAmount = 0.0;
            }
        }
    }
    
    
    
    protected LastRunStats prepareLastRunStats()
    {
        return new DeacyLastRunStats();
    }
    
    
    public void visit(Cell aCell)
    {
        Tail tail = aCell.getTail();        
        
        for (Iterator<Unit> iter = tail.getUnits().iterator(); iter.hasNext();)
        {
            Unit unit = iter.next();
            if(unit.decay(decayAmount))
            {
                iter.remove(); // remove the unit 
                ((DeacyLastRunStats)lastRunStats).lastDecayTotalRemoved++;
            }                
        }       
    }
    
    protected void executeProcessRun(Lattice aLattice)
    {   
        // update decay amount
        updateDecayAmount(aLattice);
        
        if(decayAmount == 0)
        {
            return; // do nothing
        }
        
        // duplicate the list        
        LinkedList<Cell> allCells = aLattice.getDuplicateCellList();
        
        // process the temp list of cells
        for(Cell cell : allCells)
        {
            // decay all units in this cell
            cell.visitCell(this, Constants.LOCK_WAIT_TIME);
        }        
    }
    
    
    protected class DeacyLastRunStats extends LastRunStats
    {
        protected int lastDecayTotalRemoved;
        
        public void rest()
        {
            lastDecayTotalRemoved = 0;
        }
    }
    
    
    public int getLastDecayTotalRemoved()
    {
        return ((DeacyLastRunStats)lastRunStats).lastDecayTotalRemoved;
    }

    /**
     * @return Returns the decayAmount.
     */
    public double getDecayAmount()
    {
        return decayAmount;
    }
    /**
     * @param decayAmount The decayAmount to set.
     */
    public void setDecayAmount(double decayAmount)
    {
        this.decayAmount = decayAmount;
    }


    public boolean isAutomatic()
    {
        return automatic;
    }


    public void setAutomatic(boolean automatic)
    {
        this.automatic = automatic;
    }


    public int getIdealEnergy()
    {
        return idealEnergy;
    }


    public void setIdealEnergy(int idealEnergy)
    {
        this.idealEnergy = idealEnergy;
    }


    
    
    
}
