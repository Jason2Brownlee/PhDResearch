
package jb.selfregulation.impl.dummy.expansion;

import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.impl.dummy.units.DummyUnit;
import jb.selfregulation.processes.work.ProcessMovement;


/**
 * Type: UnidirectionalMovement<br/>
 * Date: 20/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class UnidirectionalMovement extends ProcessMovement
{
    
   /* @Override
    protected int selectUnit(Cell aCell)
    {
        // check if an intelligent unit selection is NOT permitted
//        if(!intelligentUnitSelection)
//        {
//            return super.selectUnit(aCell);
//        }
        
        LinkedList<Unit> units = aCell.getTail().getUnits();
        int bestIndex = 0;
        DummyUnit best = (DummyUnit) units.get(0);
        
        for (int i = 0; i < units.size(); i++)
        {
            DummyUnit du = (DummyUnit) units.get(i);
            if(du.getFitness() > best.getFitness())
            {
                best = du;
                bestIndex = i;
            }
        }
        
        return bestIndex;
    }*/
    
    @Override
    protected int selectNeighbour(Cell aCell)
    {
        LinkedList<Cell> neighbours = aCell.getNeighbours();
        
        if(neighbours.size() != 2)
        {
            throw new RuntimeException("Total neighbours ["+neighbours.size()+"] does not meet expected for ring topology [2].");
        }
        
        return 0;
    }
}
