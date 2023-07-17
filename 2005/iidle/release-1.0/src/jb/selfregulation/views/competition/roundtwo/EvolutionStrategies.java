
package jb.selfregulation.views.competition.roundtwo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.problem.schwefels.Schwefels;
import jb.selfregulation.views.InterpolatedFunctionPlot;
import jb.selfregulation.views.competition.CompetitionFunction;
import jb.selfregulation.views.competition.OnlineSamplePlot;
import jb.selfregulation.views.competition.PointNotify;
import jb.selfregulation.views.competition.roundtwo.DifferentialEvolutionWithLocalSearch.Solution;

import org.spaceroots.mantissa.optimization.ConvergenceChecker;
import org.spaceroots.mantissa.optimization.CostException;
import org.spaceroots.mantissa.optimization.CostFunction;
import org.spaceroots.mantissa.optimization.NelderMead;
import org.spaceroots.mantissa.optimization.NoConvergenceException;
import org.spaceroots.mantissa.optimization.PointCostPair;
import org.spaceroots.mantissa.random.RandomVectorGenerator;

/**
 * Type: EvolutionStrategies<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class EvolutionStrategies
{
    protected Random r;
    protected Function problem;
    protected double [][] minmax;
    protected PointNotify plot;
    
    protected int initialPopulationSize;
    protected int populationSize;
    
    protected double tau;
    protected double eta;
    protected double rho;
    
    
    public void prepare(Function f, PointNotify pn)    
    {
        problem = f;
        plot = pn;
        minmax = f.getGenotypeMinMax();        
        r = new Random();
        initialPopulationSize = 10;
        populationSize = 10;
        
        int n = 2;
        tau = Math.pow(2.0*n, (-1.0/2.0));
        eta = Math.pow(4.0*n, (-1.0/4.0));
        rho = ((5*Math.PI)/180.0);
    }
    
    
    
    protected class Solution implements Comparable<Solution>
    {              
        public double [] coord;                
        public double score;        
        
        public double [] stdevs;
        public double [] directions;
        
        public Solution(double [] aCoord, double aScore)
        {
            this();
            coord = aCoord;
            score = aScore;
        }
        
        public Solution()
        {            
            coord = new double[2];
            score = 0;
            stdevs = new double[2];
            directions = new double[2];
            
            for (int i = 0; i < 2; i++)
            {
                stdevs[i] = (minmax[i][1]-minmax[i][0]) * r.nextDouble();
                directions[i] = (2*Math.PI) * r.nextDouble();
            }
        }
        
        
        
        @Override
        public boolean equals(Object o)
        {
            Solution s = (Solution) o;
            return coord[0] == s.coord[0] && coord[1] == s.coord[1];
        }
        
        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object
         * @param o
         * @return
         */
        public int compareTo(Solution o)
        {
            if(score < o.score)
            {
                return -1;
            }
            else if(score > o.score)
            {
                return +1;
            }
            return 0; // same
        }
        @Override
        public String toString()
        {
            return "x["+coord[0]+"], y["+coord[1]+"], f["+score+"]";
        }
    }   
    


    
    
    public Solution runAlgorithm()
    {
        problem.resetTotalEvaluations();
        
        LinkedList<Solution> pop = new LinkedList<Solution>();
        double [][] uniform = generateUniformPattern(initialPopulationSize);
        pop.addAll(evaluate(uniform));
        double [][] randoms = generateRandoms(initialPopulationSize);        
        pop.addAll(evaluate(randoms));
        
        Collections.sort(pop);
        Solution bestEver = pop.getFirst(); // min
        // reduce initial population size
        while(pop.size() > populationSize)
        {
            pop.removeLast();
        }
        int iterations = (1000 - (initialPopulationSize*2)) / populationSize;
        
        for (int i = 0; i < iterations; i++)
        {
            try{Thread.sleep(20);/*plot.clearPoints();*/}catch(Exception e){}
            
            // produce children
            pop = generateChildren(pop, bestEver);
            // check for new best
            Collections.sort(pop);
            Solution best = pop.getFirst();
            // check for new best ever
            if(best.score < bestEver.score)
            {
                bestEver = best;
            }            
        }
        
        return bestEver;
    }
    
    
    
    public LinkedList<Solution> generateChildren(
            LinkedList<Solution> pop,
            Solution best)
    {        
        // randomise parents
        Collections.shuffle(pop, r);
        LinkedList<Solution> cs = new LinkedList<Solution>();
        
        for (int i = 0; i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);
            
            // recombination
            Solution s1 = recombine(p1, p2);
            Solution s2 = recombine(p1, p2);
            
            // mutation            
            mutate(s1);
            mutate(s2);
            
            cs.add(s1);
            cs.add(s2);
        }
        
        // evaluation
        double [][] children = new double[pop.size()][];
        for (int i = 0; i < children.length; i++)
        {
            children[i] = cs.get(i).coord;
        }        
        LinkedList<Solution> c = evaluate(children);
        for (int i = 0; i < children.length; i++)
        {
            cs.get(i).score = c.get(i).score;
        }
        
        // selection
        cs.addAll(pop);
        Collections.sort(cs);
        while(cs.size() > pop.size())
        {
            cs.removeLast();
        }
        
        return cs;
    }
    
    protected void mutate(Solution s)
    {
        // mutate angles
        for (int i = 0; i < s.directions.length; i++)
        {
            s.directions[i] = (s.directions[i] + (rho*r.nextGaussian())) % (2.0*Math.PI);
        }
        
        // mutate stdev's
        double ztau = r.nextGaussian();
        for (int i = 0; i < s.stdevs.length; i++)
        {
            s.stdevs[i] = s.stdevs[i] * Math.exp((tau * ztau) + (eta*r.nextGaussian()));
        }        
        
        // mutate coords        
        for (int i = 0; i < s.coord.length; i++)
        {
            s.coord[i] = s.coord[i] + s.directions[i] * s.stdevs[i] * r.nextGaussian();
            
            // too large
            while(s.coord[i] > minmax[i][1])
            {
                s.coord[i] -= minmax[i][1]; // wrap
            }
            // too small
            while(s.coord[i] < minmax[i][0])
            {
                s.coord[i] += minmax[i][1]; // wrap
            }
        }
    }
    
    
    /**
     * Uniform crossover
     * @param p1
     * @param p2
     * @return
     */
    protected Solution recombine(Solution p1, Solution p2)
    {
        Solution s = new Solution();
        
        // coord
        for (int i = 0; i < p1.coord.length; i++)
        {
            s.coord[i] = (r.nextBoolean()) ? p1.coord[i] : p2.coord[i];
        }
        // stdev
        for (int i = 0; i < p1.stdevs.length; i++)
        {
            s.stdevs[i] = (r.nextBoolean()) ? p1.stdevs[i] : p2.stdevs[i];
        }
        // angles
        for (int i = 0; i < p1.directions.length; i++)
        {
            s.directions[i] = (r.nextBoolean()) ? p1.directions[i] : p2.directions[i];
        }
        
        return s;
    }
    
    
    
    
    public LinkedList<Solution> evaluate(double [][] v)
    {
        int remaining = (int) Math.min((1000 - problem.getTotalEvaluations()), v.length);
        LinkedList<double []> list = new LinkedList<double []>();
        for (int i = 0; i < remaining; i++)
        {
            list.add(v[i]);
        }
        double [] results = evaluate(list);
        LinkedList<Solution> sol = new LinkedList<Solution>();
        for (int i = 0; i < results.length; i++)
        {
            sol.add(new Solution(v[i], results[i]));
            if(plot!=null)
            {
                plot.addPoint(v[i], results[i]);
            }
        }
        return sol;
    }
    
    public double [] evaluate(LinkedList<double []> list)
    {
        // special case
        if(problem instanceof CompetitionFunction)
        {
            return ((CompetitionFunction)problem).evaluate(list);
        }
        // normal case
        double [] results = new double[list.size()];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = problem.evaluate(list.get(i));
        }
        return results;
    }
    
    
    public double [][] generateUniformPattern(int aTotalPoints)
    {
        double [][] coords = new double[aTotalPoints][2];
        
        double range = minmax[0][1] - minmax[0][0];
        
        int numPerDim = (int) Math.ceil(Math.sqrt(aTotalPoints));
        double increment = (1.0 / numPerDim) * range;
        int count = 0;
        for (int yy = 0; yy < numPerDim; yy++)
        {
            for (int xx = 0; xx < numPerDim && count < aTotalPoints; xx++)
            {
                coords[count][0] = minmax[0][0] + ((xx * increment) + (increment/2));
                coords[count][1] = minmax[0][0] + ((yy * increment) + (increment/2));
                count++;
            }
        }
        
        return coords;
    }
    
    public double [][] generateRandoms(int aNum)
    {                
        double [][] pop = new double[aNum][2];        
        for (int i = 0; i < pop.length; i++)
        {
            pop[i][0] = (r.nextDouble() * (minmax[0][1] - minmax[0][0]) + minmax[0][0]);
            pop[i][1] = (r.nextDouble() * (minmax[1][1] - minmax[1][0]) + minmax[1][0]);
        }        
        return pop;
    }
    
    
    
    
    protected static Function getOfflineTestFunction()
    {
        Schwefels problem = new Schwefels();
        problem.setRand(null);
        problem.setBitsPerVariate(63);
        problem.setNumDimensions(2);
        problem.setCycleLength(0);
        problem.setJitterPercentage(0.0);
        problem.prep();
        System.out.println("Best Possible: " + problem.getBestFitness());
        return problem;
    }
    protected static PointNotify getOfflinePlot(Function problem)
    {
        InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(problem, 100);
        JFrame j = new JFrame();
        j.setTitle("Plot");
        j.setSize(600, 600);
        j.getContentPane().add(plot);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setVisible(true);
        return plot;
    }
    protected static CompetitionFunction getOnlineTestFunction()
    {
        CompetitionFunction f = new CompetitionFunction();
        f.prepare();
        return f;
    }
    
    protected static PointNotify getOnlinePlot(Function problem)
    {
        OnlineSamplePlot plot = new OnlineSamplePlot();
        return plot;
    }
    
    
    
    public static void main(String[] args)
    {
        EvolutionStrategies f = new EvolutionStrategies();
        
        // offline test
        Function problem = getOfflineTestFunction();
        PointNotify plot = getOfflinePlot(problem);  
        f.prepare(problem, plot);
        Solution s = f.runAlgorithm();
        System.out.println(s);
        System.out.println("Evaluations: " + problem.getTotalEvaluations());
        
        
        // online test
//        CompetitionFunction problem = getOnlineTestFunction(); 
//        PointNotify plot = getOnlinePlot(problem);
//        problem.setMoon("20_1");
//        f.prepare(problem, plot);
//        Solution s = f.runAlgorithm();
//        System.out.println(s);
//        System.out.println("Evaluations: " + problem.getTotalEvaluations());
        
        
        // online - internal benchmark
//        CompetitionFunction problem = getOnlineTestFunction();
//        f.prepare(problem, null);
//        internalBenchmark(problem, f);
    }
    
    
    
    public final static int EXTERNAL_TOTAL_TESTS = 20;   
    
    public static void externalBenchmark(
            CompetitionFunction problem,
            EvolutionStrategies f)
    {   
        long start = System.currentTimeMillis();
        
        // benchmark - ONLINE
        problem.setMoon(null);
        
        for (int test = 0; test < EXTERNAL_TOTAL_TESTS; test++)
        {
            long s = System.currentTimeMillis();
            Solution result = f.runAlgorithm();
            long e = System.currentTimeMillis();
            System.out.println("Test["+test+"], " + result.score +", " + ((e-s)/1000.0)/60.0);                      
        }
        long end = System.currentTimeMillis();
        System.out.println("Total Time: " + ((end-start)/1000.0)/60.0 + "seconds");
    }
    
    
    public final static String [] MOONS = {"20_1", "20_2", "20_3", "20_4", "20_5", "20_6", "20_7", "20_8", "20_9", "20_10"};
    public final static int TOTAL_TESTS = 10;

    public static void internalBenchmark(
            CompetitionFunction problem,
            EvolutionStrategies f)
    {
        double [][] summary = new double[MOONS.length][TOTAL_TESTS];        
        long start = System.currentTimeMillis();
        for (int moon = 0; moon < MOONS.length; moon++)
        {
            problem.setMoon(MOONS[moon]);
            
            for (int test = 0; test < TOTAL_TESTS; test++)
            {
                long s = System.currentTimeMillis();
                Solution result = f.runAlgorithm();
                long e = System.currentTimeMillis();
                summary[moon][test] = result.score;
                System.out.println("Moon["+moon+"], Test["+test+"], " + result.score +", " + ((e-s)/1000.0)/60.0);
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
}

