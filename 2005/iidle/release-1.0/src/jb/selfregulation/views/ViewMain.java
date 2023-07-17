
package jb.selfregulation.views;


import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.problem.schwefels.Schwefels;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Min;

/**
 * Type: ViewMain<br/>
 * Date: 20/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ViewMain
{
    public final static int TOTAL_TESTS = 100;
    
    
    protected Function getTestFunction(Random r)
    {
        Schwefels problem = new Schwefels();
        problem.setRand(r);
        problem.setBitsPerVariate(32);
        problem.setNumDimensions(2);
        problem.setCycleLength(0);
        problem.setJitterPercentage(0.0);
        problem.prep();
        System.out.println("Best Possible: " + problem.getBestFitness());
        return problem;
    }
    
    protected InterpolatedFunctionPlot preparePlot(Function problem)
    {
        InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(problem, 100);
        JFrame j = new JFrame();
        j.setTitle("Plot");
        j.setSize(640, 480);
        j.getContentPane().add(plot);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setVisible(true);
        return plot;
    }
    
    public SimpleGeneticAlgorithm prepareGA(Function p)
    {
        SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm();        
        ga.setConvergenceWindowSize(100);
        ga.setPopulationSize(300);
        ga.setCrossoverPercentage(1.0);
        ga.setMutationPercentage(0.01);
        ga.setTotalElites(1);
        ga.setTournamentSize(3);
        return ga;
    }
    
    public void reportResult(
            double [] scores,
            double [] iterations,
            Function problem)
    {
        System.out.println("------------------------------------------------");
        System.out.println("Completed:     " + TOTAL_TESTS);
        System.out.println("Best Possible: " + problem.getBestFitness());
        System.out.println("Best:          " + new Min().evaluate(scores));
        System.out.println("Evaluations:   " + problem.getTotalEvaluations());        
        System.out.println("Scores:        " + new Mean().evaluate(scores) + " ("+new StandardDeviation().evaluate(scores)+")");
        System.out.println("Iterations:    " + new Mean().evaluate(iterations) + " ("+new StandardDeviation().evaluate(iterations)+")");

    }
    
    
    
    public void runNormalGA()
    {        
        Random r = new Random();        
        Function problem = getTestFunction(r);
        InterpolatedFunctionPlot plot = null;
        SimpleGeneticAlgorithm ga = prepareGA(problem);
        
        if(problem.getNumDimensions() == 2)
        {
            plot = preparePlot(problem);
        }
        
        double [] iterations = new double[TOTAL_TESTS];
        double [] scores = new double[TOTAL_TESTS];
        
        for (int i = 0; i < TOTAL_TESTS; i++)
        {
            Result result = ga.runGA(problem, r);
            scores[i] = result.bestScore;
            iterations[i] = result.iterations;
            System.out.println((i+1)+ ", " + Math.round(scores[i]) +", " + Math.round(iterations[i]));
            if(problem.getNumDimensions() == 2)
            {
                plot.updateWithPoint(result.best);
            }
        }
        
        reportResult(scores, iterations, problem);
    }
    
    
    
    
    
    public void runViewAlgorithmGA()
    {        
        int viewPopulationSize = 20;
        
        Random r = new Random();        
        Function problem = getTestFunction(r);
        InterpolatedFunctionPlot plot = null;
        InterpolatedFunctionPlot viewplot = null;
        SimpleGeneticAlgorithm ga = prepareGA(problem);
        
        if(problem.getNumDimensions() == 2)
        {
            plot = preparePlot(problem);
            viewplot = preparePlot(problem);
        }
        
        double [] iterations = new double[TOTAL_TESTS];
        double [] scores = new double[TOTAL_TESTS];
        
        LinkedList<ViewFunction> views = new LinkedList<ViewFunction>();
        LinkedList<ViewFunction> children = new LinkedList<ViewFunction>();
        
        double [] coord = new double[problem.getNumDimensions()];
        for (int i = 0; i < coord.length; i++)
        {
            coord[i] = 0.0;
        }
        views.add(new ViewFunction(problem, problem.getGenotypeMinMax(), coord, problem.evaluate(coord)));
        for (int i = 0; i < viewPopulationSize-1; i++)
        {
            ViewFunction v = calculateNewView(problem, null, r);
            views.add(v);
//            views.add(new ViewFunction(problem, problem.getGenotypeMinMax(), coord, problem.evaluate(coord)));
        }
        
        for (int i = 0; i < TOTAL_TESTS; i++)
        {            
            // swap when needed
            if(views.isEmpty())
            {
                views.addAll(children);
                children.clear();
            }
            
            ViewFunction view = views.remove(Math.abs(r.nextInt()) % views.size());
            if(plot != null)
            {
                plot.updateWithView(view);                
                LinkedList<ViewFunction> v = new LinkedList<ViewFunction>();
                v.addAll(views);
                v.addAll(children);
                viewplot.refreshAllViews(v);
            }
            
            // run
            Result result = ga.runGA(view, r);
            scores[i] = result.bestScore;
            iterations[i] = result.iterations;
            System.out.println((i+1)+ ", " + Math.round(scores[i]) +", " + Math.round(iterations[i]));
            
//            if(view.isOutOfBounds(result.best.getVectorData()))
//            {
//                throw new RuntimeException("Value out of bounds");
//            }
            
            if(plot != null)
            {
                plot.updateWithPoint(result.best);
            }
            
            // create a progeny view         
            ViewFunction v = calculateNewView(problem, result.best, r);
            children.add(v);            
        }
        
        reportResult(scores, iterations, problem);
    }
    
    protected ViewFunction calculateNewView(
            Function problem, 
            FuncOptUnit best,
            Random r)
    {
        double [] coord = null;
        double [][] minmax = problem.getGenotypeMinMax();
        
        if(best == null)
        {
            coord = new double[minmax.length];
            for (int i = 0; i < coord.length; i++)
            {
                // random point
                double d = r.nextDouble();
                coord[i] = (d * (minmax[i][1] - minmax[i][0])) + minmax[i][0];
            }
        }
        else
        {
            coord = best.getVectorData();
        }        
        
        // now build a boundary
        double [][] newMinMax = new double[minmax.length][2];
        for (int i = 0; i < newMinMax.length; i++)
        {
            double range = (minmax[i][1] - minmax[i][0]);
            double width = r.nextDouble() * range;
            
            newMinMax[i][0] = Math.max(minmax[i][0], coord[i]-(width/2));
//            if(problem.isOutOfBounds(i, newMinMax[i][0]))
//            {
//                throw new RuntimeException("Out of bounds d["+i+"], v["+newMinMax[i][0]+"]");
//            }
            newMinMax[i][1] = Math.min(minmax[i][1], coord[i]+(width/2));
//            if(problem.isOutOfBounds(i, newMinMax[i][1]))
//            {
//                throw new RuntimeException("Out of bounds d["+i+"], v["+newMinMax[i][1]+"]");
//            }
        } 
        
        ViewFunction v =  new ViewFunction(problem, newMinMax, coord, problem.evaluate(coord));
        return v;
    }
    
    
    
    
    public static void main(String[] args)
    {
        ViewMain m = new ViewMain();
        
        // bench mark
        /*  20D Function
            Completed:     100
            Best Possible: -8379.657745443252
            Best:          -8276.186765955466
            Evaluations:   187844020
            Scores:        -7585.517470624378 (345.9024162046889)
            Iterations:    468.61 (159.59607441819165)
         */
//        m.runNormalGA();
        
        
        // bench mark
        /*
         * 
         * 
         */
        m.runViewAlgorithmGA();
    }
}
