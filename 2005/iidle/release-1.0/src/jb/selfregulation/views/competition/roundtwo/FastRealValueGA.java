
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

/**
 * Type: FastRealValueGA<br/>
 * Date: 24/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FastRealValueGA
{
    protected Random r;
    protected Function problem;
    protected double [][] minmax;
    protected PointNotify plot;
    
    protected double [] globalMutationStdev;
    protected int initialPopulationSize;
    protected int populationSize;
    protected int boutSize;
    
    
    protected class Solution implements Comparable<Solution>
    {
        public final double [] coord;
        public final double score;        
        
        public Solution(double [] aCoord, double aScore)
        {
            coord = aCoord;
            score = aScore;
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
    public double [] uniformCrossover(double [] p1, double [] p2)
    {
        return (r.nextBoolean()) ? new double[]{p1[0], p2[1]} : new double[]{p2[0], p1[1]};       
    }
    public void globalGaussianMutate(double [] p)
    {   
        for (int i = 0; i < p.length; i++)
        {
            p[i] += r.nextGaussian() * globalMutationStdev[i];
            
            // too large
            while(p[i] > minmax[i][1])
            {
                p[i] -= minmax[i][1]; // wrap
            }
            // too small
            while(p[i] < minmax[i][0])
            {
                p[i] += minmax[i][1]; // wrap
            }
        }
        
        // protection
//        if(p[0] < minmax[0][0] || p[0] > minmax[0][1] || 
//           p[1] < minmax[1][0] || p[1] > minmax[1][1])
//        {
//            throw new RuntimeException("Invalid coord: x["+p[0]+"], y["+p[1]+"]");
//        }
    }    
    public void prepare(Function f, PointNotify pn)    
    {
        problem = f;
        plot = pn;
        minmax = f.getGenotypeMinMax();        
        r = new Random();       
        globalMutationStdev = new double[2];
        setGlobalMutationStdev(0.01);
        initialPopulationSize = 500;
        populationSize = 20;
        boutSize = 2;
    }

    
    public void setGlobalMutationStdev(double d)
    {
        globalMutationStdev[0] = globalMutationStdev[1] 
                         = d * (minmax[0][1] - minmax[0][0]);
    }
    
    
    public Solution runAlgorithm()
    {
        problem.resetTotalEvaluations();
        double [][] iniitalPopulation = generateRandoms(initialPopulationSize);
        LinkedList<Solution> pop = evaluate(iniitalPopulation);
        Collections.sort(pop);
        Solution bestEver = pop.getFirst(); // min
        Solution lastBest = bestEver;
        // reduce initial population size
        while(pop.size() > populationSize)
        {
            pop.removeLast();
        }
        int iterations = (1000 - initialPopulationSize) / populationSize;
        
//        try{Thread.sleep(1000);plot.clearPoints();}catch(Exception e){}
        
        for (int i = 0; i < iterations; i++)
        {
//            try{Thread.sleep(1000);plot.clearPoints();}catch(Exception e){}
            
            // select parents
            LinkedList<Solution> parents = tournamentSelection(pop);
            // produce children
            pop = generateChildren(parents);
            // check for new best
            Collections.sort(pop);
            Solution best = pop.getFirst();
            // check for new best ever
            if(best.score < bestEver.score)
            {
                bestEver = best;
            }
            
            // adapt the mutation
            adaptMutation(best, lastBest);
            lastBest = best;
        }
        
        return bestEver;
    }
    
    
    protected void adaptMutation(Solution best, Solution lastBest)    
    {
        if(best.score < lastBest.score)
        {
            globalMutationStdev[0] = (globalMutationStdev[0]*0.75); // decrease
            globalMutationStdev[1] = (globalMutationStdev[1]*0.75); // decrease
        }
        else
        {
            globalMutationStdev[0] = (globalMutationStdev[0]*1.25); // increase
            globalMutationStdev[1] = (globalMutationStdev[1]*1.25); // increase
        }
        
//        System.out.println(globalMutationStdev);
    }
    
    
    
    public LinkedList<Solution> generateChildren(LinkedList<Solution> pop)
    {
        double [][] children = new double[pop.size()][];
        
        for (int i = 0; i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);
            
            for (int j = 0; j < 2; j++)
            {
                children[i+j] = uniformCrossover(p1.coord, p2.coord);
                globalGaussianMutate(children[i+j]);
            } 
        }
        
        LinkedList<Solution> c = evaluate(children);        
        return c;
    }
    
    public LinkedList<Solution> tournamentSelection(LinkedList<Solution> pop)
    {
        LinkedList<Solution> parents = new LinkedList<Solution>();
        
        while(parents.size() < pop.size())
        {
            Solution best = pop.get(Math.abs(r.nextInt())%pop.size());
            
            for (int i = 1; i < boutSize; i++)
            {
                Solution selection = pop.get(Math.abs(r.nextInt())%pop.size());
                if(selection.score < best.score)
                {
                    best = selection;
                }
            }
            
            parents.add(best);
        }
        
        return parents;
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
    
    
    public double [][] generateRandoms(int aNum)
    {        
        double [][] minmax = problem.getGenotypeMinMax(); 
        
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
    
    
    public final static String [] MOONS = {"20_1", "20_2", "20_3", "20_4", "20_5", "20_6", "20_7", "20_8", "20_9", "20_10"};
    public final static int TOTAL_TESTS = 10;
    
    public static void main(String[] args)
    {
        // offline
//        Function problem = getOfflineTestFunction();
//        PointNotify plot = getOfflinePlot(problem);
        
        // online
        CompetitionFunction problem = getOnlineTestFunction(); 
        FastRealValueGA f = new FastRealValueGA();
        
        
        // benchmark
//        f.prepare(problem, null);
//        internalBenchmark(problem, f);
        
        
        // normal
        PointNotify plot = getOnlinePlot(problem);
        problem.setMoon("20_1");
        f.prepare(problem, plot);               
        Solution s = f.runAlgorithm();
        System.out.println(s);
        System.out.println("Evaluations: " + problem.getTotalEvaluations());
    }
    
    
    public static void internalBenchmark(
            CompetitionFunction problem,
            FastRealValueGA f)
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

