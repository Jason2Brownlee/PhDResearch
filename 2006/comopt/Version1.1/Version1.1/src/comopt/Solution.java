
package comopt;

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
    protected int [] permutation;
    protected boolean isEvaluated;
    
    public Solution(Solution s)
    {
        this((int[])null);
        int [] p = new int[s.permutation.length];
        System.arraycopy(s.permutation, 0, p, 0, p.length);
        permutation = p;
    }
    
    public Solution()
    {
        this((int[])null);
    }
    public Solution(int [] aPerm)
    {
        score = Double.NaN;
        permutation = aPerm;
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
        for (int i = 0; i < permutation.length; i++)
        {
            if(permutation[i] != s.permutation[i])
            {
                return false;
            }
        }
        return true;
    }
    public int[] getPermutation()
    {
        return permutation;
    }      
}
