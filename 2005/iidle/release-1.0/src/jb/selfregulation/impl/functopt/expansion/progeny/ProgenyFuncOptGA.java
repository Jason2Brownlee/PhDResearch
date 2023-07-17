package jb.selfregulation.impl.functopt.expansion.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyGA;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.functopt.units.FuncOptUnitFactory;


/**
 * Type: ProgenyFuncOptMutate<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyFuncOptGA extends ProgenyGA
{
//    public ProgenyFuncOptGA(UnitFactory aUnitFactory, Random aRand)
//    {
//        super(aUnitFactory, aRand);
//    }

    @Override
    protected Unit[] crossover(Unit u1, Unit u2)
    {
        FuncOptUnit p1 = (FuncOptUnit) u1;  
        FuncOptUnit p2 = (FuncOptUnit) u2;
        FuncOptUnit [] children = new FuncOptUnit[2];        
        // make a cut
        int cutPoint = rand.nextInt(p1.getBitString().length);
        // create vectors
        boolean [] v1 = new boolean[p1.getBitString().length];
        boolean [] v2 = new boolean[p1.getBitString().length];
        // prepare vectors
        prepareVector(v1, p1.getBitString(), p2.getBitString(), cutPoint); // normal
        prepareVector(v2, p2.getBitString(), p1.getBitString(), cutPoint); // reversed
        // store children
        children[0] = (FuncOptUnit) ((FuncOptUnitFactory)unitFactory).generateNewUnit(v1);        
        children[1] = (FuncOptUnit) ((FuncOptUnitFactory)unitFactory).generateNewUnit(v2);
        return children;
    }
    
    protected void prepareVector(boolean [] v, boolean [] p1, boolean [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }

    @Override
    protected void mutate(Unit aUnit)
    {
        openMutate(aUnit);
    }
    
    public void openMutate(Unit aUnit)
    {
        FuncOptUnit u = (FuncOptUnit) aUnit;        
        boolean [] string = u.getBitString();
        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(rand.nextDouble() < mutationPercentage)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }
}