
package jb.selfregulation.initialise;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.Tail;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;

/**
 * Type: LatticeInitialiser<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class LatticeInitialiser implements Configurable
{
    protected Random rand;    
    protected UnitFactory unitFactory;    
    protected Lattice lattice;
    
    
    protected int maxUnitsToGenerate;

    
    
    /**
     * 
     */
    public LatticeInitialiser()
    {}
    
    
    public String getBase()
    {
        return ".init";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".init";
        maxUnitsToGenerate = Integer.parseInt(prop.getProperty(b + ".taillength"));
        
    }
    public void setup(SystemState aState)
    {
        rand = aState.rand;
        unitFactory = aState.unitFactory;
        lattice = aState.lattice;
    }


    public void initialise(Lattice aLattice, int aMaximumCells)
    {
        for (int i = 0; i < aMaximumCells; i++)
        {     
            Cell cell = generateNewCell();
            aLattice.addCell(cell);
        }
    }
    
    
    public abstract Cell generateNewCell();
    
    public Cell generateNewCell(int numUnits)
    {
        Tail tail = new Tail(lattice.getTailLength());
        Cell cell = new Cell(tail);
        addUnitsToCell(cell, numUnits);
        return cell;
    }
    
    public void addUnitsToCell(Cell aCell, int aNumUnits)
    {
        Tail tail = aCell.getTail();
        
        for (int i = 0; i < aNumUnits; i++)
        {
            tail.addUnit(unitFactory.generateNewUnit());
        }
    }
    
    public void connectCellsInRing(Lattice aLattice)
    {
        LinkedList<Cell> cells = aLattice.getCells();
        Cell [] allCells = cells.toArray(new Cell[cells.size()]);
        
        // clear all neighbours
        for (int i = 0; i < allCells.length; i++)
        {
            allCells[i].clearNeighbours();
        }
        
        // connect on down the line
        for (int i = 0; i < allCells.length; i++)
        {
            // connect to previous node
            if(i == 0)
            {
                // connect first to last
                allCells[i].addNeighbour(allCells[allCells.length-1]); 
            }
            else
            {
                allCells[i].addNeighbour(allCells[i-1]);
            }
            
            // connect to next node            
            if(i == allCells.length-1)
            {
                // connect last to first
                allCells[i].addNeighbour(allCells[0]); 
            }
            else
            {
                allCells[i].addNeighbour(allCells[i+1]);
            }
        }
    }
    
    public void connectCellsInTorrid(Lattice aLattice)
    {
        throw new UnsupportedOperationException("Not yet supported configuration.");
    }

    public Random getRand()
    {
        return rand;
    }

    public UnitFactory getUnitFactory()
    {
        return unitFactory;
    }
}
