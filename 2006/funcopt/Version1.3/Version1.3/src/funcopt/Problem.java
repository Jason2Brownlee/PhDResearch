
package funcopt;

import java.util.LinkedList;

/**
 * Type: Problem<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Problem implements Comparable<Problem>
{
   public final static int TOTAL_EVALUATIONS = 10000;
    public final static int DIMENSIONS = 2;
    public final static int BIT_PRECISION = 64;
    
    
    protected int bitPrecision;
    protected int maxEvaluations;
    protected int dimensions;
    
    protected LinkedList<SolutionNotify> listeners;
    
    protected double [][] minmax;
    protected double [][] globalOptima;
    protected boolean isMinimise;
    
    protected int evaluationCount;
    
    
    public Problem()
    {
        listeners = new LinkedList<SolutionNotify>();
    }
    
    protected void notifyListeners(double [] d, double score)
    {
        for(SolutionNotify s : listeners)
        {
            s.notifyOfPoint(d, score);
        }
    }
    
    
    /**
     * Is s1 better than s2????
     * 
     * @param s1
     * @param s2
     * @return
     */
    public boolean isBetter(Solution s1, Solution s2)
    {
        return isBetter(s1.getScore(), s2.getScore());
    }
    /**
     * Is s1 better than s2????
     * 
     * @param s1
     * @param s2
     * @return
     */
    public boolean isBetter(double s1, double s2)
    {
        if(isMinimise)
        {
            return s1 < s2;
        }
        
        return s1 > s2;
    }
    
    
    protected abstract double calculateCost(double [] v);
    protected abstract double [][] preapreMinMax();
    protected abstract double [][] preapreOptima();
    protected abstract boolean isMinimiseProblem();
    public abstract String getName();
    
    public void initialise()   
    {
        bitPrecision = BIT_PRECISION;
        dimensions = DIMENSIONS;
        maxEvaluations = TOTAL_EVALUATIONS;
        
        resetEvaluations();
        minmax = preapreMinMax();
        globalOptima = preapreOptima();
        isMinimise = isMinimiseProblem();
    }
    
    public void setMaximumEvaluations(int aMax)
    {
        maxEvaluations = aMax;
    }
    
    public void resetEvaluations()
    {
        evaluationCount = 0;
    }
    
    public int remainingFunctionEvaluations()
    {
        if(evaluationCount > maxEvaluations)
        {
            return 0;
        }
        
        return maxEvaluations - evaluationCount;
    }
    
    public boolean isReamainingEvaluations()
    {
        return remainingFunctionEvaluations() > 0;
    }
    
    
    public void cost(Solution s)
    {
        if(s.isEvaluated())
        {
            return;
        }
        
        double c = cost(s.getCoordinate());
        s.evaluated(c);
    }
    public void cost(LinkedList<Solution> ss)
    {
        for(Solution s : ss)
        {
            cost(s);
        }
    }
    
    protected double cost(double [] v)
    {        
        // 1 to n
        if(++evaluationCount > maxEvaluations)
        {
            //throw new RuntimeException("Unable to evaluate, exceeded maximum function evaluations.");
            return Double.NaN;
        }
        
        // safety
        checkSafety(v);
        
        double s = calculateCost(v);
        notifyListeners(v, s);
        return s;
    }
    
    public void checkSafety(double [] v)
    {
        for (int i = 0; i < v.length; i++)
        {
            if(v[i] < minmax[i][0] || v[i] > minmax[i][1])
            {
                throw new RuntimeException("Unable to evaluate, coordinate is out of function bounds (dimension ["+i+"]).");
            }
        }
    }

    public double unCountedCost(double [] v)
    {
        return calculateCost(v);
    }
    
    public void addListener(SolutionNotify l)
    {
        listeners.add(l);
    }
    
    public int getBitPrecision()
    {
        return bitPrecision;
    }

    public int getDimensions()
    {
        return dimensions;
    }

    public int getEvaluationCount()
    {
        return evaluationCount;
    }

    public double[][] getGlobalOptima()
    {
        return globalOptima;
    }

    public boolean isMinimise()
    {
        return isMinimise;
    }

    public LinkedList<SolutionNotify> getListeners()
    {
        return listeners;
    }

    public int getMaxEvaluations()
    {
        return maxEvaluations;
    }

    public double[][] getMinmax()
    {
        return minmax;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param o
     * @return
     */
    public int compareTo(Problem o)
    {
        return getName().compareTo(o.getName());
    }
    
    public boolean isToroidal()
    {
        return false;
    }
}
