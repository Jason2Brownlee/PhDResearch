
package jb.selfregulation.views.competition;

import java.util.Random;

import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.views.Result;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Min;


/**
 * Type: TestMain<br/>
 * Date: 22/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TestGA
{   
    public final static String [] MOONS = {"20_1", "20_2", "20_3", "20_4", "20_5", "20_6", "20_7", "20_8", "20_9", "20_10"};
    public final static int TOTAL_TESTS = 10; 
    
    
    protected Function getTestFunction(Random r)
    {
        CompetitionFunction problem = new CompetitionFunction();
        problem.setRand(r);
        problem.prepare();
        return problem;
    }
    
    public GA prepareGA()
    {
        GA ga = new GA();
        ga.setInitialPopulationSize(250);
        ga.setPopulationSize(20);
        ga.setCrossoverPercentage(1.0);
        ga.setMutationPercentage(0.01);
        ga.setTotalElites(1);
        ga.setTournamentSize(3);
        return ga;
    }
    
    public void reportResult(
            double [] scores,
            double [] iterations)
    {
        System.out.println("------------------------------------------------");
        System.out.println("Completed:     " + TOTAL_TESTS);
        System.out.println("Best:          " + new Min().evaluate(scores));        
        System.out.println("Scores:        " + new Mean().evaluate(scores) + " ("+new StandardDeviation().evaluate(scores)+")");
        System.out.println("Iterations:    " + new Mean().evaluate(iterations) + " ("+new StandardDeviation().evaluate(iterations)+")");

    }
    
    
    public void runTestGA()
    {
        Random r = new Random();               
        GA ga = prepareGA();
        Function problem = getTestFunction(r);        
        ((CompetitionFunction)problem).setMoon("20_1");
        
        double [] iterations = new double[TOTAL_TESTS];
        double [] scores = new double[TOTAL_TESTS];
        
        for (int i = 0; i < TOTAL_TESTS; i++)
        {
            ((CompetitionFunction)problem).reset();
            long start = System.currentTimeMillis();
            Result result = ga.runGA(problem, r);
            long end = System.currentTimeMillis();
            scores[i] = result.bestScore;
            iterations[i] = result.iterations;
            System.out.println((i+1)+ ", " + scores[i] +", " + Math.round(iterations[i]) + ", " + ((end-start)/1000.0)/60.0);
        }
            
        reportResult(scores, iterations);        
    }
    
    public void runGA()
    {
        Random r = new Random();               
        GA ga = prepareGA();
        Function problem = getTestFunction(r);        
        ((CompetitionFunction)problem).setMoon("20_1");
        
        ((CompetitionFunction)problem).reset();
        long start = System.currentTimeMillis();
        Result result = ga.runGA(problem, r);
        long end = System.currentTimeMillis();
        System.out.println("Result: " + result.bestScore +", " + result.iterations + ", " + ((end-start)/1000.0)/60.0);
        double [] v = result.best.getVectorData();
        System.out.println("Coord: x["+v[0]+"], y["+v[1]+"]");
    }
    
    public void runHomeBenchmarkGA()
    {        
        double [][] summary = new double[MOONS.length][TOTAL_TESTS];
        
        Random r = new Random();               
        GA ga = prepareGA();
        Function problem = getTestFunction(r);  
        long start = System.currentTimeMillis();
        
        for (int moon = 0; moon < MOONS.length; moon++)
        {
            ((CompetitionFunction)problem).setMoon(MOONS[moon]);
            
            for (int test = 0; test < TOTAL_TESTS; test++)
            {
                ((CompetitionFunction)problem).reset();
                long s = System.currentTimeMillis();
                Result result = ga.runGA(problem, r);
                long e = System.currentTimeMillis();
                summary[moon][test] = result.bestScore;
                System.out.println("Moon["+moon+"], Test["+test+"], " + result.bestScore +", " + Math.round(result.iterations) + ", " + ((e-s)/1000.0)/60.0);
            }            
        }
        long end = System.currentTimeMillis();
        System.out.println("Total Time: " + ((end-start)/1000.0)/60.0 + "seconds");
        

        for (int moon = 0; moon < summary.length; moon++)
        {
            for (int test = 0; test < summary[moon].length; test++)
            {
                System.out.print(summary[moon][test]);
                if(test < summary[test].length-1)
                {
                    System.out.print(", ");
                }
            }            
            System.out.println();
        }
    }
    
    public void runHomeBenchmarkAlgorithm2Competition()
    {        
        double [][] summary = new double[MOONS.length][TOTAL_TESTS];
        
        Random r = new Random();               
        //Algorithm2TestForCompetition a = Algorithm2TestForCompetition();
        CompetitionFunction p = (CompetitionFunction) getTestFunction(r);  
        long start = System.currentTimeMillis();
        
        for (int moon = 0; moon < MOONS.length; moon++)
        {
            p.setMoon(MOONS[moon]);
            
            for (int test = 0; test < TOTAL_TESTS; test++)
            {
                p.resetTotalEvaluations();
                Algorithm2TestForCompetition a = new Algorithm2TestForCompetition();
                long s = System.currentTimeMillis();
                Result result = a.main(p,r);
                long e = System.currentTimeMillis();
                summary[moon][test] = result.bestScore;
                System.out.println("Moon["+moon+"], Test["+test+"], " + result.bestScore +", " + Math.round(result.iterations) + ", " + ((e-s)/1000.0)/60.0);
            }            
        }
        long end = System.currentTimeMillis();
        System.out.println("Total Time: " + ((end-start)/1000.0)/60.0 + "seconds");
        

        for (int moon = 0; moon < summary.length; moon++)
        {
            for (int test = 0; test < summary[moon].length; test++)
            {
                System.out.print(summary[moon][test]);
                if(test < summary[test].length-1)
                {
                    System.out.print(", ");
                }
            }            
            System.out.println();
        }
    }
    
    public static void main(String[] args)
    {
        TestGA m = new TestGA();
//        m.runHomeBenchmarkGA();     
        m.runHomeBenchmarkAlgorithm2Competition();
//        m.runGA();
    }
}
