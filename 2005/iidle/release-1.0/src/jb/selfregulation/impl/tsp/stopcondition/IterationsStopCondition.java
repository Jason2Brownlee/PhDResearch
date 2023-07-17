
package jb.selfregulation.impl.tsp.stopcondition;

import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.stats.SimpleRunStatistics;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.problem.TSPTourEvaluator;
import jb.selfregulation.processes.ParallelProcesses;


/**
 * Type: ConvergenceStopCondition<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class IterationsStopCondition 
    implements LatticeStatusListener, Loggable
{    
    protected long totalSteps;
    protected boolean isShutdown;
    
    protected long count;
    protected long totalFunctionEvaluations;
    protected double lastBestScore;
    
    protected final Logger logger;
    protected final TSPTourEvaluator eval;
    protected LinkedList<ParallelProcesses> processList;
    protected Problem problem;
    
    protected SimpleRunStatistics runStats;
    
    public IterationsStopCondition()
    {
        logger = Logger.getLogger(LOG_CONFIG);
        eval = new TSPTourEvaluator();        
    }
    
    
    public String getBase()
    {
        return ".stopcondition";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        totalSteps = Long.parseLong(prop.getProperty(b + ".totalsteps"));         
    }

    public void setup(SystemState aState)
    {
        processList = aState.processes;
        problem = (TSPProblem) aState.problem;
        runStats = (SimpleRunStatistics) aState.getUserDatum(SimpleRunStatistics.KEY_RUN_STATISTICS);
        
        // clear things
        lastBestScore = Double.MAX_VALUE;
        count = 0;
        totalFunctionEvaluations = 0;
        
        eval.setProblem((TSPProblem)problem);
    }
    

    public void latticeChangedEvent(Lattice aLattice)
    {
        // check for case where this thread gets context switched before
        // it can stop everything in the stop thread.
        // obviously this thread (process thread) cannot join the shutdown thread...
        if(isShutdown)
        {
            return;
        }
        
        // clear old stats
        eval.reset();
        // collect stats
        aLattice.getPerformRoughVisit(eval);
        // complete collection
        eval.finished();
        
        
        /*
        // check for improvement
        if(eval.tourMin < lastBestScore)
        {
            lastBestScore = eval.tourMin;
            count = 0;
            totalFunctionEvaluations = problem.getTotalTourEvaluations();
        }        
        else if(++count >= windowSize)
        {
            if(runStats != null)
            {
                int [] stats = aLattice.getRoughSystemState();
                runStats.addRunStatistic(lastBestScore, totalFunctionEvaluations, stats[0]);
                logger.warning("Stop condition reached, statistics updated score["+lastBestScore+"], evals["+totalFunctionEvaluations+"].");
            }
            else
            {
                logger.info("Stop condition reached, no run statistics, score["+lastBestScore+"], evals["+totalFunctionEvaluations+"].");
            }
            // shut the thing down
            shutdown();
            isShutdown = true;
        }
        */
        
        // check for improvement       
        if(eval.tourMin < lastBestScore)
        {
            
            lastBestScore = eval.tourMin;
//            System.out.println(lastBestScore);
//            count = 0;
            totalFunctionEvaluations = problem.getTotalEvaluations();
        } 
        
        else if(++count >= totalSteps)
        {
            if(runStats != null)
            {
                int [] stats = aLattice.getRoughSystemState();
                runStats.addRunStatistic(lastBestScore, totalFunctionEvaluations, stats[0]);
                logger.warning("Stop condition reached, statistics updated score["+lastBestScore+"], evals["+totalFunctionEvaluations+"].");
            }
            else
            {
                logger.info("Stop condition reached, no run statistics, score["+lastBestScore+"], evals["+totalFunctionEvaluations+"].");
            }
            // shut the thing down
            shutdown();
            isShutdown = true;
        }
    }
    
    protected void shutdown()
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                for(ParallelProcesses p : processList)
                {
                    p.stopAndWait();
                }
                
//                printAllThreads();
            }
        };
        Thread t = new Thread(r);
        t.setName("StopConditionThread");
        t.start(); // shut it all down
    }
    

    protected void printAllThreads()
    {        
        int total = Thread.activeCount();
        Thread [] t = new Thread[total];
        Thread.enumerate(t);
        System.out.println("Threads List:");
        for (int i = 0; i < t.length; i++)
        {
            System.out.println("Thread ("+i+") " + t[i].getName());
        }
    }
    
    

    
    public Logger getLogger()
    {
        return logger;
    }
}
