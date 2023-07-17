
package jb.selfregulation.impl.tsp.units;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.problem.TSPProblem;

/**
 * Type: TSPUnitFactory<br/>
 * Date: 31/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPUnitFactory extends UnitFactory
{   
    protected int length;
    protected boolean setup = false;
    
    
    public TSPUnitFactory()
    {}
    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        length = ((TSPProblem)aState.problem).getCities().length;
        setup = true;
    }
    
   
    public Unit generateNewUnit()
    {
        if(!setup)
        {
            throw new RuntimeException("Attempting to create units before setup");
        }
        
        int [] v = generateRandomVector();
        TSPUnit unit = new TSPUnit(v);
        setNewUnitEnergy(unit);
        return unit;
    }
   
    public Unit generateNewUnit(Unit aParentUnit)
    {
        // duplicate
        int [] v = duplicateVector(((TSPUnit)aParentUnit).getData());     
        return generateNewUnit(v);
    }
    
    public Unit generateNewUnit(int [] aData)
    {
        TSPUnit unit = new TSPUnit(aData);
        setNewUnitEnergy(unit);
        return unit;
    }
       
    
    public int [] duplicateVector(int [] aV)
    {
        int [] v = new int[aV.length];
        System.arraycopy(aV, 0, v, 0, v.length);
        return v;
    }
    
    
    public int [] generateRandomVector()
    {
        int [] v = new int[length];
        for (int i = 0; i < v.length; i++)
        {
            v[i] = i;
        }        
        shuffle(v);
        shuffle(v);
        return v;
    }
    
   protected void shuffle(int [] v)
   {
       for (int i = 0; i < v.length; i++)
       {
           randomSwap(v);
       }
   }

   
   protected void randomSwap(int [] v)
   {
       int s1 = rand.nextInt(v.length);
       int s2 = rand.nextInt(v.length);
       
       int a = v[s1];
       v[s1] = v[s2];
       v[s2] = a;
   }
}
