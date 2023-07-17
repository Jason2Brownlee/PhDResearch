
package funcopt.problems;

import funcopt.Problem;

/**
 * 
 * Type: SumOfDifferentPowerFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SumOfDifferentPowerFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //  f9(x)=sum(abs(x(i))^(i+1)), i=1:n; -1<=x(i)<=1.
        
        double sum = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum += Math.pow(Math.abs(v[i]), (i+1));
        }        
        return sum;
    }

    @Override
    public String getName()
    {
        return "Sum of different power function";
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-1, +1};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0; x(i)=0, i=1:n.
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = 0;
        }
        return d;
    }

}
