
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
 * Type: LVQProgenyStrategy<br/>
 * Date: 30/09/2005<br/>
 * <br/>
 * Description:
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class LVQProgenyStrategy extends ProgenyStrategy
{
    protected double learningRate;
    
    protected volatile long correctCount;
    protected volatile long incorrectCount;
    
    @Override
    public String getBase()
    {
        return super.getBase() + ".lvq";
    }
    @Override
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);        
        String b = aBase + super.getBase() + ".lvq";
        
        learningRate = Double.parseDouble(prop.getProperty(b+".learning.rate"));
    }
   
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
                    ClassificationUnit child = createProgeny(parent, matches[i]);
                    // store child
                    progeny.add(child);
                    // clear parent
                    parent.clearBMU();
                }
            }
        }
        
        return progeny;
    }
    
    
    /**
     * 
     * @param parent
     * @param aInstance
     * @return
     */
    protected ClassificationUnit createProgeny(
            ClassificationUnit parent, 
            Instance aInstance)
    {
        ClassificationUnit child = (ClassificationUnit) unitFactory.generateNewUnit(parent);
        double [] childV = child.getData();
        double [] v = aInstance.toDoubleArray();
        
        // check for same class between parent (bmu) and instance
        if(parent.getAssignedClass() == aInstance.classValue())
        {
            correctCount++;
            
            for (int i=0, j=0; i < v.length; i++)
            {
                if(aInstance.classIndex() != i)
                {
                    double diff = (v[i] - childV[j]);
                    childV[j] += (diff * learningRate); // move closer
                    j++;
                }
            }
        }
        // differing classes
        else
        {
            incorrectCount++;
            
            for (int i=0, j=0; i < v.length; i++)
            {
                if(aInstance.classIndex() != i)
                {
                    double diff = (v[i] - childV[j]);
                    childV[j] -= (diff * learningRate); // move further away
                    j++;
                }
            }
        }
        
        return child;
    }
    
    public void clearAccuracyCounts()
    {
        correctCount = 0;
        incorrectCount = 0;
    }
    
    public long getCorrectCount()
    {
        return correctCount;
    }
    public long getIncorrectCount()
    {
        return incorrectCount;
    }
}
