
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: TwoDimensionalDeceptiveFunction<br/>
 * Date: 3/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TwoDimensionalDeceptiveFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double top = v[0]*v[0] + v[1]*v[1];
        double root = Math.sqrt(top/2.0);
        return (root <= 0.8) ? 0.8-root : (1.0-root)/0.2;
    }   
    
    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{0, 1};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = 1;
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
        return "2D Deceptive Function";
    }

}
