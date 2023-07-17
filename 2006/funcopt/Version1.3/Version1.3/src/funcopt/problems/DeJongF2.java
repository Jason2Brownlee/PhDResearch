
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: DeJongF2<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Rosenbrock's valley (De Jong's function 2)
 * <br/>
 * @author Jason Brownlee
 */
public class DeJongF2 extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // Rosenbrock's valley (De Jong's function 2)
        //
        // f2(x)=sum(100·(x(i+1)-x(i)^2)^2+(1-x(i))^2)
        // i=1:n-1; -2.048<=x(i)<=2.048.
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions-1; i++)
        {
            sum += 100 * Math.pow((v[i+1] - Math.pow(v[i],2)),2) + Math.pow(1 - v[i], 2);
        }
        
        return sum; 
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-2.048, +2.048};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0; x(i)=1, i=1:n.
        
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = 1;
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
        return "De Jong F2 (Rosenbrock's valley)";
    }

}
