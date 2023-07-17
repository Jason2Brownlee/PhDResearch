
package jb.selfregulation.initialise;

import jb.selfregulation.Cell;

/**
 * Type: UniformLatticeInitialiser<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class UniformLatticeInitialiser extends LatticeInitialiser
{



   
    public Cell generateNewCell()
    {
        Cell cell = generateNewCell(maxUnitsToGenerate);
        return cell;
    }

}
