package jb.selfregulation.impl.functopt.expansion.progeny;

import java.util.Arrays;
import java.util.LinkedList;

import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyACO;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.functopt.units.FuncOptUnitFactory;


/**
 * 
 * Type: ProgenyFuncOptACO<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyFuncOptACO extends ProgenyACO
{
    protected final static double INITAL_HISTORY_CONSTANT = 1.0;    
    protected Function function;
    
    
    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        function = (Function) aState.problem;
    }
    

    @Override
    protected Unit generateChild(double[][] aHistory)
    {
        boolean [] bitString = new boolean[aHistory.length];
        
        for (int i = 0; i < bitString.length; i++)
        {
            double sum = sum(aHistory[i]);
            if((aHistory[i][0]/sum) < rand.nextDouble())
            {
                bitString[i] = false; // 0
            }
            else
            {
                bitString[i] = true; // 1
            }
        }
        
        Unit unit = ((FuncOptUnitFactory)unitFactory).generateNewUnit(bitString);
        return unit;
    }


    @Override
    protected double[][] prepareHistoryMatrix(
            LinkedList<Unit> units, 
            StimulationStrategy aStimulationStrategy)
    {
        double [][] history = new double[function.getTotalBits()][2];
        // fill the history with a constant so that there is no zero 
        for (int i = 0; i < history.length; i++)
        {
            Arrays.fill(history[i], INITAL_HISTORY_CONSTANT);
        }
        // determine min max availalbe
        double [] minmax = determineBestScore(units, aStimulationStrategy);
        // process all units
        for (Unit u : units)
        {
            FuncOptUnit fu = (FuncOptUnit) u;
            // calculate contribution
            double quality = calculateContribution(fu, minmax[0], minmax[1]);
            // make contribution
            addUnitContribution(fu, history, quality);
        }                        
        return history;
    }
    
    protected double calculateContribution(FuncOptUnit u, double min, double max)
    {
        double score = u.getFunctionEvaluation();
        
        if(!function.isMinimisation())
        {
            return (score / max); // simple
        }
        
        // minimisation
        double offset = (min < 0) ? Math.abs(min) : 0; // add this to shift the lot into positive space
        // perform shift
        score += offset;
        min += offset;
        max += offset;
        // invert
        min = (max - min);
        score = (max - score);
        // calculate quality
        double quality = (score / min);
        
        if(quality>1||quality<0) 
            throw new RuntimeException("pox quality["+quality+"]");
        return quality;
    }

    protected double [] determineBestScore(
            LinkedList<Unit> units, 
            StimulationStrategy aStimulationStrategy)
    {
        aStimulationStrategy.sort(units);
        return new double[]{
                ((FuncOptUnit)units.getLast()).getFunctionEvaluation(), 
                ((FuncOptUnit)units.getFirst()).getFunctionEvaluation()};
    }
    
    protected void addUnitContribution(
            FuncOptUnit u, 
            double [][] aHistory, 
            double contribution)
    {
        boolean [] s = u.getBitString();
        
        for (int i = 0; i < s.length; i++)
        {
            // check if bit is on (1)
            if(s[i])
            {
                aHistory[i][1] += contribution; 
            }
            else
            {
                aHistory[i][0] += contribution;
            }
        }
    }
}