
package jb.selfregulation.expansion.selection;

import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;


public class SelectionAll extends SelectionStrategy
{

    @Override
    protected LinkedList<Unit> performSelection(
            StimulationStrategy aStimulation, 
            Cell aCell)
    {
        // copy all 
        LinkedList<Unit> all = new LinkedList<Unit>();
        all.addAll(aCell.getTail().getUnits());
        return all;
    }

}
