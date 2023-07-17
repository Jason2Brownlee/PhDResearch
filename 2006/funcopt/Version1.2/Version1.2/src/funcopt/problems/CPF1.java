
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: CPF1<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class CPF1 extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double sum = 0.0;        
        double product = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            double a = Math.abs(v[i]);
            
            sum += a;
            if(i==0)
            {
                product = a;
            }
            else
            {
                product *= a;
            }           
        }
        
        return (sum + product);
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
        return "CPF1";
    }

}
