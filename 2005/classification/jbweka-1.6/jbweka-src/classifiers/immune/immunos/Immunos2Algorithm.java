
package weka.classifiers.immune.immunos;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Type: ImmunosClonalMean<br>
 * Date: 28/01/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class Immunos2Algorithm extends Immunos1Algorithm
{
    protected double [][] exemplars;
    
    
    
    protected void prepareClassifier(Instances aInstances)
    {
        super.prepareClassifier(aInstances);
        
        exemplars = new double[groups.length][];
        int numAttributes = aInstances.numAttributes();
        
        // prepare exemplars
        for (int i = 0; i < groups.length; i++)
        {            
            exemplars[i] = new double[numAttributes];
            int [] counts = new int[numAttributes];
            
            // sum values for each attribute over all instances
            for (int j = 0; j < groups[i].numInstances(); j++)
            {
                Instance current = groups[i].instance(j);
                for (int k = 0; k < exemplars[i].length; k++)
                {
                    // check for missing
                    if(Instance.isMissingValue(current.value(k)))
                    {
                        continue;
                    }
                    
                    exemplars[i][k] += current.value(k);
                    counts[k]++;
                }
            }
            
            // calculate means
            for (int j = 0; j < exemplars[i].length; j++)
            {
                exemplars[i][j] /= counts[j];
                
                if(aInstances.attribute(j).isNominal())
                {
                    exemplars[i][j] = Math.round(exemplars[i][j]);
                }
            }
        }
    }
    
    protected double [] calculateGroupAvidity(Instance aInstance)
    {
        double [] dataInstance = aInstance.toDoubleArray();
        double [] avidity = new double[groups.length];
        
        for (int i = 0; i < groups.length; i++)
        {
            // check for empty group
            if(groups[i].numInstances() == 0)
            {
                avidity[i] = Double.NaN;
            }
            else
            {
               double affinity = affinityFunction.distanceEuclideanUnnormalised(exemplars[i], dataInstance);
               // store summed affinity as avidity
               affinity = (groups[i].numInstances() / affinity);
	           avidity[i] = affinity;
            }
        }
        
        return avidity;
    }
}
