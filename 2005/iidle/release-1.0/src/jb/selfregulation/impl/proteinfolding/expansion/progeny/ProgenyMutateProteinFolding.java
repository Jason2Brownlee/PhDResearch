
package jb.selfregulation.impl.proteinfolding.expansion.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyMutate;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;


/**
 * Type: ProgenyMutateProteinFolding<br/>
 * Date: 15/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyMutateProteinFolding extends ProgenyMutate
{
    @Override
    protected void mutate(Unit aUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aUnit;
        byte [] model = u.getModel();
        
        for (int i = 0; i < model.length; i++)
        {
            if(rand.nextDouble() < mutation)
            {
                // 1,2,3,4 for the directions
                model[i] = (byte) ((Math.abs(rand.nextInt()) % 4) + 1);
            }
        }
    }
}
