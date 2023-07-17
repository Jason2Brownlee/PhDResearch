
package funcopt.problems;

import funcopt.Problem;

/**
 * Type: ShekelsFoxholes<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ShekelsFoxholes extends Problem
{

    @Override
    protected double calculateCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double sum = 0.0;
        for (int i = 0; i < 25; i++)
        {           
            double a = 16.0 * ((i % 5.0) - 2.0);
            double b = 16.0 * ((i / 5) - 2.0);
            double lower = 1.0 + i + Math.pow((x - a), 6.0) + Math.pow((y - b), 6.0);
            sum += 1.0 / lower;
        }
        
        return (1.0 / (0.002 + sum));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-65.536, +65.535};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // TODO Auto-generated method stub
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
        return "Shekel's Foxholes";
    }
}
