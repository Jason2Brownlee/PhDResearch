
package jb.selfregulation.impl.dummy.display;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.dummy.units.DummyUnit;


/**
 * Type: SpecialUnitLatticeEvaluator<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SpecialUnitLatticeEvaluator 
    implements UnitVisitor
{
    public int totalSpecial;
    public int totalNotSpecial;

    
    public void visitUnit(Unit aUnit)
    {
        DummyUnit u = (DummyUnit) aUnit;
        
        if(u.isSpecial())
        {
            totalSpecial++;            
        }
        else
        {
            totalNotSpecial++;
        }
    }
    
  
    
    public void finished()
    {
     
    }
    
    public void reset()
    {
        totalSpecial = totalNotSpecial = 0;
    }    
}