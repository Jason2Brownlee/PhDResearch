
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
 * Type: SimulatedAnnealing<br/>
 * Date: 9/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimulatedAnnealing
{
    protected Random r;
    protected Function problem;
    protected double [][] minmax;
    protected PointNotify plot;
    
    protected int initialPopulationSize;

    protected double temperature;
    protected double tempAdjustment;
    protected int stepSizeWindow;
    protected double stepSize;
    protected double c;
    
//    protected double [] stepSize = new double[2];
//    protected double Ns;
//    protected double c;
    
    public void prepare(Function f, PointNotify pn)    
    {
        problem = f;
        plot = pn;
        minmax = f.getGenotypeMinMax();        
        r = new Random();
        initialPopulationSize = 100;
        
        temperature = ((minmax[0][1] - minmax[0][0]) / 4);
        tempAdjustment = 0.85;
        stepSizeWindow = 40;
        c = 2;
        stepSize = ((minmax[0][1] - minmax[0][0]) / 2);
    }
    
    
    
    
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
    


    

    protected double[] generateNextSample(Solution aCurrentSample)
    {
        // duplicate the current coordinate
        double [] coord = new double[2];
        System.arraycopy(aCurrentSample.coord, 0, coord, 0, coord.length);
        
        // re-generate for all axis (not one axis at a time as in convetional SA)
        for (int axis = 0; axis < coord.length; axis++)
        {
            double v = 0.0;
           
            // generate new point                
            double rand = r.nextDouble();
            rand *= (r.nextBoolean()) ? -1 : +1;
            v = coord[axis] + rand * stepSize;
            
            // wrap
            while(v > minmax[axis][1])
            {
                v -= minmax[axis][1]; // wrap
            }
            // too small
            while(v < minmax[axis][0])
            {
                v += minmax[axis][1]; // wrap
            }
            
            coord[axis] = v; // store
        }

        
        return coord;
//        Solution newCurrent = singleEval(coord);
//        return newCurrent;
    }
    
    
    public boolean shouldAccept(Solution current, Solution newCurrent)
    {
        // check if just better
        if(newCurrent.score < current.score) // better
        {
            return true;
        }
        
        if(newCurrent.score > current.score) // worse
        {
            // exp(-delta f / T)
            double p = Math.exp(-Math.abs(current.score - newCurrent.score) / temperature);
            if(r.nextDouble() < p)
            {
                return true;
            }
        }
        
        return false;
    }
    
    

    
    public Solution runAlgorithm()
    {
        problem.resetTotalEvaluations();
//        double [][] iniitalPopulation = generateRandoms(initialPopulationSize);
//        double [][] iniitalPopulation = generateUniformPattern(initialPopulationSize);
//        LinkedList<Solution> pop = evaluate(iniitalPopulation);
        
        
        LinkedList<Solution> pop = new LinkedList<Solution>();
        double [][] uniform = generateUniformPattern(initialPopulationSize);
        pop.addAll(evaluate(uniform));
        double [][] randoms = generateRandoms(initialPopulationSize);
        pop.addAll(evaluate(randoms));
        
        Collections.sort(pop);
        Solution bestEver = pop.getFirst(); // min
        
        Solution current = bestEver;
        int steps = (1000 - (initialPopulationSize*2));         
        int accepted = 0;
        
        if((steps%stepSizeWindow)!=0)
        {
            throw new RuntimeException("I'm lazy, and i want remaining steps as a factor of "+stepSizeWindow+"!");
        }
        
        for (int i = 0; i < (steps/stepSizeWindow); i++)
        {   
            try{Thread.sleep(50);}catch(Exception e){}
            
            double [][] sss = new double[stepSizeWindow][];
            for (int j = 0; j < sss.length; j++)
            {
                sss[j] = generateNextSample(current); 
            }
            LinkedList<Solution> solutions = evaluate(sss);
            for(Solution s : solutions)
            {
                // check if its the best thing ever seen
                if(s.score < bestEver.score)
                {
                    bestEver = s;
                } 
                // check if it should be kept
                if(shouldAccept(current, s))
                {
                    current = s;
                    accepted++;
                }
            }
            
            double acceptedRatio = ((double)accepted / (double)stepSizeWindow);
            double newStepSize = 0.0;
            
            // check if the ratio is too high
            if(acceptedRatio > 0.60) 
            {
                // accepting too many - tighten up (decrease)
                newStepSize = stepSize*(1.0+c*((acceptedRatio-0.6)/0.4));
            }
            // check if ratio is too low
            else if(acceptedRatio < 0.40)
            {
                // not accepting enough - open up (increase)
                newStepSize = (stepSize/(1.0+c*((0.4-acceptedRatio)/0.4)));
            }
            // otherwise in the sweet spot - stay the same
            else
            {
                newStepSize = stepSize;
            }
            stepSize = newStepSize;
            
            if(stepSize > (minmax[0][1] - minmax[0][0]))
            {
                stepSize = (minmax[0][1] - minmax[0][0]);
            }
//                else if(stepSize < 0.00001)
//                {
//                    stepSize = 0.00001;
//                }
            
            System.out.println("t["+temperature+"], a["+accepted+"], s["+stepSize+"] F["+current.score+"]");
            
            // adapt temperature
            temperature *= tempAdjustment;
            accepted = 0;
            
        }
        
        return bestEver;
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
    
    
    public Solution singleEval(double [] coord)
    {
        double result = problem.evaluate(coord);
        Solution s = new Solution(coord, result);
        if(plot!=null)
        {
            plot.addPoint(coord, result);
        }
        return s;
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
        SimulatedAnnealing f = new SimulatedAnnealing();
        
        
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
            SimulatedAnnealing f)
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
            SimulatedAnnealing f)
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

