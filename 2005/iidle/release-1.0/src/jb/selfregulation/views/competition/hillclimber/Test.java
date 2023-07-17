package jb.selfregulation.views.competition.hillclimber;
import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.impl.functopt.problem.schwefels.Schwefels;
import jb.selfregulation.views.InterpolatedFunctionPlot;

import org.spaceroots.mantissa.optimization.ConvergenceChecker;
import org.spaceroots.mantissa.optimization.CostException;
import org.spaceroots.mantissa.optimization.CostFunction;
import org.spaceroots.mantissa.optimization.NelderMead;
import org.spaceroots.mantissa.optimization.NoConvergenceException;
import org.spaceroots.mantissa.optimization.PointCostPair;
import org.spaceroots.mantissa.random.RandomVectorGenerator;

/**
 * Type: Test<br/>
 * Date: 27/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Test
{
    public static void main(String[] args)
    {
        try
        {
            /**
             * @param rho reflection coefficient
             * @param khi expansion coefficient
             * @param gamma contraction coefficient
             * @param sigma shrinkage coefficient
             */
            double rho   = 1.0;
            double khi   = 2.0;
            double gamma = 0.5;
            double sigma = 0.5;
            NelderMead search = new NelderMead(rho, khi, gamma, sigma);        
            Function f = new Function();
            int maxEvaluations = 100;
            Checker c = new Checker();
            Generator g = new Generator();
            int totalRestarts = 1;
            PointCostPair pair = search.minimizes(f, maxEvaluations, c, g, totalRestarts);
            System.out.println("Final Score: " + pair.getCost());
        }
        catch (CostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoConvergenceException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }
    
    
    
    protected static class Function implements CostFunction
    {
        public static double bestEver = -837.965774544325;
        InterpolatedFunctionPlot plot;
        
        public Function()
        {
            Schwefels problem = new Schwefels();
            problem.setRand(null);
            problem.setBitsPerVariate(63);
            problem.setNumDimensions(2);
            problem.setCycleLength(0);
            problem.setJitterPercentage(0.0);
            problem.prep();
            plot = new InterpolatedFunctionPlot(problem, 100);
            JFrame j = new JFrame();
            j.setTitle("Plot");
            j.setSize(600, 600);
            j.getContentPane().add(plot);
            j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            j.setVisible(true);
            
            System.out.println("BE: " + bestEver);
        }
        
        
        public double cost(double[] x) throws CostException
        {
            double sum = 0;        
            for (int i = 0; i < x.length; i++)
            {
                sum += (-x[i]) * Math.sin(Math.sqrt(Math.abs(x[i])));
            }
                        
            plot.addPoint(x);
            System.out.println("b["+bestEver+"], l["+x.length+"], x["+x[1]+"], y["+x[1]+"], f["+sum+"]");
            
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return sum;
        }        
    }
    
    protected static class Checker implements ConvergenceChecker
    {

        public boolean converged(PointCostPair[] simplex)
        {
            for (int i = 0; i < simplex.length; i++)
            {
                if(simplex[i].getCost() == Function.bestEver)
                {
                    return true;
                }
            }
            
            return false;
        }
        
    }
    
    protected static class Generator implements RandomVectorGenerator
    {
        Random r = new Random();

        public double[] nextVector()
        {
           return new double[]{v(),v()};
        }
        
        public double v()
        {
            return r.nextDouble() * (500 - -500) + -500;
        }
        
        
    }
}
