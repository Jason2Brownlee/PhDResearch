
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: DeJongF1<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DeJongF1 extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // f1(x)=sum(x(i)^2), i=1:n, -5.12<=x(i)<=5.12
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            sum += Math.pow(v[i], 2);
        }
        
        return sum;        
    }

    @Override
    public String getName()
    {
        return "De Jong F1";
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
            d[i] = new double[]{-5.12, +5.12};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0, x(i)=0, i=1:n.
        
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = 0;
        }
        return d;
    }
    
}
