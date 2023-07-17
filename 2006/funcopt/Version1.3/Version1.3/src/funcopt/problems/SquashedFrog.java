
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: SquashedFrog<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Evolutionary Computation Using Island Populations in Time (2004)
 * <br/>
 * @author Jason Brownlee
 */
public class SquashedFrog extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double t = Math.pow((x-0.1),2.0) + Math.pow((y-0.2), 2.0);        
        double result =  1.0 + Math.pow(t, 0.25) - Math.cos(5.0 * Math.PI * Math.sqrt(t));
        return result;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-2, +2};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[][]{{0.1,0.2}};
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Squashed Frog Function (Timbo)";
    }
}
