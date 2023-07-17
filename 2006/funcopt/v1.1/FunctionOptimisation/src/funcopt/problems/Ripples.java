
package funcopt.problems;

import funcopt.Problem;

/**
 * 
 * Type: Ripples<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description: Parameter free adaptive clonal selection (2004)
 * <br/>
 * @author Jason Brownlee
 */
public class Ripples extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double p1 = 0.0;        
        for (int i = 0; i < dimensions; i++)
        {
            p1 += (v[i]*v[i]);
        }
        p1 = Math.sin(5.0 * Math.sqrt(p1));
        
        double p2 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p2 += (v[i]*v[i]);
        }
        p2 = 5.0 * Math.sqrt(p2);
        
        return 1.0 - (p1/p2);
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
        return "Ripples";
    }
}
