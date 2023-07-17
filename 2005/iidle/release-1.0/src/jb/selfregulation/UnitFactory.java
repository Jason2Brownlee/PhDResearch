
package jb.selfregulation;

import java.util.Properties;
import java.util.Random;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;

/**
 * Type: UnitFactory<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class UnitFactory implements Configurable
{
    public final static double ENERGY_MAX = 1.0;
    public final static double ENERGY_MIN = 0.0;
    public final static double ENERGY_INITIAL = ENERGY_MAX;
    
    protected Random rand;
    
    public UnitFactory()
    {}
    
    public String getBase()
    {
        return "";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        // nothing to load
    }
    
    public void setup(SystemState aState)
    {
        rand = aState.rand;
    }
    
    
    
    public abstract Unit generateNewUnit();    
    public abstract Unit generateNewUnit(Unit aParentUnit);    
    
    
    public void setNewUnitEnergy(Unit aUnit)
    {
//        aUnit.setEnergy(ENERGY_INITIAL);
        aUnit.setEnergy(rand.nextDouble());
    }
//    public void updateUnitEnergy(Unit aUnit)
//    {
//        aUnit.setEnergy(aUnit.getEnergy() + ENERGY_MAX);
//        aUnit.setEnergy(ENERGY_MAX);
//    }

    public Random getRand()
    {
        return rand;
    }

    public void setRand(Random rand)
    {
        this.rand = rand;
    }
    
    
    
}
