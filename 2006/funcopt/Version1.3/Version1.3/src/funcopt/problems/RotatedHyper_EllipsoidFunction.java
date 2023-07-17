
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: RotatedHyper_EllipsoidFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RotatedHyper_EllipsoidFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //f1b(x)=sum(sum(x(j)^2), j=1:i), i=1:n, -65.536<=x(i)<=65.536.
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            for (int j = 0; j < i; j++)
            {
                sum += Math.pow(v[j], 2);
            }
        }
        
        return sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-65.536, +65.536};
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

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Rotated hyper-ellipsoid function";
    }

}
