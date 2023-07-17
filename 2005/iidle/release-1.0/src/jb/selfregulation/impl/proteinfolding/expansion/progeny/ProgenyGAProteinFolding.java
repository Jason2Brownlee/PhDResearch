
package jb.selfregulation.impl.proteinfolding.expansion.progeny;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyGA;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnitFactory;

/**
 * Type: ProgenyGAProteinFolding<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyGAProteinFolding extends ProgenyGA
{
    @Override
    protected Unit[] crossover(Unit u1, Unit u2)
    {
        ProteinFoldingUnit p1 = (ProteinFoldingUnit) u1;  
        ProteinFoldingUnit p2 = (ProteinFoldingUnit) u2;
        ProteinFoldingUnit [] children = new ProteinFoldingUnit[2];        
        // make a cut
        int cutPoint = rand.nextInt(p1.getModel().length);
        // create vectors
        byte [] v1 = new byte[p1.getModel().length];
        byte [] v2 = new byte[p1.getModel().length];
        // prepare vectors
        prepareVector(v1, p1.getModel(), p2.getModel(), cutPoint); // normal
        prepareVector(v2, p2.getModel(), p1.getModel(), cutPoint); // reversed
        // store children
        children[0] = (ProteinFoldingUnit) ((ProteinFoldingUnitFactory)unitFactory).generateNewUnit(v1);        
        children[1] = (ProteinFoldingUnit) ((ProteinFoldingUnitFactory)unitFactory).generateNewUnit(v2);
        return children;
    }
    
    protected void prepareVector(byte [] v, byte [] p1, byte [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }

    @Override
    protected void mutate(Unit aUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aUnit;
        byte [] model = u.getModel();
        
        for (int i = 0; i < model.length; i++)
        {
            if(rand.nextDouble() < mutationPercentage)
            {
                // 1,2,3,4 for the directions
                model[i] = (byte) ((Math.abs(rand.nextInt()) % 4) + 1);
            }
        }
    }
}
