
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: SchwefelsFunction<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SchwefelsFunction extends Problem
{
    @Override
    protected double calculateCost(double[] v)
    {
        // f7(x)=sum(-x(i)·sin(sqrt(abs(x(i))))), i=1:n; -500<=x(i)<=500.
        
        double sum = 0.0;        
        for (int i = 0; i < dimensions; i++)
        {
            sum += (-v[i]) * Math.sin(Math.sqrt(Math.abs(v[i])));
        }
        return sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-500, +500};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=-n·418.9829; x(i)=420.9687, i=1:n.
        
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = +420.9687;
        }
        return d;
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Schwefel's Function";
    }        
}
