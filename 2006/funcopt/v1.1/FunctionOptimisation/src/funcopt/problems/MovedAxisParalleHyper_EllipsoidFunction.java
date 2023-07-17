
package funcopt.problems;

import funcopt.Problem;

public class MovedAxisParalleHyper_EllipsoidFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // f1c(x)=sum(5*i·x(i)^2), i=1:n, -5.12<=x(i)<=5.12.
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            sum += 5.0 * i *Math.pow(v[i], 2);
        }
        
        return sum;
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
        // f(x)=0; x(i)= 5*i, i=1:n.
        
//        double [][] d = new double[1][dimensions];
//        for (int i = 0; i < d[0].length; i++)
//        {
//            d[0][i] = 5*(i+1);
//        }
//        return d;        
        
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
        return "Moved axis parallel hyper-ellipsoid function";
    }

}
