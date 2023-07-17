
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
 * Type: PSOLocalSearch<br/>
 * Date: 8/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class PSOLocalSearch
{
    protected Random r;
    protected Function problem;
    protected double [][] minmax;
    protected PointNotify plot;
    
    protected int initialPopulationSize;
    protected int populationSize; 
    
    protected double vMax;
    protected double c1;
    protected double c2;    
    protected double momentum;
    public double RADIUS;
    
    protected int localSearchIterations;
    
    
    
    protected class Solution implements Comparable<Solution>
    {
        public double [] velocity;        
        public double [] pbestcoord;
        public double pbestScore;
        
        public final double [] coord;
        public final double score;        
        
        public Solution(double [] aCoord, double aScore)
        {
            coord = aCoord;
            score = aScore;
            
            velocity = new double[coord.length];
            pbestcoord = new double[coord.length];
            
            // randomised initial velocity (until replaced)
            velocity[0] = (r.nextDouble() * (vMax*0.5));
            velocity[0] *= (r.nextBoolean() ? -1 : 1);
            velocity[1] = (r.nextDouble() * (vMax*0.5));
            velocity[1] *= (r.nextBoolean() ? -1 : 1);
            
            // personal best, is current until otherwise stated
            pbestScore = aScore;
            System.arraycopy(coord, 0, pbestcoord, 0, coord.length);
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
        initialPopulationSize = 200;
        populationSize = 40;
        localSearchIterations = 2;
        
        RADIUS = 0.005 * (minmax[0][1] - minmax[0][0]);
        vMax = (minmax[0][1] - minmax[0][0]) * 1.0;
        c1 = 2;
        c2 = 2;
        momentum = 0.4;
    }

    
    
    public Solution runAlgorithm()
    {
        problem.resetTotalEvaluations();
        double [][] iniitalPopulation = generateRandoms(initialPopulationSize);
//        double [][] iniitalPopulation = generateUniformPattern(initialPopulationSize);
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
        
        Solution best = runLocalSearch(localSearchIterations * populationSize, bestEver);
        
//        System.out.println("Last Best:   " + bestEver.score);
//        System.out.println("New Best:    " + best.score);
//        System.out.println("Change:      " + Math.abs(bestEver.score - best.score));
        
        if(best.score < bestEver.score)
        {
            bestEver = best;
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
            e.printStackTrace();
        }
        catch (NoConvergenceException e)
        {
            e.printStackTrace();
        }
        return best;
    }
    
    
    
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
    
    public LinkedList<Solution> generateChildren(
            LinkedList<Solution> pop,
            Solution best)
    {        
        int T = pop.size();
        double [][] children = new double[T][2];
        
        // create children one at a time
        // basically copy parents, thne update position and velocity        
        for (int k = 0; k < T; k++)
        {
            Solution s = pop.get(k);
            double [] position = s.coord;
            double [] velocity = s.velocity;
            double [] pBestPos = s.pbestcoord;
            double [] bestPos = best.coord;
            
            // child's position is changed
            System.arraycopy(position, 0, children[k], 0, position.length);
            
            // update velocity
            for (int i = 0; i < velocity.length; i++)
            {
                // update velocity
                velocity[i] = 
                  (momentum*velocity[i]) + // how much of the previous velocity
                  (1-momentum)* 
//                    velocity[i] + 
                  (
                  + (c1 * r.nextDouble() * (pBestPos[i] - position[i])) 
                  + (c2 * r.nextDouble() * (bestPos[i] - position[i]))
                  );
                
                // bound velocity
                if(velocity[i] > vMax)
                {
                    velocity[i] = vMax;
                }
                else if(velocity[i] < -vMax)
                {
                    velocity[i] = -vMax;
                }                
            }
            
            // update position
            for (int i = 0; i < children[k].length; i++)
            {                
                // update position
                children[k][i] = children[k][i] + velocity[i];
                
                // wrap to torrid
                
                // too large
                while(children[k][i] > minmax[i][1])
                {
                    children[k][i] -= minmax[i][1]; // wrap
                }
                // too small
                while(children[k][i] < minmax[i][0])
                {
                    children[k][i] += minmax[i][1]; // wrap
                }
            }
        }
        
        // evaluate all children
        LinkedList<Solution> c = evaluate(children);
        
        // process children so that they get correct velocities
        for (int i = 0; i < c.size(); i++)
        {
            Solution parent = pop.get(i);
            Solution child = c.get(i);
            // copy velocity
            System.arraycopy(parent.velocity, 0, child.velocity, 0, parent.velocity.length);
            // update personal best
            if(child.score < parent.pbestScore)
            {
                // take current position as pbest
                // do nothing - this occurs on construction
            }
            else
            {
                // take parents p-best coord and p-best score
                System.arraycopy(parent.pbestcoord, 0, child.pbestcoord, 0, parent.pbestcoord.length);
                child.pbestScore = parent.pbestScore;
            }
        }
        
        return c;
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
        // offline test
//        Function problem = getOfflineTestFunction();
//        PointNotify plot = getOfflinePlot(problem);        
//        PSOLocalSearch f = new PSOLocalSearch();
//        f.prepare(problem, plot);
//        Solution s = f.runAlgorithm();
//        System.out.println(s);
//        System.out.println("Evaluations: " + problem.getTotalEvaluations());
        
        
        // online test
//        CompetitionFunction problem = getOnlineTestFunction(); 
//        PointNotify plot = getOnlinePlot(problem);
//        problem.setMoon("20_1");
//        PSOLocalSearch f = new PSOLocalSearch();
//        f.prepare(problem, plot);
//        Solution s = f.runAlgorithm();
//        System.out.println(s);
//        System.out.println("Evaluations: " + problem.getTotalEvaluations());
        
        
        // online - internal benchmark
        CompetitionFunction problem = getOnlineTestFunction();
        PSOLocalSearch f = new PSOLocalSearch();
        f.prepare(problem, null);
        internalBenchmark(problem, f);
    }
    
    
    
    public final static int EXTERNAL_TOTAL_TESTS = 20;   
    
    public static void externalBenchmark(
            CompetitionFunction problem,
            PSOLocalSearch f)
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
            PSOLocalSearch f)
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

