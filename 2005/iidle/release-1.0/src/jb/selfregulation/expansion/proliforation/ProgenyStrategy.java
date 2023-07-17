
package jb.selfregulation.expansion.proliforation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Tail;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;


/**
 * Type: ProgenyStrategy<br/>
 * Date: 4/07/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class ProgenyStrategy implements Configurable
{    
    protected UnitFactory unitFactory;    
    
    protected int totalProgeny;
    
    
    
    public String getBase()
    {
        return ".proliforation";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".proliforation";
        totalProgeny = Integer.parseInt(prop.getProperty(b + ".total"));
    }
    public void setup(SystemState aState)
    {
        unitFactory = aState.unitFactory;
    }
    

    
    public int progeny(
            Cell aCell, 
            LinkedList<Unit> selected, 
            StimulationStrategy aStimulationStrategy)
    {
        // generate progeny
        LinkedList<Unit> progeny = generateProgeny(aCell, selected, aStimulationStrategy);
        // store in cell
        return store(progeny, aCell.getTail());
    }
    
    protected int store(LinkedList<Unit> progeny, Tail aTail)
    {
        int total = 0;
        
        for (Iterator<Unit> iter = progeny.iterator(); !aTail.isFull() && iter.hasNext(); total++)
        {
            Unit u =  iter.next();
            aTail.addUnit(u);
        }
        return total;
    }    
    
    protected abstract LinkedList<Unit> generateProgeny(Cell aCell, LinkedList<Unit> selected, StimulationStrategy aStimulationStrategy);

    public int getTotalProgeny()
    {
        return totalProgeny;
    }
    public void setTotalProgeny(int totalProgeny)
    {
        this.totalProgeny = totalProgeny;
    }
    public UnitFactory getUnitFactory()
    {
        return unitFactory;
    }
    public void setUnitFactory(UnitFactory unitFactory)
    {
        this.unitFactory = unitFactory;
    }    
    
    
}
