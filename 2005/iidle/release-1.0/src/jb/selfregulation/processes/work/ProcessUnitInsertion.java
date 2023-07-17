
package jb.selfregulation.processes.work;

import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Tail;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ProcessWork;

/**
 * Type: ProcessUnitInsertion<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessUnitInsertion extends ProcessWork 
    implements CellVisitor
{
    protected UnitFactory unitFactory;    
    protected Random rand;
    

    

    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup this work
        unitFactory = aState.unitFactory;
        rand = aState.rand;
    }
    
    
    
    
    
    
   
    public void visit(Cell aCell)
    {
        // check that the cell's tail is not full
        Tail tail = aCell.getTail();
        if(!tail.isFull())
        {
            Unit newUnit = unitFactory.generateNewUnit();
            tail.addUnit(newUnit);
            ((NewUnitRunStats)lastRunStats).totalInsertsLastRun++;
        }
    }
    
    protected void executeProcessRun(Lattice aLattice)
    {
        // select a random cell 
        Cell cell = aLattice.selectRandomCell(rand);
        // perform an insert
        cell.visitCell(this, Constants.LOCK_WAIT_TIME);
    }
    
    protected LastRunStats prepareLastRunStats()
    {
        return new NewUnitRunStats();
    }
    
    protected class NewUnitRunStats extends LastRunStats
    {
        protected int totalInsertsLastRun;
        
        public void rest()
        {
            totalInsertsLastRun = 0;
        }
    }
    
    public int getInsertionsLastRun()
    {
        return ((NewUnitRunStats)lastRunStats).totalInsertsLastRun;
    }
}
