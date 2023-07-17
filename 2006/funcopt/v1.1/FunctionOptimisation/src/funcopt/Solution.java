
package funcopt;

/**
 * Type: Solution<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Solution implements Comparable<Solution>
{
    protected double score;
    protected double [] coordinate;
    protected boolean isEvaluated;
    
    public Solution()
    {
        this(null);
    }
    public Solution(double [] aCoord)
    {
        score = Double.NaN;
        coordinate = aCoord;
        isEvaluated = false;
    }
    
    
    public void evaluated(double aCost)
    {
        if(isEvaluated)
        {
            throw new RuntimeException("Solution is already evaluated!");
        }
        score = aCost;
        isEvaluated = true;
    }
    
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object
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
        return 0; // same
    }
    
    public double[] getCoordinate()
    {
        return coordinate;
    }
    public void setCoordinate(double[] coordinate)
    {
        this.coordinate = coordinate;
    }
    public double getScore()
    {
        return score;
    }
    
    
    
    public boolean isEvaluated()
    {
        return isEvaluated;
    }
    @Override
    public String toString()
    {
        return "score["+score+"]";
    }
    
    @Override
    public boolean equals(Object o)
    {
        Solution s = (Solution) o;
        for (int i = 0; i < coordinate.length; i++)
        {
            if(coordinate[i] != s.coordinate[i])
            {
                return false;
            }
        }
        return true;
    }
}
