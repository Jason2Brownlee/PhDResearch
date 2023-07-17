
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: Bumps<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description:
 * 
 * From: Learning and optimization using the clonal selection principle (2002)
 * 
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class Bumps extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        return v[0] * Math.sin(4*Math.PI*v[0]) - v[1]*Math.sin(4*Math.PI*v[1]+Math.PI) + 1;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]                            
        {
                {-1, +2},
                {-1, +2}
        };
    }

    @Override
    protected double[][] preapreOptima()
    {
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
        return "Bumps";
    }
}
