
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: MultiplePeakFunction<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MultiplePeakFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double root = Math.sqrt((x*x) + (y*y));
        double sin = Math.sin(root);
        double p1 = (sin*sin) - 0.5;
        
        double lower = 1.0 + 0.001 * ((x*x) + (y*y));
        double p2 = lower*lower;
        
        return (0.5 - (p1/p2)) * -1.0;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-30, +30};
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
        return false;
    }

    @Override
    public String getName()
    {
        return "Multiple-Peak Function";
    }

}
