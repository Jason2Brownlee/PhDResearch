
package jb.selfregulation.impl.proteinfolding.drawing;
import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;


/**
 * Type: ProteinFoldingLatticeEvaluator<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProteinFoldingLatticeEvaluator 
    implements UnitVisitor
{
    public double evalMax;
    public double evalMin;
    public double evalMean;
    public long evalTotal; 

    public void visitUnit(Unit aUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aUnit;
        
        if(u.isEvaluated())
        {
            double s = u.getScore();
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