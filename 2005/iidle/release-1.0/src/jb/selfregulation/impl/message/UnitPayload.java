
package jb.selfregulation.impl.message;

import jb.selfregulation.Unit;

public class UnitPayload extends ApplicationMessage
{
    protected Unit [] units;

    public UnitPayload()
    {
    }
    
    public UnitPayload(Unit [] aUnit)
    {
        units = aUnit;
    }

    public Unit[] getUnits()
    {
        return units;
    }

    public void setUnits(Unit[] units)
    {
        this.units = units;
    }
}
