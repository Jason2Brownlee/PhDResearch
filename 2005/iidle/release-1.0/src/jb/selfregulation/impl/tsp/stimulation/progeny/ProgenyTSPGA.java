package jb.selfregulation.impl.tsp.stimulation.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyGA;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;
import jb.selfregulation.impl.tsp.units.TSPUnitFactory;



/**
 * Type: ProgenyTSPGA<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyTSPGA extends ProgenyGA
{
    protected TSPProblem problem;


    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (TSPProblem) aState.problem;
    }
    
    
    
    protected TSPUnit [] crossover(Unit u1, Unit u2)
    {
        TSPUnit [] progeny = new TSPUnit[2];
        TSPUnit p1 = (TSPUnit) u1;
        TSPUnit p2 = (TSPUnit) u2;
        
        // determine cross point
        int crossPoint = 0;
        
        if(rand.nextDouble() < crossoverPercentage)
        {
            crossPoint = rand.nextInt(p1.getData().length);
        } // otherwise take all of the second unit
        
        // perform crossover
        progeny[0] = getCrossedPermutation(p1.getData(), p2.getData(), crossPoint);
        progeny[1] = getCrossedPermutation(p2.getData(), p1.getData(), crossPoint);        
        return progeny;
    }    
    
    protected TSPUnit getCrossedPermutation(
            int [] d1, 
            int [] d2, 
            int point)
    {
        int [] data = new int[d1.length];
        
        // copy the first block
        for (int i = 0; i < point; i++)
        {
            data[i] = d1[i];
        }
        
        // copy the remaining from the other        
        for (int i = point, offset = point; i < data.length; )
        {
            int next = d2[offset++];
            if(offset >= data.length)
            {
                offset = 0;
            }
            // check if the piece is useful
            if(!exists(next, data, i))
            {
                data[i++] = next;
            }
        }
        
        // ensure the thing is good
        problem.checkSafety(data);
        
        // create the unit
        TSPUnit unit = (TSPUnit) ((TSPUnitFactory)unitFactory).generateNewUnit(data);
        return unit;
    }
    
    protected boolean exists(int a, int [] v, int end)
    {
        for (int i = 0; i < end; i++)
        {
            if(v[i] == a)
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    // local mutation
    protected void mutate(Unit aUnit)
    {        
        TSPUnit u = (TSPUnit) aUnit;
        int [] data = u.getData();
        
        for (int i = 0; i < data.length; i++)
        {
            if(rand.nextDouble() < mutationPercentage)
            {
                /*
                if(i == data.length-1)
                {
                    swap(i, 0, data);
                }
                else
                {
                    swap(i, i+1, data);
                }
                */
                
                // random swap
                swap(i, rand.nextInt(data.length), data);
            } 
        }
    }
    
    protected void swap(int i, int j, int [] d)
    {
        int t = d[i];
        d[i] = d[j];
        d[j] = t;
    }
}