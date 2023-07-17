
package jb.selfregulation.impl.proteinfolding.units;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;

/**
 * Type: ProteinFoldingUnitFactory<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProteinFoldingUnitFactory extends UnitFactory
{
    protected HPModelEval problem;
    
    /**
     * The one true unit creation method 
     * everything else should end up calling this for consistancy
     * 
     * @param v
     * @return
     */
    public Unit generateNewUnit(byte [] v)
    {
        problem.isValid(v); // validation
        ProteinFoldingUnit unit = new ProteinFoldingUnit(v);
        setNewUnitEnergy(unit); // set energy
        return unit;
    }    
    @Override
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (HPModelEval) aState.problem;
    }    
    @Override
    public Unit generateNewUnit()
    {
        byte [] model = new byte[problem.getModelLength()];
        for (int j = 0; j < model.length; j++)
        {
            model[j] = (byte) ((Math.abs(rand.nextInt()) % 4) + 1);
        }
        return generateNewUnit(model);
    }
    @Override
    public Unit generateNewUnit(Unit aParentUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aParentUnit;
        byte [] v = new byte[u.getModel().length];
        System.arraycopy(u.getModel(), 0, v, 0, v.length);
        return generateNewUnit(v);
    }
}
