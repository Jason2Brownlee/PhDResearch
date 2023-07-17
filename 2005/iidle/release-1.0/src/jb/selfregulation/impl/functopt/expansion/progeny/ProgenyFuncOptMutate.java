package jb.selfregulation.impl.functopt.expansion.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyMutate;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;


/**
 * Type: ProgenyFuncOptMutate<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyFuncOptMutate extends ProgenyMutate
{
//    public ProgenyFuncOptMutate(UnitFactory aUnitFactory, Random aRand)
//    {
//        super(aUnitFactory, aRand);
//    }

    
    @Override
    protected void mutate(Unit aUnit)
    {               
        FuncOptUnit u = (FuncOptUnit) aUnit;        
        boolean [] string = u.getBitString();
        //double m = ((double)mutation / (double)string.length);
        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(rand.nextDouble() < mutation)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }

   
    
}