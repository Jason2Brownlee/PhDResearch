
package jb.selfregulation.impl.proteinfolding.expansion.progeny;

import java.util.Arrays;
import java.util.LinkedList;

import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyACO;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnitFactory;


/**
 * Type: ProgenyACOProteinFolding<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyACOProteinFolding extends ProgenyACO
{   
    protected final static double INITAL_HISTORY_CONSTANT = 1.0;
    protected HPModelEval problem;
    protected ProteinFoldingUnitFactory unitFactory;    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (HPModelEval) aState.problem;
        unitFactory = (ProteinFoldingUnitFactory) aState.unitFactory;
    }
    
    @Override
    protected Unit generateChild(double[][] aHistory)
    {
        byte [] model = new byte[problem.getModelLength()];
        
        for (int i = 0; i < model.length; i++)
        {
            // sum
            double sumTotal = sum(aHistory[i]);
            // selection
            double selection = rand.nextDouble();
            
            double sum = 0.0;
            boolean done = false;
            for (int j = 0; !done && j < aHistory[i].length; j++)
            {
                sum += (aHistory[i][j] / sumTotal);
                if(sum >= selection)
                {
                    // selection made
                    model[i] = (byte) (j+1); // shited to 1-4
                    done = true;
                }
            }    
            if(!done)
            {
                throw new RuntimeException("ACO Unable to select a dimension selection["+selection+"], sum["+sumTotal+"]");
            }
        }
        
        return unitFactory.generateNewUnit(model);
    }


    @Override
    protected double[][] prepareHistoryMatrix(
            LinkedList<Unit> units, 
            StimulationStrategy aStimulationStrategy)
    {
        double [][] history = new double[problem.getModelLength()][4];
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
            ProteinFoldingUnit pfu = (ProteinFoldingUnit) u;
            // calculate contribution
            double quality = calculateContribution(pfu, minmax[0], minmax[1]);
            // make contribution
            addUnitContribution(pfu, history, quality);
        }              
        // normalise
        for (int i = 0; i < history.length; i++)
        {
            normaliseColumn(history[i]);
        }        
        // apply exponent
        for (int i = 0; i < history.length; i++)
        {
            for (int j = 0; j < history[i].length; j++)
            {
                history[i][j] = Math.pow(history[i][j], historyExponent);
            }
        }
        
        // now remember kids - to normalise again before decision time
        
        return history;
    }
    
    protected double calculateContribution(
            ProteinFoldingUnit u, 
            double min, 
            double max)
    {
        double score = u.getScore();
        return ((score - min) / (max - min));
    }

    protected double [] determineBestScore(
            LinkedList<Unit> units, 
            StimulationStrategy aStimulationStrategy)
    {
        aStimulationStrategy.sort(units);
        return new double[]{
                ((ProteinFoldingUnit)units.getLast()).getScore(), 
                ((ProteinFoldingUnit)units.getFirst()).getScore()};
    }
    
    protected void addUnitContribution(
            ProteinFoldingUnit u, 
            double [][] aHistory, 
            double contribution)
    {
        byte [] s = u.getModel();
        
        for (int i = 0; i < s.length; i++)
        {
            // contribute to selected absolute movement
            aHistory[i][s[i]-1] += contribution; 
        }
    }
}
