
package jb.selfregulation.impl.classification.units;

import java.util.LinkedList;

import jb.selfregulation.Unit;
import weka.core.Instance;

/**
 * Type: ClassificationUnit
 * <br/>
 * Date: 29/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ClassificationUnit extends Unit
{
    protected double [] data;
    protected double assignedClass;
    protected boolean isBMU;
    protected LinkedList<Instance> matches;
    
    public ClassificationUnit()
    {
        matches = new LinkedList<Instance>();
    }
    
    
    public boolean isBmu()
    {
        return isBMU;
    }
    public void clearBMU()
    {
        isBMU = false;
        matches.clear();        
    }
    public void addMatch(Instance aInstance)
    {
        isBMU = true;
        matches.add(aInstance);
    }
    public Instance [] getMatches()
    {
        return matches.toArray(new Instance[matches.size()]);
    }
    
    public double distance(Instance aInstance)
    {
        double d = 0.0;
        double [] v = aInstance.toDoubleArray();
        for (int i = 0, j=0; i < v.length; i++)
        {
            if(i!=aInstance.classIndex())
            {
                double diff = v[i] - data[j++];
                d += (diff*diff);
            }
        }
        
        return Math.sqrt(d);
    }
    
    
    public double getAssignedClass()
    {
        return assignedClass;
    }
    public void setAssignedClass(double assignedClass)
    {
        this.assignedClass = assignedClass;
    }
    public double[] getData()
    {
        return data;
    }
    public void setData(double[] data)
    {
        this.data = data;
    }        
}
