
package jb.selfregulation.processes;

import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;

/**
 * Type: ParallelProcesses<br/>
 * Date: 7/06/2005<br/>
 * <br/>
 * Description: Used for running multiple pieces of work at the same times.  
 * Can be stopped, paused and restarted
 * <br/>
 * @author Jason Brownlee
 */
public class ParallelProcesses extends BaseProcess 
    implements Runnable, Loggable, Configurable
{
    protected volatile long waitTime;
    protected volatile boolean canRun;
    protected volatile boolean paused;
    protected volatile boolean isStopped;
    
    protected final LinkedList<LatticeStatusListener> listeners;
    protected final Logger logger;
    
    protected Thread internalThread;        
    
    protected Lattice lattice;
    protected String name;

    public ParallelProcesses(long aProcessId)
    {
        super(aProcessId);
        logger = Logger.getLogger(LOG_CONFIG); 
        listeners = new LinkedList<LatticeStatusListener>();

    }    

    public void start()
    {
        if(!isStopped)
        {
            throw new RuntimeException("Unable to start, not stopped.");
        }
        
        if(canRun)
        {
            throw new RuntimeException("Unable to start, already running.");
        }
        
        canRun = true;
        paused = false;    
        isStopped = false;
        internalThread = new Thread(this);
        internalThread.setName("Thread - " + name);
        internalThread.start();
        logger.info("Started process thread :: " + name);
    }
    

    public String getBase()
    {
        return ".process." + processId;
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        
        listeners.clear(); // clear everything
        work.clear();
        
        name = prop.getProperty(b + ".name");
        waitTime = Long.parseLong(prop.getProperty(b + ".frequency"));
        
        try
        {
            // prepare work
            int processTotalWork = Integer.parseInt(prop.getProperty(b+".work.total"));
            for (int i = 0; i < processTotalWork; i++)
            {
                String nb = b + ".work." + i;
                // create the work
                ProcessWork work = (ProcessWork) Class.forName(prop.getProperty(nb + ".classname")).newInstance();
                // load the configuration
                work.loadConfig(nb, prop);
                // add the work
                addWork(work);
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to prepare work.", e);
        }
        
        try
        {
            // prepare work
            int totalListeners = Integer.parseInt(prop.getProperty(b+".listeners.total"));
            for (int i = 0; i < totalListeners; i++)
            {
                String nb = b + ".listener." + i;
                // create the listener
                LatticeStatusListener listener = (LatticeStatusListener) Class.forName(prop.getProperty(nb + ".classname")).newInstance();
                // load the configuration
                listener.loadConfig(nb, prop);
                // add the listener
                addListener(listener);
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to prepare listeners.", e);
        }      
    }
    
    
    public void setup(SystemState aState)
    {
        lattice = aState.lattice;
     
        canRun = false;
        paused = false;        
        isStopped = true;
        
        // setup all work
       for(ProcessWork w : work)
       {
           w.setup(aState);
       }        
        // setup all listeners
       for(LatticeStatusListener l : listeners)
       {
           l.setup(aState);
       }
    }
    
    
    

    public void run()
    {
        // run the algorithm
        while (canRun)
        {
            // check for pause
            checkForPause();            
            if(!canRun){continue;}
            
            // execute processes
            stepProcess(lattice);
            if(!canRun){continue;}
            
            // update listeners
            updateListeners();
            if(!canRun){continue;}
            
            // wait for some time
            waitForSomeTime();
        }
        
        // tell anyone waiting we have stopped
        stopped();
    }
    
    protected void stopped()
    {
        synchronized(this)
        {
            isStopped = true;
            logger.info("Stopped process thread :: " + name);
            this.notifyAll();
        }
    }

    protected void checkForPause()
    {
        synchronized (this)
        {
            if(paused)
            {
                logger.info("Paused process thread :: " + name);
                
                while (paused && canRun)
                {
                    try
                    {
                        this.wait(5000);
                    }
                    catch (InterruptedException e)
                    {}
                }
                
                logger.info("Unpaused process thread :: " + name);
            }
        }
    }

    protected void updateListeners()
    {
        for (LatticeStatusListener l : listeners)
        {
            l.latticeChangedEvent(lattice);
        }
    }

    protected void waitForSomeTime()
    {
        long entry = System.currentTimeMillis();

        synchronized (this)
        {
            while (canRun && System.currentTimeMillis() < (entry + waitTime))
            {
                long timeToWait = (entry + waitTime) - System.currentTimeMillis();

                try
                {
                    if(timeToWait > 10)
                    {
                        this.wait(timeToWait);
                    }
                }
                catch (InterruptedException e)
                {}
            }
        }
    }

    public long getWaitTime()
    {
        return waitTime;
    }

    public void setWaitTime(long waitTime)
    {
        synchronized (this)
        {
            this.waitTime = waitTime;
            this.notify();
        }
    }

    public boolean isCanRun()
    {
        return canRun;
    }

    public void stopAndWait()
    {        
        synchronized (this)
        {
            if(!canRun)
            {
                throw new RuntimeException("Cannot stop, not running.");
            }
            
            this.paused = false;
            this.canRun = false;
            this.notify(); // tell threads we have stopped
        }
        
        // wait for stop
        waitForStop();
    }
    
    public void waitForStop()
    {
        synchronized (this)
        {
            while (!isStopped)
            {
                try
                {
                    this.wait(5000);
                }
                catch (InterruptedException e)
                {}
            }
        }
    }

    public void addListener(LatticeStatusListener l)
    {
        if(l != null)
        {
            listeners.add(l);
        }
    }

    public boolean isPaused()
    {
        return paused;
    }
    public boolean isStopped()
    {
        return isStopped;
    }

    public void setPaused(boolean paused)
    {
        synchronized (this)
        {
            this.paused = paused;
            this.notify();
        }
    }

    public Thread getInternalThread()
    {
        return internalThread;
    }

    public long getTime()
    {
        return waitTime;
    }

    public void setTime(long aTime)
    {
        synchronized (this)
        {
            this.waitTime = aTime;
            this.notify();
        }
    }
    
    public Logger getLogger()
    {
        return logger;
    }

    public Lattice getLattice()
    {
        return lattice;
    }

    public void setLattice(Lattice lattice)
    {
        this.lattice = lattice;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LinkedList<LatticeStatusListener> getListeners()
    {
        return listeners;
    }
    
    
}
