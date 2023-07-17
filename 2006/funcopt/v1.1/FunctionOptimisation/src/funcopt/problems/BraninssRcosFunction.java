
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: BraninssRcosFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BraninssRcosFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //   fBran(x1,x2)=a·(x2-b·x1^2+c·x1-d)^2+e·(1-f)·cos(x1)+e
        // a=1, b=5.1/(4·pi^2), c=5/pi, d=6, e=10, f=1/(8·pi)
        // -5<=x1<=10, 0<=x2<=15.

        double a = 1;
        double b = 5.1 / (4*Math.pow(Math.PI, 2));
        double c = 5 / Math.PI;
        double d = 6;
        double e = 10;
        double f = 1 / (8 * Math.PI);
        
        return a*Math.pow(v[1]-b*Math.pow(v[0],2)+c*v[0]-d,2)+e*(1-f)*Math.cos(v[0])+e;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -5<=x1<=10, 0<=x2<=15
        return new double[][]{{-5, +10}, {0, +15}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=0.397887; (x1,x2)=(-pi,12.275), (pi,2.275), (9.42478,2.475).
        
        return new double[][]
        {
                {-Math.PI,12.275},
                {Math.PI,2.275},
                {9.42478,2.475}
        };
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Branins's Rcos Function";
    }
}
