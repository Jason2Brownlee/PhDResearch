
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: HimmelbausFunction<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HimmelbausFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            sum += Math.pow(v[i], 4.0) - (16.0 * Math.pow(v[i], 2.0) + (5.0 * v[i]));
        }
        
        return (1.0/v.length) * sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-6, +6};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // TODO Auto-generated method stub
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
        return "Himmelbau's Function";
    }

}
