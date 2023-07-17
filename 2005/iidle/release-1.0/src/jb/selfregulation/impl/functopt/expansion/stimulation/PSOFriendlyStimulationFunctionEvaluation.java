
package jb.selfregulation.impl.functopt.expansion.stimulation;

import jb.selfregulation.Unit;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;


/**
 * 
 * Type: PSOFriendlyStimulationFunctionEvaluation<br/>
 * Date: 23/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class PSOFriendlyStimulationFunctionEvaluation extends StimulationFunctionEvaluation
{
    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        FuncOptUnit u = (FuncOptUnit) aUnit;
        
//        if(u.isHasPersonalBest())
//        {
//            double score = function.evaluate(u.getVectorData());
//            u.setFunctionEvaluation(score);
//            u.setHasFunctionEvaluation(true);
//        }
//        else
        {
            super.generateAndStoreFeedback(aUnit);
        }
    }
}
