
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: GriewangksFunction8<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GriewangksFunction8 extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //   f8(x)=sum(x(i)^2/4000)-prod(cos(x(i)/sqrt(i)))+1, i=1:n
        // -600<=x(i)<= 600.
        
        double sum = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum += ((v[i]*v[i]) / 4000.0);
        }
        
        double product = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            double a = Math.cos(v[i] / Math.sqrt(i+1.0)) + 1.0;
            if(i == 0)
            {
                product = a;
            }
            else
            {
                product *= a;
            }
        }
        
        return (sum - product);
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-600, +600};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0; x(i)=0, i=1:n.
        
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
        return "Griewangk's Function 8";
    }
}
