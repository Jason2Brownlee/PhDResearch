
package jb.selfregulation.impl.functopt.drawing;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;


/**
 * Type: FucOptLatticeEvaluator<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FuncOptLatticeEvaluator 
    implements UnitVisitor
{
    public double evalMax;
    public double evalMin;
    public double evalMean;
    public long evalTotal; 

    public void visitUnit(Unit aUnit)
    {
        FuncOptUnit u = (FuncOptUnit) aUnit;
        
        if(u.isHasFunctionEvaluation())
        {
            double s = u.getFunctionEvaluation();
            if(s < evalMin)
            {
                evalMin = s;
            }
            if(s > evalMax)
            {
                evalMax = s;
            }
            evalMean += s;
            evalTotal++;
        }
    }
    
  
    
    public void finished()
    {
        evalMean /= evalTotal;
    }
    
    public void reset()
    {
        evalMax = Double.MIN_VALUE;
        evalMin = Double.MAX_VALUE;
        evalMean = 0D;
        evalTotal = 0L;
    }
    
}