
package jb.selfregulation.processes.listeners;

import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;


/**
 * Type: SimpleIterationStopCondition<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * 
 * Generic stop condition - stops after a defined number of iterations
 *  
 * <br/>
 * @author Jason Brownlee
 */
public class SimpleIterationStopCondition 
    implements LatticeStatusListener, Loggable
{    
    protected long totalSteps;
    protected boolean isShutdown;
    
    protected long count;
    
    protected final Logger logger;
    protected LinkedList<ParallelProcesses> processList;
    
    
    
    
    public SimpleIterationStopCondition()
    {
        logger = Logger.getLogger(LOG_CONFIG);      
    }   
    public String getBase()
    {
        return ".stopcondition";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        totalSteps = Long.parseLong(prop.getProperty(b + ".totalsteps"));;
    }
    public void setup(SystemState aState)
    {
        processList = aState.processes;
        count = 0;
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
        else if(++count >= totalSteps)
        {
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
