
package jb.selfregulation.impl.proteinfolding.drawing;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;

/**
 * 
 * Type: GetBestCellVisitor<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GetBestCellVisitor implements UnitVisitor
{
    protected ProteinFoldingUnit best;

    /**
     * 
     * @param aUnit
     */
    public void visitUnit(Unit aUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aUnit;
        
        if(u.isEvaluated())
        {
            if(best==null)
            {
                best = u;
            }
            else
            {
                if(u.getScore() > best.getScore())
                {
                    best = u;
                }
            }
        }
    }

    public ProteinFoldingUnit getBest()
    {
        return best;
    }

    public void setBest(ProteinFoldingUnit best)
    {
        this.best = best;
    }

    
}
