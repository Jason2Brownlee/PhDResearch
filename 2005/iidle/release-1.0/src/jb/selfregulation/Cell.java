
package jb.selfregulation;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Type: Cell<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Cell
{
    protected final LinkedList<Cell> neighbours;
    protected final Tail tail;
    protected final Semaphore lock;
    
    protected Long lastFeedbackId;    
    protected volatile boolean isPortal;
    protected long totalStimulations;
    

    /**
     * 
     */
    public Cell(Tail aTail)
    {
        lock = new Semaphore(1);
        neighbours = new LinkedList<Cell>();
        tail = aTail;
        isPortal = false;
    }    
    
    
    public void updateStimulationCount()
    {
        totalStimulations++;
    }
    public long getStimulationCount()
    {
        return totalStimulations;
    }
    
    public void addNeighbour(Cell aNeighbour)
    {
        neighbours.add(aNeighbour);
    }    

    /**
     * @return Returns the neighbours.
     */
    public LinkedList<Cell> getNeighbours()
    {
        return neighbours;
    }
    /**
     * @param neighbours The neighbours to set.
     */
    public void setNeighbours(LinkedList<Cell> n)
    {
        this.neighbours.addAll(n);
    }    
    
    
    public void visitCell(CellVisitor aVisitor)
    {
        getLock();
        aVisitor.visit(this);
        putLock();
    }
    public void visitCell(CellVisitor aVisitor, long aTimeout)
    {
        if(getLockTimeout(aTimeout))
        {
            aVisitor.visit(this);
            putLock();
        }
    }
    
    
    public LinkedList<Unit> getDuplicateTailList()
    {
        getLock();
        LinkedList<Unit> allUnits = new LinkedList<Unit>();
        allUnits.addAll(tail.getUnits());
        putLock();
        return allUnits;
    }
    
    public LinkedList<Unit> getDuplicateTailList(long aTimeout)
    {
        LinkedList<Unit> allUnits = new LinkedList<Unit>();
        
        if(getLockTimeout(aTimeout))
        {            
            allUnits.addAll(tail.getUnits());
            putLock();
            
        }
        return allUnits;
    }
    
    
    
    /**
     * @return Returns the tail.
     */
    public Tail getTail()
    {
        return tail;
    }
    
    public void clearNeighbours()
    {
        neighbours.clear();
    }
    
    private void getLock()
    {
        try
        {
            lock.acquire();
        }
        catch (InterruptedException e)
        {}
    }
    
    private boolean getLockTimeout(long aWaitTime)
    {
        try
        {
            return lock.tryAcquire(1, aWaitTime, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }
    
    private void putLock()
    {
        lock.release();
    }


    public boolean isPortal()
    {
        return isPortal;
    }
    public void enablePortal()
    {
        isPortal = true;
    }
    public void disablePortal()
    {
        isPortal = false;
    }


    public Long getLastFeedbackId()
    {
        return lastFeedbackId;
    }


    public void setLastFeedbackId(Long lastFeedbackId)
    {
        this.lastFeedbackId = lastFeedbackId;
    }
    
    
}
