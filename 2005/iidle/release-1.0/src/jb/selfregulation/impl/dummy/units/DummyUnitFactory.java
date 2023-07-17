
package jb.selfregulation.impl.dummy.units;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;

public class DummyUnitFactory extends UnitFactory
{

    @Override
    public Unit generateNewUnit()
    {
        DummyUnit u = new DummyUnit();
        u.setFitness(rand.nextDouble());
        setNewUnitEnergy(u);
        return u;
    }

    @Override
    public Unit generateNewUnit(Unit aParentUnit)
    {   
        Unit u = generateNewUnit();
        u.setEnergy(1.0);
        
        if(((DummyUnit)aParentUnit).isSpecial())
        {
            ((DummyUnit)u).setSpecial(true);
            ((DummyUnit)u).setFitness(1.0);
        }
        
        return u;
    }

}
