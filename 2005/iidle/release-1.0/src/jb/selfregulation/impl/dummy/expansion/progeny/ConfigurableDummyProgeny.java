
package jb.selfregulation.impl.dummy.expansion.progeny;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.dummy.units.DummyUnit;

/**
 * Type: ConfigurableDummyProgeny<br/>
 * Date: 25/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ConfigurableDummyProgeny extends ProgenyStrategy
{
    protected long id;
    protected double mean;
    protected double stdev;
    
    protected Random rand;
    
    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        
        String b = aBase + ".proliforation";
        id = Long.parseLong(prop.getProperty(b + ".id"));
        mean = Double.parseDouble(prop.getProperty(b + ".mean"));
        stdev = Double.parseDouble(prop.getProperty(b + ".stdev"));
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
        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        for (Unit u : selected)
        {
            for (int i = 0; i < totalProgeny; i++)
            {
                // create
                DummyUnit du = (DummyUnit) unitFactory.generateNewUnit(u);                                
                // calculate fitness
                double f = (rand.nextGaussian() * stdev + mean);
                
                // risky!
//                if(f < 0 || f > 1)
//                {
//                    throw new RuntimeException("fitness out of range: " + f + " mean["+mean+"] stdev["+stdev+"].");
//                }
                
                if(f < 0)
                {
                    f = 0;
                }
                else if(f > 1)
                {
                    f = 1;
                }
                
                du.setFitness(f);
                du.setCreatedId(id);
                
                // add to batch
                progeny.add(du);
            }
        }
        
        return progeny;
    }
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    
    
    

}
