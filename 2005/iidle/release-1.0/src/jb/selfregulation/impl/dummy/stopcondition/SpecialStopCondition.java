
package jb.selfregulation.impl.dummy.stopcondition;

import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.stats.IRunStatistics;
import jb.selfregulation.impl.dummy.display.SpecialUnitLatticeEvaluator;
import jb.selfregulation.impl.dummy.stats.TakeoverTimeStatistics;
import jb.selfregulation.processes.ParallelProcesses;


/**
 * Type: SpecialStopCondition<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * 
 * Stops after a special units have taken over, or after all special units have been removed
 * After the time the first special unit is detected, the system will stop
 * when special units have taken over or after all special units go missing
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class SpecialStopCondition 
    implements LatticeStatusListener, Loggable
{    
    protected boolean isShutdown;
    protected int waitTime;
    protected boolean started;
    protected int maxIterationWait;
    protected int iterationCount;
    
    protected final Logger logger;
    protected LinkedList<ParallelProcesses> processList;
    protected SpecialUnitLatticeEvaluator eval;
    
    protected TakeoverTimeStatistics stats;
    
    
    
    public SpecialStopCondition()
    {
        logger = Logger.getLogger(LOG_CONFIG);     
        eval = new SpecialUnitLatticeEvaluator();
    }   
    public String getBase()
    {
        return ".stopcondition";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        maxIterationWait = Integer.parseInt(prop.getProperty(b+".maxwait"));
    }
    public void setup(SystemState aState)
    {
        processList = aState.processes;
        stats = (TakeoverTimeStatistics) aState.getUserDatum(IRunStatistics.KEY_RUN_STATISTICS);
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

        // check the state of the world
        eval.reset();
        aLattice.getPerformRoughVisit(eval);
        eval.finished();
        
        // check if already started
        if(started)
        {
            // failure - lost special after getting special
            if(eval.totalSpecial == 0)
            {
                updateStats(-1);
                shutdown();
            }
            // success
            else if(eval.totalNotSpecial == 0)
            {
                updateStats(waitTime);
                shutdown();
            }
            
            ++waitTime;
            // waited to long
            if(waitTime >= maxIterationWait)
            {
                updateStats(-1);
                shutdown();
            }            
        }
        else if(eval.totalSpecial != 0)
        {
            waitTime++;
            started = true;
        }        
        // note - it is possible that given that the initial detection of special is 
        // based on a rough visit, that the inital speical trigger could not be detected
        // this little stop gap prevents this from causing the batch config to run forever
        else if(++iterationCount > (2*maxIterationWait))
        {
            updateStats(-1);
            shutdown();
        }
    }
    
    protected void updateStats(int aVal)
    {
        stats.updateStatistic(aVal);
    }
    
    protected void shutdown()
    {
        isShutdown = true;
        Runnable r = new Runnable()
        {
            public void run()
            {
                for(ParallelProcesses p : processList)
                {
                    p.stopAndWait();
                }
            }
        };
        Thread t = new Thread(r);
        t.setName("StopConditionThread");
        t.start(); // shut it all down
    }    
    public Logger getLogger()
    {
        return logger;
    }
}
