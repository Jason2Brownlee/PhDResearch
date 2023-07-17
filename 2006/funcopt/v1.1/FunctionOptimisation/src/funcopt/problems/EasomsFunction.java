
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: EasomsFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class EasomsFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // fEaso(x1,x2)=-cos(x1)·cos(x2)·exp(-((x1-pi)^2+(x2-pi)^2))
        
        return -Math.cos(v[0])*Math.cos(v[1])*Math.exp(-(Math.pow(v[0]-Math.PI,2)+Math.pow(v[1]-Math.PI,2)));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -100<=x(i)<=100, i=1:2.
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-100, +100};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=-1; (x1,x2)=(pi,pi).
        return new double[][]{{Math.PI,Math.PI}};
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Easom's function";
    }

}
