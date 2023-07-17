
package funcopt.problems;

import funcopt.Problem;

/**
 * 
 * Type: Exp<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description: Parameter free adaptive clonal selection (2004)
 * <br/>
 * @author Jason Brownlee
 */
public class Exp extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double sum = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum += -(v[i]*v[i]);
        }
        return 1.0 - Math.exp(sum);
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] m = new double[dimensions][2];
        for (int i = 0; i < m.length; i++)
        {
            m[i][0] = -10.0;
            m[i][1] = +10.0;
        }
        return m;
    }

    @Override
    protected double[][] preapreOptima()
    {
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
        return "Exp";
    }

}
