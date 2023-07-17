
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: HimmelblausFunction<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HimmelblausFunction extends Problem
{
    @Override
    protected double calculateCost(double[] v)
    {
//        double x = v[0];
//        double y = v[1];        
//        return (2186.0-Math.pow(Math.pow(x,2.0) + y - 11.0, 2.0) + Math.pow(x + Math.pow(y,2.0) - 7.0, 2.0)) / 2186.0;
        
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
        //return new double[][]{{3,2}};
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
        return "Himmelblau's Function";
    }

}

