
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: ThreePotHoles<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Evolutionary Computation Using Island Populations in Time (2004)
 * <br/>
 * @author Jason Brownlee
 */
public class ThreePotHoles extends Problem
{
    @Override
    protected double calculateCost(double[] v)
    {
        double p1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p1 += ((v[i] + 8.0) * (v[i] + 8.0)) + 0.1;
        }
        
        double p2 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p2 += ((v[i] + 2.0) * (v[i] + 2.0)) + 0.2;
        }
        
        double p3 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p3 += (v[i] - 3.0) * (v[i] - 3.0);
        } 
        
        double result = Math.sqrt(Math.sqrt(p1) * Math.sqrt(p2) * Math.sqrt(p3));       
        return result;
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
        return new double[][]{{3, 3}};
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Three Pot Holes";
    }

}
