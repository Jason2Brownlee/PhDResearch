
package jb.selfregulation.expansion.proliforation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;



/**
 * Type: ProgenyMutate<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class ProgenyMutate extends ProgenyStrategy
{
    protected Random rand;
    protected double mutation;
    
    
    
    public String getBase()
    {
        return super.getBase() + ".mutate";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".mutate";
        mutation = Double.parseDouble(prop.getProperty(b + ".mutation"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
    }
    
    

    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected, 
            StimulationStrategy aStimulationStrategy)
    {
        LinkedList<Unit> progenyList = new LinkedList<Unit>();
        
        // check for nothing selected
        if(selected.isEmpty())
        {
            return progenyList;
        }
       
        // generate progeny
        for(Unit u : selected)
        {
            for (int i = 0; i < totalProgeny; i++)
            {
                // generate child duplicate - with full energy
                Unit child = unitFactory.generateNewUnit(u);                
                // store child
                progenyList.add(child);
            }
        }        
        
        // mutate children
        mutation(progenyList);
        return progenyList;
    }    
    
    protected void mutation(LinkedList<Unit> progenyList)
    {        
        for (Iterator<Unit> iter = progenyList.iterator(); iter.hasNext(); )
        {       
            mutate(iter.next());
        }
    }
    
    
    protected abstract void mutate(Unit aUnit);


    public double getMutation()
    {
        return mutation;
    }

    public void setMutation(double mutation)
    {
        this.mutation = mutation;
    }    

    public Random getRand()
    {
        return rand;
    }
}
