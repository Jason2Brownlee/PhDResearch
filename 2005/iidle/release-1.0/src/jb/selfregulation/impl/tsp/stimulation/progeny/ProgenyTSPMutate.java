package jb.selfregulation.impl.tsp.stimulation.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyMutate;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * Type: ProgenyGA<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyTSPMutate extends ProgenyMutate
{


    protected void mutate(Unit aUnit)
    {        
        TSPUnit u = (TSPUnit) aUnit;
        int [] data = u.getData();
//        double prob = ((double)mutation / (double)data.length);
        
        for (int i = 0; i < data.length; i++)
        {
            if(rand.nextDouble() < mutation)
            {
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