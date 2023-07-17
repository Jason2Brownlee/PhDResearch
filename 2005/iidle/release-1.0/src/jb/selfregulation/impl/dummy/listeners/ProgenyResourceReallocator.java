
package jb.selfregulation.impl.dummy.listeners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.impl.dummy.expansion.progeny.ConfigurableDummyProgeny;
import jb.selfregulation.impl.dummy.units.DummyUnit;
import jb.selfregulation.processes.work.ProcessExpansion;


/**
 * Type: ProgenyResourceReallocator<br/>
 * Date: 25/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyResourceReallocator implements LatticeStatusListener
{
    protected long period;
    protected long count;
    protected long lastStrikeTime;
    protected int totalProgenyStrategies;
    protected long totalProgenyResource;
    
    protected ProgenyCreatedByUnitVisitor visitor;
    protected ProgenyStrategy [] progenyStrategies;
    

    public void latticeChangedEvent(Lattice aLattice)
    {
        count++;
        
        // check if time to strike
        if(count >= lastStrikeTime+period)
        {
            lastStrikeTime = count;
            // visit
            aLattice.getPerformRoughVisit(visitor);
            // reallocate           
            performReallocation();
            // cleanup
            visitor.clear();            
            
            System.out.println("STRIKE!");
        }
    }

    
    protected void performReallocation()
    {
        // calculate raw scores
        double [] scores = new double[totalProgenyStrategies];        
        for (int i = 0; i < scores.length; i++)
        {
            LinkedList<DummyUnit> list =  visitor.getList(i);
            if(!list.isEmpty())
            {
                scores[i] = calculateScore(list);
            }            
        }
        // normalise
        double best = scores[0];
        for (int i = 0; i < scores.length; i++)
        {
            if(scores[i] > best)
            {
                best = scores[i];
            }
        }
        for (int i = 0; i < scores.length; i++)
        {
            scores[i] = scores[i] / best;
        }
        // allocate
        for (int i = 0; i < progenyStrategies.length; i++)
        {
            int allocated = (int) Math.round(totalProgenyResource * scores[i]);
            allocated /= 2; // because the amplitude is two
            progenyStrategies[i].setTotalProgeny(allocated);
        }        
    }
    
    protected double calculateScore(LinkedList<DummyUnit> list)
    {   
        // mean
        double score = 0;        
        for(DummyUnit u : list)
        {
            score += u.getFitness();
        }
        return score / list.size();       
        
        // max
        // TODO
        
        // mean + max / 2
        // TODO
    }
    
    
    public String getBase()
    {        
        return ".progenyreallocator";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".progenyreallocator";
        period = Long.parseLong(prop.getProperty(b + ".period"));
        totalProgenyStrategies = Integer.parseInt(prop.getProperty(b + ".totalstrategies"));
        totalProgenyResource = Integer.parseInt(prop.getProperty(b + ".totalprogeny"));
    }
    public void setup(SystemState aState)
    {
        visitor = new ProgenyCreatedByUnitVisitor(totalProgenyStrategies);      
        progenyStrategies = new ProgenyStrategy[totalProgenyStrategies];
        for (int i = 0; i < progenyStrategies.length; i++)
        {
            Long id = new Long(i + 1);
            progenyStrategies[i] = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).get(id).getProgeny();
            if(((ConfigurableDummyProgeny)progenyStrategies[i]).getId() != i+1)
            {
                throw new RuntimeException("Expected that progeny proliforation strategy ["+id+"] would have same ID as expansion strategy ["+i+"].");
            }
        }
    }
}
