
package jb.selfregulation.impl.classification.expansion.selection;

import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.selection.SelectionStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.classification.units.ClassificationUnit;


/**
 * Type: BMUSelectionStrategy<br/>
 * Date: 30/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BMUSelectionStrategy extends SelectionStrategy
{

    @Override
    protected LinkedList<Unit> performSelection(StimulationStrategy aStimulation, Cell aCell)
    {
        LinkedList<Unit> list = new LinkedList<Unit>();
        
        LinkedList<Unit> all = aCell.getTail().getUnits();
        for(Unit u : all)
        {
            ClassificationUnit cu = (ClassificationUnit) u;
            if(cu.isBmu())
            {
                list.add(cu);
            }
        }
        
        return list;
    }

}
