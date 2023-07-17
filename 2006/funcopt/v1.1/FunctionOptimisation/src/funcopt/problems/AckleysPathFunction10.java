
package funcopt.problems;

import funcopt.Problem;

/**
 * 
 * Type: AckleysPathFunction10<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AckleysPathFunction10 extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //   f10(x)=-a·exp(-b·sqrt(1/n·sum(x(i)^2)))-exp(1/n·sum(cos(c·x(i))))+a+exp(1)
        // a=20; b=0.2; c=2·pi; i=1:n; -32.768<=x(i)<=32.768.
        
        double sum1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum1 += (v[i] * v[i]);
        }        
        double p1 = Math.exp(-0.2 * Math.sqrt( (1.0/dimensions) * sum1));
       
        double sum2 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum2 += Math.cos(2.0*Math.PI*v[i]);
        }
        double p2 = Math.exp((1.0/dimensions) * sum2);
        
        double result = -20.0 * p1 - p2 + 20.0 + Math.E;
        return result;
    }

    @Override
    public String getName()
    {
        return "Ackley's Path Function 10";
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
            d[i] = new double[]{-32.768, +32.768};
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
}
