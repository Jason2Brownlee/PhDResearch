
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: SixHumpCamelBackFunction<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SixHumpCamelBackFunction extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        //fSixh(x1,x2)=(4-2.1·x1^2+x1^4/3)·x1^2+x1·x2+(-4+4·x2^2)·x2^2
        
        double x = v[0];
        double y = v[1];
        
        double a = (4.0-2.1*(x*x)+(x*x*x*x)/3.0) * (x*x)+x*y + (-4+4*(y*y)) * (y*y);
        return a;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -3<=x1<=3, -2<=x2<=2.
        return new double[][]{{-3, +3}, {-2, +2}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=-1.0316; (x1,x2)=(-0.0898,0.7126), (0.0898,-0.7126).
        
        return new double[][]
        {
                {-0.0898,0.7126},
                {0.0898,-0.7126}
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
        return "Six-Hump Camel Back Function";
    }

}
