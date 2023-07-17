
package jb.selfregulation;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.initialise.LatticeInitialiser;

/**
 * Type: Lattice<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Lattice implements Configurable, Loggable
{      
    public final static int MIN_CELLS = 1;
    
    protected int totalCells;
    protected int tailLength;
    protected LatticeInitialiser init;
    
    protected final Logger logger;
    protected final LinkedList<Cell> cells;
    protected final LinkedList<Cell> portalsList;
    protected final Semaphore lock;
    

    /**
     * 
     */
    public Lattice()
    {
        logger = Logger.getLogger(LOG_CONFIG);
        cells = new LinkedList<Cell>();
        portalsList = new LinkedList<Cell>();
        lock = new Semaphore(1);
    }

    public String getBase()
    {
        return ".lattice";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        cells.clear(); // clear out everything
        portalsList.clear();
        
        totalCells = Integer.parseInt(prop.getProperty(aBase + getBase() + ".totalcells"));
        tailLength = Integer.parseInt(prop.getProperty(aBase + getBase() + ".taillength"));
        try
        {
            init = (LatticeInitialiser) Class.forName(prop.getProperty(aBase + getBase() + ".init.classname")).newInstance();
            init.loadConfig(aBase + getBase(), prop);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to preapre lattice initialiser", e);
        }
        
        logger.config("Lattice configuration loaded, totalcells["+totalCells+"], taillength["+tailLength+"]");
    }

    public void setup(SystemState aState)
    {
        init.setup(aState);
        // prepare this thing
        init.initialise(this, totalCells); 
        init.connectCellsInRing(this);
    }

    public void addCell(Cell aCell)
    {
        cells.add(aCell);
    }
    
    public Cell selectRandomCell(Random aRand)
    {
        getLock();
        int selected = aRand.nextInt(cells.size());
        Cell cell = cells.get(selected);
        putLock(); 
        return cell;
    }
    
    public LinkedList<Cell> getDuplicateCellList()
    {
        getLock();
        LinkedList<Cell> l = new LinkedList<Cell>();
        l.addAll(cells);        
        putLock();
        return l;
    }
    
    
    /**
     * @return Returns the cells.
     */
    public LinkedList<Cell> getCells()
    {
        return cells;
    }
    
    private void getLock()
    {
        try
        {
            lock.acquire();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    private void putLock()
    {
        lock.release();
    }
    
    
    /**
     * Does not lock on cells/units, thus the score may not the the most accurate
     * @return - [0]total units, [1]total cells, [2]total system evaled, [3]total system selected, [4] visitors
     */
    public int [] getRoughSystemState()
    {
        int [] scores = new int[5];
        
        // cell stats
        LinkedList<Cell> allCells = getDuplicateCellList();
        scores[1] = allCells.size();
        
        // process all cells        
        for(Cell c : allCells)
        {
            // this may not get lock on the cell
            LinkedList<Unit> allUnits = c.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            for(Unit u : allUnits)
            {
                // units
                scores[0]++;
                
                // check if evaluated
                if(u.isEvaluated())
                {
                    scores[2]++;
                }
                if(u.isSelected())
                {
                    scores[3]++;
                }
                if(u.isVisitor())
                {
                    scores[4]++;
                }
            }
        }
        
        return scores;
    }
    
    
    
    public double getRoughSystemEnergy()
    {
        double sum = 0.0;
        
        // process all cells
        LinkedList<Cell> allCells = getDuplicateCellList();
        for(Cell c : allCells)
        {
            // this may not get lock on the cell
            LinkedList<Unit> allUnits = c.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            for(Unit u : allUnits)
            {
                sum += u.getEnergy();
            }
        }
        
        return sum;
    }
    
    
    /**
     * Does not lock on cells/units, thus the score may not the the most accurate
     * @param v
     */
    public void getPerformRoughVisit(UnitVisitor v)
    {        
        // process all cells
        LinkedList<Cell> allCells = getDuplicateCellList();
        for(Cell c : allCells)
        {
            // this may not get lock on the cell
            LinkedList<Unit> allUnits = c.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            for(Unit u : allUnits)
            {
                v.visitUnit(u);
            }
        }
    }
    
    /**
     * Does not lock on cells/units, thus the score may not the the most accurate
     * @return - min, max
     */
    public double [] getRoughEnergyRange()
    {
        double [] range = new double[2];
        range[0] = Double.MAX_VALUE;
        range[1] = Double.MIN_VALUE;
        
        // process all cells
        LinkedList<Cell> allCells = getDuplicateCellList();
        for(Cell c : allCells)
        {
            // this may not get lock on the cell
            LinkedList<Unit> allUnits = c.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            for(Unit u : allUnits)
            {
                double s = u.getEnergy();
                if(s < range[0])
                {
                    range[0] = s;
                }
                if(s > range[1])
                {
                    range[1] = s;
                }
            }
        }
        
        return range;
    }
    
    
    /**
     * Does not lock on cells/units, thus the score may not the the most accurate
     * @return - max
     */
    public int getRoughMaxTailLength()
    {
        int max = Integer.MIN_VALUE;
        
        // process all cells
        LinkedList<Cell> allCells = getDuplicateCellList();
        for(Cell c : allCells)
        {
            int size = c.getTail().getUnits().size(); // this could be better...
            if(size > max)
            {
                max = size;
            }
        }
        
        return max;
    }
    /**
     * Does not lock on cells/units, thus the score may not the the most accurate
     * @return - total
     */
    public int getRoughTotalCells()
    {
        // this could be better
        return cells.size();
    }
    
    /**
     * Does not lock, thus there may be conflict problems
     *
     */
    public void setGlobalTailLength(int aMaxTailLength)
    {        
        // cheat for speed
        LinkedList<Cell> allCells = getDuplicateCellList();
        for(Cell c : allCells)
        {
            c.getTail().setMaxTailLength(aMaxTailLength);
        }
        
    }
    
    public void increaseCells(int aNumber)
    {
        LinkedList<Cell> list = new LinkedList<Cell>();
        Random r = init.getRand();
        for (int i = 0; i < aNumber; i++)
        {
            list.add(init.generateNewCell(r.nextInt(10)));
        }
        addCells(list, init);
    }
    public void decreaseCells(int aNumber)
    {
        if(cells.size()-aNumber < MIN_CELLS)
        {
            throw new RuntimeException("Invalid total number of cells to decrease by.");
        }
        deleteRandomCells(init.getRand(), aNumber, init);
    }
    
    
    public void addCells(LinkedList<Cell> newCells, LatticeInitialiser aInit)
    {
        getLock(); // shutdown everything
        cells.addAll(newCells); // add cells
        // reconnect ring
        aInit.connectCellsInRing(this);
        putLock();
    }
    
    public void deleteRandomCells(Random aRand, int aNumCells, LatticeInitialiser aInit)
    {
        getLock(); // shutdown everything
        // remove random cells
        for (int i = 0; i < aNumCells; i++)
        {
            boolean done = false;
            do
            {            
                // make selection
                int selection = aRand.nextInt(cells.size());
                // never remove portals
                if(!cells.get(selection).isPortal())
                {
                    // remove a cell
                    cells.remove(selection);
                    done = true;
                }
            }
            while(!done);
        }
        // reconnect ring
        aInit.connectCellsInRing(this);
        putLock();
    }
    
    
    protected class ChangeTailLength implements CellVisitor
    {
        protected int newMaxTailLength;
        
        public ChangeTailLength(int aNewMaxTailLength)
        {
            newMaxTailLength = aNewMaxTailLength;
        }
        
        public void visit(Cell aCell)
        {
            aCell.getTail().setMaxTailLength(newMaxTailLength);
        }
    }
    
    
    protected class PortalDisablerVisitor implements CellVisitor
    {
        public boolean success = false;
        
        public void visit(Cell aCell)
        {
            aCell.disablePortal();
            success = true;
        }
    }
    
    protected class PortalEnablerVisitor implements CellVisitor
    {
        public boolean success = false;
        
        public void visit(Cell aCell)
        {
            aCell.enablePortal();
            success = true;
        }
    }
    
    public void setPortals(int aNumPortals, Random aRand)
    {
        getLock(); // get lock
        // check if there is reshaping to do
        if(portalsList.size() != aNumPortals)
        {
            // check for a decrease
            if(portalsList.size() > aNumPortals)
            {
                while(portalsList.size() != aNumPortals)
                {
                    // pick a portal to disable
                    int selection = aRand.nextInt(portalsList.size());
                    Cell c = portalsList.get(selection);
                    PortalDisablerVisitor p = new PortalDisablerVisitor();
                    c.visitCell(p, Constants.LOCK_WAIT_TIME);
                    if(p.success)
                    {
                        portalsList.remove(selection);
                    }
                }
            }
            // check for an increase
            else if(portalsList.size() < aNumPortals)
            {
                while(portalsList.size() != aNumPortals)
                {
                    // pick a portal to enable
                    int selection = aRand.nextInt(cells.size());
                    Cell c = cells.get(selection);
                    PortalEnablerVisitor p = new PortalEnablerVisitor();
                    c.visitCell(p, Constants.LOCK_WAIT_TIME);
                    if(p.success)
                    {
                        portalsList.add(c);
                    }
                }
            }        
        }        
        putLock(); // release lock
    }
    
    public LinkedList<Cell> getDuplicatePortalList()
    {
        LinkedList<Cell> allPortals = new LinkedList<Cell>();
        getLock(); // get lock
        allPortals.addAll(portalsList);
        putLock(); // release lock
        return allPortals;
    }

    public LatticeInitialiser getInit()
    {
        return init;
    }
    public int getTailLength()
    {
        return tailLength;
    }   
    public void setTailLength(int tailLength)
    {
        this.tailLength = tailLength;
    }    
    public Logger getLogger()
    {
        return logger;
    }
    
    public int getInitialTotalCells()
    {
        return totalCells;
    }
}

