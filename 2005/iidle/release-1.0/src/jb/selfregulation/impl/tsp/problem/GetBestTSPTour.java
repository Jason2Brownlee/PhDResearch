
package jb.selfregulation.impl.tsp.problem;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;
import jb.selfregulation.impl.tsp.units.TSPUnit;

/**
 * Type: GetBestTSPTour<br/>
 * Date: 20/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GetBestTSPTour implements UnitVisitor
{
    protected TSPUnit best;

    /**
     * 
     * @param aUnit
     */
    public void visitUnit(Unit aUnit)
    {
        TSPUnit u = (TSPUnit) aUnit;
        
        if(u.isEvaluated() && u.isHasTourLength())
        {
            if(best==null)
            {
                best = u;
            }
            else
            {
                // minimise
                if(u.getTourLength() < best.getTourLength())
                {
                    best = u;
                }
            }
        }
    }

    public TSPUnit getBest()
    {
        return best;
    }    
}