
package jb.selfregulation.impl.dummy.listeners;

import java.util.LinkedList;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.dummy.units.DummyUnit;


/**
 * Type: ProgenyCreatedByUnitVisitor<br/>
 * Date: 25/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyCreatedByUnitVisitor implements UnitVisitor
{
    protected LinkedList [] strategies;
    
    
    public ProgenyCreatedByUnitVisitor(int totalProgenyStrategies)
    {
        strategies = new LinkedList[totalProgenyStrategies];
        for (int i = 0; i < strategies.length; i++)
        {
            strategies[i] = new LinkedList<DummyUnit>();
        }
    }
    
    
    public LinkedList<DummyUnit> getList(int id)
    {
        return strategies[id];
    }
    
    public void clear()
    {
        for (int i = 0; i < strategies.length; i++)
        {
            strategies[i].clear();
        }
    }
    
    
    public void visitUnit(Unit aUnit)
    {
        DummyUnit du = (DummyUnit) aUnit;
        int id = (int)du.getCreatedId();
        if(id > 0)
        {
            strategies[id-1].add(du);
        }
    }
}
