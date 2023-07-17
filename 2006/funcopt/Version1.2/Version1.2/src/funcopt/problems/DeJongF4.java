
package funcopt.problems;

import java.util.Random;

import funcopt.Problem;

/**
 * Type: DeJongF4<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DeJongF4 extends Problem
{
    protected Random r;
    
    public DeJongF4()
    {
        r = new Random();
    }
    

    @Override
    protected double calculateCost(double[] v)
    {
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            for (int j = 0; j < 30; j++)
            {
                sum += (i * Math.pow(v[i], 4) + r.nextGaussian());
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
            d[i] = new double[]{-1.28, +1.28};
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
        return "De Jong F4";
    }

}
