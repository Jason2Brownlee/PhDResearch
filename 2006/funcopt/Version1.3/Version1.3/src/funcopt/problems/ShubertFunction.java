
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: ShubertFunction<br/>
 * Date: 11/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ShubertFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double result = 1;

        for (int index = 0; index < dimensions; index++)
        {
            double sum = 0;

            for (int i = 1; i <= 5; i++)
            {
                sum += i * Math.cos((i + 1) * v[index] + i);
            }

            result *= sum;
        }

        return -result;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-10, +10};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        return null;
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Shubert Function";
    }
}
