
package jb.selfregulation.expansion.selection;

import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;

public abstract class SelectionStrategy 
    implements UnitSelector, Configurable
{    
    public final static int MIN_SELECTION = 2;
    
    
    public String getBase()
    {
        return ".selection";
    }    
    public void loadConfig(String aBase, Properties prop)
    {}    
    public void setup(SystemState aState)
    {}
    
    
    public void select(Unit aUnit)
    {
        // update energy
        aUnit.setEnergy(Unit.MAX_ENERGY);
    }

    public LinkedList<Unit> select(StimulationStrategy aStimulation, Cell aCell)
    {
        LinkedList<Unit> selected = performSelection(aStimulation, aCell);
        // visit each of the selected an inform them that they are selected
        for(Unit u : selected)
        {
            u.select(this);
        }
        
        return selected;
    }

    protected abstract LinkedList<Unit> performSelection(StimulationStrategy aStimulation, Cell aCell);
    
}
