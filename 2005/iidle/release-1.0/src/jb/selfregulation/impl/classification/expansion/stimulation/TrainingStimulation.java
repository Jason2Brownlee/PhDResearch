
package jb.selfregulation.impl.classification.expansion.stimulation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.classification.problem.ClassificationProblem;
import jb.selfregulation.impl.classification.units.ClassificationUnit;
import weka.core.Instance;
import weka.core.Instances;


/**
 * Type: TrainingStimulation<br/>
 * Date: 30/09/2005<br/>
 * <br/>
 * Description: Stimulate the units
 * <br/>
 * @author Jason Brownlee
 */
public class TrainingStimulation extends StimulationStrategy
{
    protected ClassificationProblem problem;
    protected Instances training;
    protected Random rand;
    // settings
    protected int numPatterns;
    
    
    public String getBase()
    {
        return super.getBase() + ".training";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".training";
        
        // load settings
        numPatterns = Integer.parseInt(prop.getProperty(b + ".numpatterns"));
    }    
    public void setup(SystemState aState)
    {
        super.setup(aState);

        // store bits
        problem = (ClassificationProblem) aState.problem;
        training = problem.getTraining();
        rand = aState.rand;
    }
    
    
    protected LinkedList<Instance> preaprePatternList()
    {
        LinkedList<Instance> list = new LinkedList<Instance>();
        // a little bit of safety - in case the num patterns is > total training instances
        while(list.size() < numPatterns && 
                list.size()<training.numInstances())
        {
            int selection = rand.nextInt(training.numInstances());
            list.add(training.instance(selection));
        }
        return list;
    }
    
    protected ClassificationUnit locateBMU(Instance it, LinkedList<Unit> aUnits)
    {
        double best = Double.MAX_VALUE;
        ClassificationUnit bmu = null;
        
        for(Unit u : aUnits)
        {
            ClassificationUnit cu = (ClassificationUnit) u;
            double dist = cu.distance(it);
            if(dist < best)
            {
                best = dist;
                bmu = cu;
            }
        }
        
        return bmu;
    }
    
    @Override
    protected void stimulateUnits(Cell aCell, LinkedList<Unit> aUnits)
    {
        // visit and stimulate all units
        for(Unit u : aUnits)
        {
            u.stimulate(this);
        }
        
        // do the real work
        LinkedList<Instance> list = preaprePatternList();
        for(Instance it : list)
        {
            // locate the bmu
            ClassificationUnit bmu = locateBMU(it, aUnits);            
            // mark the bmu
            bmu.addMatch(it); // stores match and marks as bmu
            // TODO score 
        }
        
        // inform the cell who the last stimlation process was
        aCell.setLastFeedbackId(getId());
    }        
    @Override
    public Long stimulate(Unit aUnit)
    {
        return id; // nothing
    }
    @Override
    protected void generateAndStoreFeedback(Unit aUnit)
    {
        throw new RuntimeException("Not supported!");        
    }
    
    
    
    
    
    
    
    @Override
    public String getName()
    {
        return "Training Stimulation";
    }
    
    @Override
    protected Comparator prepareComparator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBetter(Unit a, Unit b)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean getIsMinimisation()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double calculateMeanScore(Cell aCell)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    

}
