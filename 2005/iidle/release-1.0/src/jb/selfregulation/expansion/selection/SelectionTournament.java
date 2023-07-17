
package jb.selfregulation.expansion.selection;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;


/**
 * Type: SelectionTournament<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SelectionTournament extends SelectionStrategy
{    
    protected Random rand;    
    protected int tournamentSize;
    protected int numToSelect;
   
    
    public String getBase()
    {
        return super.getBase() + ".tournament";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".tournament";
        tournamentSize = Integer.parseInt(prop.getProperty(b + ".size"));
        numToSelect = Integer.parseInt(prop.getProperty(b + ".total"));
    }    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
    }
    
    
    public LinkedList<Unit> performSelection(LinkedList<Unit> units,StimulationStrategy aStimulation)
    {
        LinkedList<Unit> selected = new LinkedList<Unit>();
        
        if(!units.isEmpty())
        {
            if(numToSelect < MIN_SELECTION)        
            {
                throw new RuntimeException("Configured to select too few units " + numToSelect);
            } 
        
            while(selected.size() < numToSelect)
            {
                // perofmr a selection
                Unit u = select(units, selected, aStimulation);
                // add to the pool
                selected.add(u);
            }            
        }
        
        return selected;
    }
    
    
    @Override
    protected LinkedList<Unit> performSelection(
            StimulationStrategy aStimulation, 
            Cell aCell)
    {
        LinkedList<Unit> units = new LinkedList<Unit>();
        units.addAll(aCell.getTail().getUnits());
        return performSelection(units, aStimulation);
    }
    

    protected Unit select(
            LinkedList<Unit> units,
            LinkedList<Unit> alreadySelected,
            StimulationStrategy aStimulation)
    {
        Unit unit = null;        
        
        // make selection
        for (int i = 0; i < Math.min(tournamentSize, units.size()); i++)
        {
            int s = rand.nextInt(units.size());
            if(i == 0)
            {
                unit = units.get(s);
            }
            else if(aStimulation.isBetter(units.get(s), unit))
            {
                unit = units.get(s);
            }
        }
            
        // add to selection list
        alreadySelected.add(unit);
        
        return unit;
    }


    public int getTournamentSize()
    {
        return tournamentSize;
    }
    public void setTournamentSize(int tournamentSize)
    {
        this.tournamentSize = tournamentSize;
    }
    public int getNumToSelect()
    {
        return numToSelect;
    }
    public void setNumToSelect(int numToSelect)
    {
        this.numToSelect = numToSelect;
    }
    public Random getRand()
    {
        return rand;
    }
    public void setRand(Random rand)
    {
        this.rand = rand;
    }
    
    
}
