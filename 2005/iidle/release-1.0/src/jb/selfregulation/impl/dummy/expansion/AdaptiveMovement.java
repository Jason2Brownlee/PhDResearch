
package jb.selfregulation.impl.dummy.expansion;

import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.impl.dummy.units.DummyUnit;
import jb.selfregulation.processes.work.ProcessMovement;



/**
 * Type: AdaptiveMovement<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AdaptiveMovement extends ProcessMovement
{
    protected boolean intelligentUnitSelection;
    protected boolean intelligentNeighbourSelection;
    protected boolean allowNonMovement;
    
    @Override
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        
        String b = aBase + getBase();

        // load
        intelligentUnitSelection = Boolean.parseBoolean(prop.getProperty(b + ".smartunit"));
        intelligentNeighbourSelection = Boolean.parseBoolean(prop.getProperty(b + ".smartneighbour"));
        allowNonMovement = Boolean.parseBoolean(prop.getProperty(b + ".allownonmove"));
    }    
    
    @Override
    protected int selectNeighbour(Cell aCell)
    {
        if(!intelligentNeighbourSelection)
        {
            return super.selectNeighbour(aCell);
        }
        
        LinkedList<Cell> neighbours = aCell.getNeighbours();
        int selection = 0;
        Cell best = neighbours.get(0);
        
        for (int i = 1; i < neighbours.size(); i++)
        {
            Cell c = neighbours.get(i);
            if(c.getStimulationCount() > best.getStimulationCount())
            {
                best = c;
                selection = i;
            }
        }        
        
        if(allowNonMovement)
        {
            // check if a movement is NOT worth doing
            if(best.getStimulationCount() < aCell.getStimulationCount())
            {
                // do not move
                return -1;
            }            
        }

        return selection;
    }
    
    @Override
    protected int selectUnit(Cell aCell)
    {
        // check if an intelligent unit selection is NOT permitted
        if(!intelligentUnitSelection)
        {
            return super.selectUnit(aCell);
        }
        
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
    }
}
