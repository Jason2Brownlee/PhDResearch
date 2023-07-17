
package jb.selfregulation.impl.classification.expansion.progeny;

import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.classification.units.ClassificationUnit;
import weka.core.Instance;


/**
 * Type: LVQProgenyStrategyNoUpdate<br/>
 * Date: 3/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LVQProgenyStrategyNoUpdate extends LVQProgenyStrategy
{    
   
    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected, 
            StimulationStrategy aStimulationStrategy)
    {
        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        if(!selected.isEmpty())
        {
            for(Unit u : selected)
            {
                ClassificationUnit parent = (ClassificationUnit) u;
                Instance [] matches = parent.getMatches();
                for (int i = 0; i < matches.length; i++)
                {
                    // create child
                    createProgeny(parent, matches[i]);
                    // clear parent
                    parent.clearBMU();
                }
            }
        }
        
        return progeny;
    }
    
    @Override
    protected ClassificationUnit createProgeny(
            ClassificationUnit parent, 
            Instance aInstance)
    {       
        // check for same class between parent (bmu) and instance
        if(parent.getAssignedClass() == aInstance.classValue())
        {
            correctCount++;
        }
        // differing classes
        else
        {
            incorrectCount++;
        }
        return null;
    }
    
}
