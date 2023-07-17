
package jb.selfregulation.impl.classification.units;

import java.util.Properties;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.classification.problem.ClassificationProblem;
import weka.core.Instance;
import weka.core.Instances;

/** 
 * Type: ClassificationUnitFactory<br/>
 * Date: 29/09/2005<br/>
 * <br/>
 * Description:
 * 
 * Very simple initialisation procedure that creates new +blank+ units
 * that have the variables and class assignment of instances in the training
 * data. When the system is initialised, this simple approach will ensure
 * that the mixture of classes will be the same as that in the training data - 
 * and the same as the actual data as long as the training set is an effective sample.
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class ClassificationUnitFactory extends UnitFactory
{
    
    protected ClassificationProblem problem;
    
    
    
    public String getBase()
    {
        return ".classification";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        // nothing to load
        super.loadConfig(aBase, prop);
    }
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        // need a reference to the problem
        problem = (ClassificationProblem) aState.problem;
    }
    
    
    
    @Override
    public Unit generateNewUnit()
    {
        // simply initalise with a randomly selected instance
        Instances training = problem.getTraining();
        int selection = rand.nextInt(training.numInstances());
        Instance instance = training.instance(selection);
        double [] vec = instance.toDoubleArray();
        
        double [] v = new double[vec.length-1];
        for (int i=0, j=0; i < v.length; i++)
        {
            if(instance.classIndex() != i)
            {
                v[j++] = vec[i];
            }
        }
        // store data in new unit
        ClassificationUnit u = new ClassificationUnit();
        u.setData(v);
        u.setAssignedClass(instance.classValue());
        return u;
    }

    
    @Override
    public Unit generateNewUnit(Unit aParentUnit)
    {
        // prepare parent
        ClassificationUnit parent = (ClassificationUnit) aParentUnit; 
        double [] parentVec = parent.getData(); 
        // prepare data
        ClassificationUnit u = new ClassificationUnit();
        double [] v = new double[parentVec.length];
        System.arraycopy(parentVec, 0, v, 0, parentVec.length);
        // store data
        u.setData(v);
        u.setAssignedClass(parent.getAssignedClass());
        
        return u;
    }

}
