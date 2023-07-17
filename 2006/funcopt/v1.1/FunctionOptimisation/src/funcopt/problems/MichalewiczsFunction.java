
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: MichalewiczsFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MichalewiczsFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        // f12(x)=-sum(sin(x(i))·(sin(i·x(i)^2/pi))^(2·m)), i=1:n, m=10
        // 0<=x(i)<=pi.
        
        double m = 10;
        double sum = 0;
        for (int i = 0; i < dimensions; i++)
        {
            double inner = (i * Math.pow(v[i],2)) / Math.PI;             
            sum += Math.sin(v[i]) * Math.pow(Math.sin(inner), 2*m);
        }
        return -sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{0, Math.PI};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=-4.687 (n=5); x(i)=???, i=1:n.
        // f(x)=-9.66 (n=10); x(i)=???, i=1:n.
        
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
        return "Michalewicz's Function";
    }
}
