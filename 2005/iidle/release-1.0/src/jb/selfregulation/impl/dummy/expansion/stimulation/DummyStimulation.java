
package jb.selfregulation.impl.dummy.expansion.stimulation;

import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.dummy.units.DummyUnit;

public class DummyStimulation extends StimulationStrategy
{
    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {}

    @Override
    public String getName()
    {
        return ".dummy";
    }

    @Override
    protected Comparator prepareComparator()
    {
        return new Comparator<DummyUnit>()
        {
            /**
             * Compares its two arguments for order.  Returns a negative integer,
             * zero, or a positive integer as the first argument is less than, equal
             * to, or greater than the second.<p>
             * 
             * reversed - maximise - biggest to smallest
             */
            public int compare(DummyUnit o1, DummyUnit o2)
            {
                if(o1.getFitness() < o2.getFitness())
                {
                    return -1;
                }
                else if(o1.getFitness() > o2.getFitness())
                {
                    return +1; // reversed
                }
                return 0;
            }
        };
    }

    @Override
    public boolean isBetter(Unit a, Unit b)
    {
        DummyUnit a1 = (DummyUnit) a;
        DummyUnit b1 = (DummyUnit) b;
        
        if(a1.getFitness() > b1.getFitness())
        {
            return true; // a better
        }
        
        return false; // b better
    }

    @Override
    protected boolean getIsMinimisation()
    {
        return false; // maximisation
    }

    @Override
    public double calculateMeanScore(Cell aCell)
    {
        double sum = 0.0;
        LinkedList<Unit> list = aCell.getTail().getUnits();
        
        if(list.isEmpty())
        {
            return 0.0;
        }
        
        for(Unit u : list)
        {
            sum += ((DummyUnit)u).getFitness();
        }
        
        
        return sum / list.size();
    }
}
