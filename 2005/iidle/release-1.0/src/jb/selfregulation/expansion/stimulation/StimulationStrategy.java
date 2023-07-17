
package jb.selfregulation.expansion.stimulation;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;

/**
 * Type: StimulationFactory<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class StimulationStrategy
    implements UnitStimulator, Configurable
{
    protected final Comparator comparator;
    
    protected Long id;
    protected boolean isMinimise;
    
  
    public StimulationStrategy()
    {
        comparator = prepareComparator();
    }    
    
    
    
    public String getBase()
    {
        return ".stimulation";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".stimulation";
        id = Long.parseLong(prop.getProperty(b + ".id"));
    }
    
    public void setup(SystemState aState)
    {
        isMinimise = getIsMinimisation();
    }
    
    
    
    
    protected abstract void generateAndStoreFeedback(Unit aUnit);    
    
    public abstract String getName();    
    
    protected abstract Comparator prepareComparator();
    
    public abstract boolean isBetter(Unit a, Unit b);
    
    protected abstract boolean getIsMinimisation();
    
    public abstract double calculateMeanScore(Cell aCell);
    
    
    public boolean isMinimisation()
    {
        return isMinimise;
    }
    public boolean isMaximisation()
    {
        return !isMinimise;
    }
    
    public void sort(LinkedList<Unit> list)
    {
        Collections.sort(list, comparator);
    }
    
    public void stimulate(Cell aCell)
    {
        LinkedList<Unit> units = aCell.getTail().getUnits();
        // visit and stimulate all units
        stimulateUnits(aCell, units);
    } 
    
    protected void stimulateUnits(Cell aCell, LinkedList<Unit> aUnits)
    {
        // visit and stimulate all units
        for(Unit u : aUnits)
        {
            u.stimulate(this);
        }
        
        // inform the cell who the last stimlation process was
        aCell.setLastFeedbackId(getId());
    }    
    
    public Long stimulate(Unit aUnit)
    {
        // evaluate
        generateAndStoreFeedback(aUnit);
        return id;
    }
   
    public Long getId()
    {
        return id;
    }



    public boolean isMinimise()
    {
        return isMinimise;
    }



    public void setMinimise(boolean isMinimise)
    {
        this.isMinimise = isMinimise;
    }



    public void setId(Long id)
    {
        this.id = id;
    }    
    
}
