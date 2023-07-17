
package jb.selfregulation.expansion.selection;

import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;


/**
 * Type: SelectionGreedy<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SelectionGreedy extends SelectionStrategy
{
    protected int numToSelect;    
    
    
    public String getBase()
    {
        return super.getBase() + ".greedy";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        numToSelect = Integer.parseInt(prop.getProperty(aBase + getBase() + ".total"));
    }
    
     
    
    
    

    @Override
    protected LinkedList<Unit> performSelection(
            StimulationStrategy aStimulation, 
            Cell aCell)
    {
        LinkedList<Unit> units = aCell.getTail().getUnits();
        LinkedList<Unit> selected = new LinkedList<Unit>();
        
        if(!units.isEmpty())
        {            
            if(numToSelect < MIN_SELECTION)
            {
                throw new RuntimeException("Configured to select too few units " + numToSelect);
            }    
            
            // sort 
            aStimulation.sort(units);
            
            // will loop around if the tail is too short to fill the 
            // desired number of selected units
            while(selected.size() < numToSelect)
            {
                // select the best
                for (int i = 0; selected.size() < numToSelect && i < units.size(); i++)
                {
                    if(aStimulation.isMinimisation())
                    {                   
                        selected.add(units.get(i)); // get first and onwards
                    }
                    else
                    {
                        selected.add(units.get(units.size()-1-i)); // get last and onwards
                    }
                }
            }
        }
        
        return selected;
    }


    public int getNumToSelect()
    {
        return numToSelect;
    }


    public void setNumToSelect(int numToSelect)
    {
        this.numToSelect = numToSelect;
    }
}
