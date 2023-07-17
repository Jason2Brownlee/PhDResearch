
package humint.problem;

import java.awt.Dimension;
import java.awt.Shape;

/**
 * Type: Problem<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Problem implements Comparable<Problem>
{
    public final static Dimension BOUNDS = new Dimension(400, 400);
    
    public Problem()
    {
        initialise();
    }
    
    protected abstract void initialise();
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public Dimension getDomainBounds()
    {
        return BOUNDS; 
    }
    
    public abstract Shape [] getProblem();    
    public abstract String getName();

    public int compareTo(Problem o)
    {
        return getName().compareTo(o.getName());
    }
    
    
}
