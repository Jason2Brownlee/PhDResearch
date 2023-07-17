
package jb.selfregulation.processes.work;

import java.util.LinkedList;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Tail;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.network.PortalIncoming;
import jb.selfregulation.processes.ProcessWork;




/**
 * Type: PortalInboundProcess<br/>
 * Date: 2/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessPortalInbound extends ProcessWork implements CellVisitor
{
    protected final static int MAX_DELIVER_ATTEMPTS = 10;
    
    protected PortalIncoming portalIncoming;
    protected Random rand;    
    protected LinkedList<Unit> tmpAllUnits;
    protected int unitsPerPortal;
    
  
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        portalIncoming = aState.p2pNetwork;
        rand = aState.rand;
    }

    @Override
    protected void executeProcessRun(Lattice aLattice)
    {
        // no race condition, if there is some, they remain until this process removes them.
        if(portalIncoming.isUnitsIncoming())
        {
            // get all units
            tmpAllUnits = portalIncoming.getAllUnitsIncomming();
            // update stats
            ((PortalInboundProcessRunStats)lastRunStats).totalUnitsReceived += tmpAllUnits.size();            
            // process all units
            LinkedList<Cell> allPortals = aLattice.getDuplicatePortalList();
            if(allPortals.isEmpty())
            {
                // clear all data - if no portals, data is lost
                tmpAllUnits.clear();
            }
            else
            {
                // divide all goodies up between the portals
                dispenseAllUnits(allPortals);
            }   
        }
    }
    
    public void dispenseAllUnits(LinkedList<Cell> allPortals)
    {   
        // determine the division
        unitsPerPortal = tmpAllUnits.size() / allPortals.size();
        
        int attempts = 0;
        int max = allPortals.size() + MAX_DELIVER_ATTEMPTS;
        
        // run until everything is gone or too many attempts have been made
        while(attempts++<max && !tmpAllUnits.isEmpty())
        {
            // select a random portal
            int s = rand.nextInt(allPortals.size());
            Cell selectedPortal = allPortals.get(s);
            
            // do not add to neighbours, just add to portal
            if(!selectedPortal.getTail().isFull())
            {
                selectedPortal.visitCell(this, Constants.LOCK_WAIT_TIME);
            }
            
            /*
            // select a neighbour
            int n  = rand.nextInt(portal.getNeighbours().size());
            Cell winner = portal.getNeighbours().get(n);
            if(!winner.getTail().isFull())
            {
                winner.visitCell(this, Constants.LOCK_WAIT_TIME);
            }
            */
        }
        
        // not, if the total attempts is exceeded, then the data will remain for next time
        // if next time there are no portals, data will of course be lost
    }
    
    public void visit(Cell aCell)
    {
        Tail tail = aCell.getTail();
        
        for (int i = 0; !tail.isFull() && !tmpAllUnits.isEmpty() && i < unitsPerPortal; i++)
        {
            Unit u = tmpAllUnits.removeFirst();            
            // top up the energy
            u.setEnergy(Unit.MAX_ENERGY);
            // mark as visitor
            u.setVisitor(true);
            // add to the tail
            tail.addUnit(u);
//            System.out.println(((ProteinFoldingUnit)u).getScore());
        }
    }    
    
    public int getTotalUnitsReceived()
    {
        return ((PortalInboundProcessRunStats)lastRunStats).totalUnitsReceived;
    }
    public long getLastRunStamp()
    {
        return ((PortalInboundProcessRunStats)lastRunStats).lastRunStamp;
    }
    
    
    @Override
    protected LastRunStats prepareLastRunStats()
    {
        return new PortalInboundProcessRunStats();
    }    
    protected class PortalInboundProcessRunStats extends LastRunStats
    {
        protected int totalUnitsReceived;
        protected long lastRunStamp;
        
        @Override
        public void rest()
        {
            totalUnitsReceived = 0;
            lastRunStamp = System.currentTimeMillis();
        }        
    }
}
