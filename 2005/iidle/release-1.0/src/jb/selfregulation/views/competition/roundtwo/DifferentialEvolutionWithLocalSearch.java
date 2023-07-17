
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

import org.spaceroots.mantissa.optimization.ConvergenceChecker;
import org.spaceroots.mantissa.optimization.CostException;
import org.spaceroots.mantissa.optimization.CostFunction;
import org.spaceroots.mantissa.optimization.NelderMead;
import org.spaceroots.mantissa.optimization.NoConvergenceException;
import org.spaceroots.mantissa.optimization.PointCostPair;
import org.spaceroots.mantissa.random.RandomVectorGenerator;

/**
 * Type: DifferentialEvolution<br/>
 * Date: 6/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DifferentialEvolutionWithLocalSearch
{
    protected Random r;
    protected Function problem;
    protected double [][] minmax;
    protected PointNotify plot;
    
    protected int initialPopulationSize;
    protected int populationSize;    
    protected double CR;
    protected double F;
    protected double K;
    
    protected int localSearchIterations;
    
    
    
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
    
    public void prepare(Function f, PointNotify pn)    
    {
        problem = f;
        plot = pn;
        minmax = f.getGenotypeMinMax();        
        r = new Random();
        initialPopulationSize = 100;
        populationSize = 20;
        CR = 0.8;
        F = 0.8;
        K = 0.5;
        localSearchIterations = 5;
    }

    
    
    public Solution runAlgorithm()
    {
        problem.resetTotalEvaluations();
        double [][] iniitalPopulation = generateRandoms(initialPopulationSize);
        LinkedList<Solution> pop = evaluate(iniitalPopulation);
        Collections.sort(pop);
        Solution bestEver = pop.getFirst(); // min
        // reduce initial population size
        while(pop.size() > populationSize)
        {
            pop.removeLast();
        }
        int iterations = (1000 - initialPopulationSize) / populationSize;
        // local search iterations
        iterations -= localSearchIterations;        
        
        for (int i = 0; i < iterations; i++)
        {
//            try{Thread.sleep(200);/*plot.clearPoints();*/}catch(Exception e){}
            
            // produce children
            pop = generateChildren(pop);
            // check for new best
            Collections.sort(pop);
            Solution best = pop.getFirst();
            // check for new best ever
            if(best.score < bestEver.score)
            {
                bestEver = best;
            }            
        }
        
        Solution best = runLocalSearch(localSearchIterations * populationSize, bestEver);
        
//        System.out.println("Last Best:   " + bestEver.score);
//        System.out.println("New Best:    " + best.score);
//        System.out.println("Change:      " + Math.abs(bestEver.score - best.score));
        
        if(best.score < bestEver.score)
        {
            bestEver = best;            
        }
        else
        {
//            System.out.println("Local search gave no improvement");
        }
        
        return bestEver;
    }
    
    
    public Solution runLocalSearch(
            int maxEvaluations, 
            Solution currentBest)
    {
        Solution best = null;
        
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
            F f = new F();
            Checker c = new Checker(maxEvaluations);
            double [][] verticies = prepareVerticies(currentBest);
            PointCostPair pair = search.minimizes(f, Integer.MAX_VALUE, c, verticies);
            best = new Solution(pair.getPoint(), pair.getCost());
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
        return best;
    }
    
    public final static double RADIUS = 0.005;
    
    protected double [][] prepareVerticies(Solution s)
    {
        double [] c = s.coord;        
        return new double[][]
        { 
                {c[0]-RADIUS, c[1]+RADIUS}, // top left
                {c[0]+RADIUS, c[1]+RADIUS}, // top right
                {c[0], c[1]-RADIUS} // foward
        };
    }
    
    protected class F implements CostFunction
    {        
        public double cost(double[] x) throws CostException
        {
            double [][] coords = new double[][]{{x[0], x[1]}};
            LinkedList<Solution> s = evaluate(coords);
            
            if(s.isEmpty())
            {
                return Double.POSITIVE_INFINITY;
            }
            
            double score = s.getFirst().score;
            return score;
        }        
    }
    
    protected class Checker implements ConvergenceChecker
    {
        protected int totalEvals;
        protected int count;
        
        public Checker(int aEvals)
        {
            totalEvals = aEvals;
        }

        public boolean converged(PointCostPair[] simplex)
        {
            for (int i = 0; i < simplex.length; i++)
            {
                if(Double.isInfinite(simplex[i].getCost()))
                {                    
                    return true;
                }
            }            
            
            if(++count >= totalEvals)
            {                
                return true;
            }
            return false;
        }        
    }
    
    public LinkedList<Solution> generateChildren(LinkedList<Solution> pop)
    {
        // DE_RAND_1_BIN
        return DE_RAND_1_BIN(pop);
        
        // DE_Current_to_RAND
//        return DE_Current_to_RAND(pop);
        
        // DE_RAND_1_EXP
//        return DE_RAND_1_EXP(pop);
        
        // DE_CURRENT_TO_RAND_1_BIN
//        return DE_CURRENT_TO_RAND_1_BIN(pop);
    }
    
    
    public LinkedList<Solution> DE_RAND_1_BIN(LinkedList<Solution> pop)
    {
        int NP = pop.size();
        int D = 2;
        
        double [][] children = new double[NP][];
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3;
            do{r1=(Math.abs(r.nextInt())%NP);}while(r1==i);
            do{r2=(Math.abs(r.nextInt())%NP);}while(r2==i||r2==r1);
            do{r3=(Math.abs(r.nextInt())%NP);}while(r3==i||r3==r1||r3==r2);
            
            Solution p0 = pop.get(i);
            Solution p1 = pop.get(r1);
            Solution p2 = pop.get(r2);
            Solution p3 = pop.get(r3);
            
            children[i] = new double[D];          
            
            int j = (int) (r.nextDouble() * D); // random starting point
            for (int k = 1; k <= D; k++)
            {
                if(r.nextDouble() < CR || k == D)
                {
                    children[i][j] = p3.coord[j] + F * (p1.coord[j] - p2.coord[j]);
                }
                else
                {
                    children[i][j] = p0.coord[j];
                }
                
                // too large
                while(children[i][j] > minmax[j][1])
                {
                    children[i][j] -= minmax[j][1]; // wrap
                }
                // too small
                while(children[i][j] < minmax[j][0])
                {
                    children[i][j] += minmax[j][1]; // wrap
                }                
                
                // check bounds
                j = (j + 1) % D; // wrap                
            }
        }
        
        LinkedList<Solution> c = evaluate(children);     
        
        LinkedList<Solution> n = new LinkedList<Solution>();
        for (int i = 0; i < c.size(); i++)
        {
            Solution s1 = pop.get(i);
            Solution s2 = c.get(i);
            n.add((s1.score<s2.score) ? s1 : s2);
        }        
        
        return n;
    }
    
    
    public LinkedList<Solution> DE_Current_to_RAND(LinkedList<Solution> pop)
    {
        int NP = pop.size();
        int D = 2;
        
        double [][] children = new double[NP][];
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3;
            do{r1=(Math.abs(r.nextInt())%NP);}while(r1==i);
            do{r2=(Math.abs(r.nextInt())%NP);}while(r2==i||r2==r1);
            do{r3=(Math.abs(r.nextInt())%NP);}while(r3==i||r3==r1||r3==r2);
            
            Solution p0 = pop.get(i);
            Solution p1 = pop.get(r1);
            Solution p2 = pop.get(r2);
            Solution p3 = pop.get(r3);
            
            children[i] = new double[D];          
            
            for (int j = 0; j < D; j++)
            {
                // randomise K
                K = r.nextDouble();
                
                children[i][j] = p0.coord[j] + 
                    (K * (p3.coord[j] - p0.coord[j])) + 
                    (F * (p1.coord[j] - p2.coord[j]));
                
                // too large
                while(children[i][j] > minmax[j][1])
                {
                    children[i][j] -= minmax[j][1]; // wrap
                }
                // too small
                while(children[i][j] < minmax[j][0])
                {
                    children[i][j] += minmax[j][1]; // wrap
                }
            }
        }
        
        LinkedList<Solution> c = evaluate(children);     
        
        LinkedList<Solution> n = new LinkedList<Solution>();
        for (int i = 0; i < c.size(); i++)
        {
            Solution s1 = pop.get(i);
            Solution s2 = c.get(i);
            n.add((s1.score<s2.score) ? s1 : s2);
        }        
        
        return n;
    }
    
    
    
    public LinkedList<Solution> DE_RAND_1_EXP(LinkedList<Solution> pop)
    {
        int NP = pop.size();
        int D = 2;
        
        double [][] children = new double[NP][];
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3;
            do{r1=(Math.abs(r.nextInt())%NP);}while(r1==i);
            do{r2=(Math.abs(r.nextInt())%NP);}while(r2==i||r2==r1);
            do{r3=(Math.abs(r.nextInt())%NP);}while(r3==i||r3==r1||r3==r2);
            
            Solution p0 = pop.get(i);
            Solution p1 = pop.get(r1);
            Solution p2 = pop.get(r2);
            Solution p3 = pop.get(r3);
            
            children[i] = new double[D];          
            
            int j = (int) (r.nextDouble() * D); // random starting point
            int flag = 0;
            for (int k = 1; k <= D; k++)
            {
                if(r.nextDouble() < CR || k == D)
                {
                    flag = 1;
                }
                if(flag == 1)
                {
                    children[i][j] = p3.coord[j] + F * (p1.coord[j] - p2.coord[j]);
                }
                else
                {
                    children[i][j] = p0.coord[j];
                }
                
                // too large
                while(children[i][j] > minmax[j][1])
                {
                    children[i][j] -= minmax[j][1]; // wrap
                }
                // too small
                while(children[i][j] < minmax[j][0])
                {
                    children[i][j] += minmax[j][1]; // wrap
                }                
                
                // check bounds
                j = (j + 1) % D; // wrap                
            }
        }
        
        LinkedList<Solution> c = evaluate(children);     
        
        LinkedList<Solution> n = new LinkedList<Solution>();
        for (int i = 0; i < c.size(); i++)
        {
            Solution s1 = pop.get(i);
            Solution s2 = c.get(i);
            n.add((s1.score<s2.score) ? s1 : s2);
        }        
        
        return n;
    }
    
    
    public LinkedList<Solution> DE_CURRENT_TO_RAND_1_BIN(LinkedList<Solution> pop)
    {
        int NP = pop.size();
        int D = 2;
        
        double [][] children = new double[NP][];
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3;
            do{r1=(Math.abs(r.nextInt())%NP);}while(r1==i);
            do{r2=(Math.abs(r.nextInt())%NP);}while(r2==i||r2==r1);
            do{r3=(Math.abs(r.nextInt())%NP);}while(r3==i||r3==r1||r3==r2);
            
            Solution p0 = pop.get(i);
            Solution p1 = pop.get(r1);
            Solution p2 = pop.get(r2);
            Solution p3 = pop.get(r3);
            
            children[i] = new double[D];          
            
            int j = (int) (r.nextDouble() * D); // random starting point
            for (int k = 1; k <= D; k++)
            {
                if(r.nextDouble() < CR || k == D)
                {
                    // randomise K
                    K = r.nextDouble();
                    
                    children[i][j] = p0.coord[j] +  (K * (p3.coord[j] - p0.coord[j])) + (F * (p1.coord[j] - p2.coord[j]));
                }
                else
                {
                    children[i][j] = p0.coord[j];
                }
                
                // too large
                while(children[i][j] > minmax[j][1])
                {
                    children[i][j] -= minmax[j][1]; // wrap
                }
                // too small
                while(children[i][j] < minmax[j][0])
                {
                    children[i][j] += minmax[j][1]; // wrap
                }                
                
                // check bounds
                j = (j + 1) % D; // wrap                
            }
        }
        
        LinkedList<Solution> c = evaluate(children);     
        
        LinkedList<Solution> n = new LinkedList<Solution>();
        for (int i = 0; i < c.size(); i++)
        {
            Solution s1 = pop.get(i);
            Solution s2 = c.get(i);
            n.add((s1.score<s2.score) ? s1 : s2);
        }        
        
        return n;
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
        
        
        DifferentialEvolutionWithLocalSearch f = new DifferentialEvolutionWithLocalSearch();
        
        
        // internal benchmark
//        f.prepare(problem, null);
//        internalBenchmark(problem, f);
        
        // external benchmark
        f.prepare(problem, null);
        externalBenchmark(problem, f);
        
        
        // normal
//        PointNotify plot = getOnlinePlot(problem);
//        problem.setMoon("20_1");        
//        f.prepare(problem, plot);               
//        Solution s = f.runAlgorithm();
//        System.out.println(s);
//        System.out.println("Evaluations: " + problem.getTotalEvaluations());
    }
    
    
    
    public final static int EXTERNAL_TOTAL_TESTS = 20;   
    public static void externalBenchmark(
            CompetitionFunction problem,
            DifferentialEvolutionWithLocalSearch f)
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
    
    
    
    public static void internalBenchmark(
            CompetitionFunction problem,
            DifferentialEvolutionWithLocalSearch f)
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

