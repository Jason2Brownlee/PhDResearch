
package jb.selfregulation.impl.functopt.expansion.stimulation;


import java.util.Comparator;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * Type: StimulationFunctionEvaluation<br/>
 * Date: 15/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationFunctionEvaluation extends StimulationStrategy
{
    protected final static String NAME = "Function Evaluation";
    
    protected Function function;
    
    
    
    public void setup(SystemState aState)
    {
        // the order is intentional - so minimisation call works
        function = (Function) aState.problem;
        super.setup(aState);        
    }        

    @Override
    protected boolean getIsMinimisation()
    {
        return function.isMinimisation();
    }

    @Override
    public double calculateMeanScore(Cell aCell)
    {
        LinkedList<Unit> list = aCell.getDuplicateTailList();
        double meanScore = 0.0;
        double count = 0;
        
        for(Unit u : list)
        {
            FuncOptUnit funcoptUnit = (FuncOptUnit) u;
            if(funcoptUnit.isEvaluated())
            {
                meanScore += funcoptUnit.getFunctionEvaluation();
                count++;
            }
        }
        
        if(count == 0.0)
        {
            return Double.NaN;
        }
        
        return meanScore / count;
    }
   


    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        FuncOptUnit u = (FuncOptUnit) aUnit;
        
        // check if a re-evaluation is required
        if(!u.isHasFunctionEvaluation())
        {
            // calculate genotype
            double [] genotype = function.calculateGenotype(u.getBitString());
            // calculate score
            double score = function.evaluate(u.getVectorData());
            // store the bits
            u.setVectorData(genotype);
            u.setFunctionEvaluation(score);
            u.setHasFunctionEvaluation(true);
        }
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    protected Comparator prepareComparator()
    {
        if(isMinimise)
        {
            return new EvalMinimisationComparable();
        }
        
        return new EvalMaximisationComparable();
    }

    @Override
    public boolean isBetter(Unit a, Unit b)
    {
        double a1 = ((FuncOptUnit)a).getFunctionEvaluation();
        double b1 = ((FuncOptUnit)b).getFunctionEvaluation();
        
        if(function.isMinimisation())
        {
            if(a1 < b1)
            {
                return true;
            }
            return false;
        }
       
        // maximisation
        if(a1 > b1)
        {
            return true;
        }
        return false;        
    }

    public Function getFunction()
    {
        return function;
    }

    public void setFunction(Function function)
    {
        this.function = function;
    }
    
    
    
}
