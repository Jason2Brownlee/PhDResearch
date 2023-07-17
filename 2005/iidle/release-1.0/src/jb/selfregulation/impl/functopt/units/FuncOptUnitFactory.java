
package jb.selfregulation.impl.functopt.units;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.problem.Function;

/**
 * Type: FuncOptUnit<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FuncOptUnitFactory extends UnitFactory
{
    protected int bitStringLength;   
    protected int totalParameters;
    protected Function problem;
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        bitStringLength = ((Function) aState.problem).getTotalBits();
        totalParameters = ((Function) aState.problem).getNumDimensions();
        problem = (Function) aState.problem;
    }
    
    
    
    protected boolean [] generateRandomBitString()
    {
        boolean [] b = new boolean[bitStringLength];        
        for (int i = 0; i < b.length; i++)
        {
            b[i] = rand.nextBoolean();
        }        
        return b;
    }
    
    
    @Override
    public Unit generateNewUnit()
    {
        boolean [] v = generateRandomBitString();
        return generateNewUnit(v);
    }
    @Override
    public Unit generateNewUnit(Unit aParentUnit)
    {
        FuncOptUnit u = (FuncOptUnit) aParentUnit;
        boolean [] v = new boolean[u.getBitString().length];
        System.arraycopy(u.getBitString(), 0, v, 0, v.length);
        return generateNewUnit(v);
    }
    
    
    /**
     * The one true unit creation method for func opt 
     * everything else should end up calling this for consistancy
     * 
     * @param aBitString
     * @return
     */
    public Unit generateNewUnit(boolean [] aBitString)
    {
        FuncOptUnit unit = new FuncOptUnit();
        unit.setBitString(aBitString);
        unit.setVectorData(problem.calculateGenotype(aBitString));
        setNewUnitEnergy(unit); // set energy
        unit.setVelocity(new double[totalParameters]); // HACK TODO: remove this hack        
        return unit;
    }



    public int getBitStringLength()
    {
        return bitStringLength;
    }



    public void setBitStringLength(int bitStringLength)
    {
        this.bitStringLength = bitStringLength;
    }



    public Function getProblem()
    {
        return problem;
    }



    public void setProblem(Function problem)
    {
        this.problem = problem;
    }



    public int getTotalParameters()
    {
        return totalParameters;
    }



    public void setTotalParameters(int totalParameters)
    {
        this.totalParameters = totalParameters;
    }
    
    
    
}