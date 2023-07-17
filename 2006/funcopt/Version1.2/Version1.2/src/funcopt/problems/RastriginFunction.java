
package funcopt.problems;

import funcopt.Problem;

public class RastriginFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // f6(x)=10·n+sum(x(i)^2-10·cos(2·pi·x(i))), i=1:n; -5.12<=x(i)<=5.12.
        
        double sum = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum += (v[i]*v[i]) - 10.0 * Math.cos(2.0*Math.PI*v[i]);
        }
        return (10.0 * dimensions) + sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-5.12, +5.12};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        //  f(x)=0; x(i)=0, i=1:n.
        
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
        return "Rastrigin's Function";
    }

}
