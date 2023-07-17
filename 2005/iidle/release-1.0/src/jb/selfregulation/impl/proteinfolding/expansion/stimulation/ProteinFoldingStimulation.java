
package jb.selfregulation.impl.proteinfolding.expansion.stimulation;

import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;


/**
 * Type: ProteinFoldingStimulation<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProteinFoldingStimulation extends StimulationStrategy
{
    protected HPModelEval problem;
    

    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) aUnit;
        byte [] model = u.getModel();
        float score = problem.evaluateModel(model);
        u.setScore(score);
    }

    @Override
    public String getName()
    {
        return "Protein Folding";
    }
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (HPModelEval) aState.problem;
    }

    @Override
    protected Comparator prepareComparator()
    {
        return new EvalMaximisationComparable();
    }

    @Override
    public boolean isBetter(Unit a, Unit b)
    {
        ProteinFoldingUnit u1 = (ProteinFoldingUnit) a;
        ProteinFoldingUnit u2 = (ProteinFoldingUnit) b;
        
        if(u1.getScore() > u2.getScore())
        {
            return true;
        }
        
        return false;
    }

    @Override
    protected boolean getIsMinimisation()
    {
        return false;
    }

    @Override
    public double calculateMeanScore(Cell aCell)
    {
        float score = 0.0f;
        int total = 0;
        
        LinkedList<Unit> list = aCell.getTail().getUnits();
        if(list.isEmpty())
        {
            return 0.0;
        }
        
        for(Unit a : list)
        {
            ProteinFoldingUnit u = (ProteinFoldingUnit) a;
            if(u.isEvaluated())
            {
                score += u.getScore();
                total++;
            }
        }
        
        if(total == 0)
        {
            return 0.0;
        }
        
        return score / total;
    }

}
