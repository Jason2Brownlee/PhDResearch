
package funcopt.problems;

import funcopt.Problem;


/**
 * Type: GoldsteinPricesFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GoldsteinPricesFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //   fGold(x1,x2)=[1+(x1+x2+1)^2·(19-14·x1+3·x1^2-14·x2+6·x1·x2+3·x2^2)]·
        //[30+(2·x1-3·x2)^2·(18-32·x1+12·x1^2+48·x2-36·x1·x2+27·x2^2)]
        
        return (1 + Math.pow(v[0]+v[1]+1,2) * (19-14*v[0]+3*Math.pow(v[0],2)-14*v[1]+6*v[0]*v[1]+3*Math.pow(v[1],2)))
        * Math.pow(30 + (2*v[0]-3*v[1]), 2)*(18-32*v[1]+12*Math.pow(v[0],2)+48*v[1]-36*v[0]*v[1]+27*Math.pow(v[1],2));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -2<=x(i)<=2, i=1:2.
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-2, +2};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=3; (x1,x2)=(0,-1).
        
        // wrong for sure!
        //return new double[][]{{0,-1}};
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
        return "Goldstein-Price's function";
    }

}
