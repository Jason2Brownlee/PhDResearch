
package jb.selfregulation.processes.work;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.CellVisitor;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.network.PortalOutgoing;
import jb.selfregulation.processes.ProcessWork;

/**
 * Type: PortalOutboundProcess<br/>
 * Date: 2/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessPortalOutbound extends ProcessWork 
    implements CellVisitor
{
    protected PortalOutgoing portalOutgoing;
    protected final LinkedList<Unit> tmpAllUnits;
    
    protected boolean limited;
    protected volatile int total;
    protected Random rand;
    
    
    @Override
    public String getBase()
    {
        return ".outbound";
    }
    @Override
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + getBase();
        limited = Boolean.parseBoolean(prop.getProperty(b + ".limited"));
        total = Integer.parseInt(prop.getProperty(b + ".total"));        
    }
    
    public ProcessPortalOutbound()
    {
        tmpAllUnits = new LinkedList<Unit>();
    }
    
    @Override
    public void setup(SystemState aState)
    {
        super.setup(aState);
        portalOutgoing = aState.p2pNetwork;
        tmpAllUnits.clear();
        rand = aState.rand;
    }   
    

    @Override
    protected void executeProcessRun(Lattice aLattice)
    {
        // get a copy of the portal list
        LinkedList<Cell> allPortals = aLattice.getDuplicatePortalList();
        
        // process the list, visiting each portal in turn
        for(Cell portal : allPortals)
        {
            // copy out all units
            portal.visitCell(this, Constants.LOCK_WAIT_TIME);
            
            if(!tmpAllUnits.isEmpty())
            {
                // send units into the universe
                portalOutgoing.sendUnitsToRandomNeighbour(tmpAllUnits);
                // update stats
                ((PortalOutboundProcessRunStats)lastRunStats).totalUnitsSent += tmpAllUnits.size();
                // clear the units
                tmpAllUnits.clear();
            }
        }
    }    
    
    public void visit(Cell aCell)
    {
        LinkedList<Unit> units = aCell.getTail().getUnits();
        
        // still a portal and has units
        if(aCell.isPortal() && !units.isEmpty())
        {
            Collections.shuffle(units, rand);
            
            // process the list searching for all non visitor units
            for (Iterator<Unit> iter = units.iterator(); iter.hasNext();)
            {
                if(limited && tmpAllUnits.size()>=total)
                {
                    break; // stop
                }
                
                Unit u = iter.next();
                // check if the unit can be taken
                if(!u.isVisitor())
                {
                    tmpAllUnits.add(u);
                    iter.remove();
                }
            }
        }
    }
    
    
    public int getTotalUnitsSent()
    {
        return ((PortalOutboundProcessRunStats)lastRunStats).totalUnitsSent;
    }
    public long getLastRunStamp()
    {
        return ((PortalOutboundProcessRunStats)lastRunStats).lastRunStamp;
    }
    
    @Override
    protected LastRunStats prepareLastRunStats()
    {
        return new PortalOutboundProcessRunStats();
    }    
    protected class PortalOutboundProcessRunStats extends LastRunStats
    {
        protected int totalUnitsSent;
        protected long lastRunStamp;
        
        @Override
        public void rest()
        {
            totalUnitsSent = 0;
            lastRunStamp = System.currentTimeMillis();
        }        
    }
    public boolean isLimited()
    {
        return limited;
    }

    public void setLimited(boolean limited)
    {
        this.limited = limited;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }    
}
