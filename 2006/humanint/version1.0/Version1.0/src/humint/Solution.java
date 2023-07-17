
package humint;

import java.awt.Shape;

/**
 * Type: Solution<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Solution implements Comparable<Solution>
{
    protected Shape [] shapes;
    protected double score;

    public Shape[] getShapes()
    {
        return shapes;
    }

    public void setShapes(Shape[] shapes)
    {
        this.shapes = shapes;
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param o
     * @return
     */
    public int compareTo(Solution o)
    {
        if(score < o.score)
        {
            return -1;
        }
        else if(score > o.score)
        {
            return +1;
        }
        return 0;
    }
    
    
    
}
