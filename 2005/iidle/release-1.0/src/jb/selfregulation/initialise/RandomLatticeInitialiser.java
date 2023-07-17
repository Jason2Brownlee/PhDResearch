
package jb.selfregulation.initialise;

import jb.selfregulation.Cell;

/**
 * Type: RandomLatticeInitialiser<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RandomLatticeInitialiser extends LatticeInitialiser
{
    
    public Cell generateNewCell()
    {
        int tailLength = 1 + rand.nextInt(maxUnitsToGenerate - 1);  
        Cell cell = generateNewCell(tailLength);
        return cell;
    }
}
