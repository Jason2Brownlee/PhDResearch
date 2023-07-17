
package jb.selfregulation.impl.dummy.expansion.progeny;

import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;

/**
 * Type: DummyProgeny<br/>
 * Date: 17/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DummyProgeny extends ProgenyStrategy
{
    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected, 
            StimulationStrategy aStimulationStrategy)
    {
        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        for (Unit u : selected)
        {
            for (int i = 0; i < totalProgeny; i++)
            {
                progeny.add(unitFactory.generateNewUnit(u));
            }
        }
        
        return progeny;
    }

}
