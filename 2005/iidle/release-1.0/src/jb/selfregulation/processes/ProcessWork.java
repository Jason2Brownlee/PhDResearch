
package jb.selfregulation.processes;

import java.util.Properties;
import java.util.logging.Logger;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;

/**
 * Type: ProcessWork<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class ProcessWork 
    implements Loggable, Configurable
{   
    protected final LastRunStats lastRunStats;
    protected final Logger logger;
    
    protected volatile long numProcessIterations;    
    
    
    
    public ProcessWork()
    {
        logger = Logger.getLogger(LOG_CONFIG);
        lastRunStats = prepareLastRunStats();
    }
    
    
    public String getBase()
    {
        return "";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        // amplitude
        numProcessIterations = Integer.parseInt(prop.getProperty(aBase + ".amplitude"));        
    }
    public void setup(SystemState aState)
    {
        // nothing
    }


    public void executeProcess(Lattice aLattice)
    {
        // reset run stats
        lastRunStats.rest();
        
        // execute the process
        for (int i = 0; i < numProcessIterations; i++)
        {
            executeProcessRun(aLattice);
        }
    }
    
    protected abstract void executeProcessRun(Lattice aLattice);
    
    protected abstract LastRunStats prepareLastRunStats();

    /**
     * @return Returns the numProcessIterations.
     */
    public long getNumProcessIterations()
    {
        return numProcessIterations;
    }
    /**
     * @param numProcessIterations The numProcessIterations to set.
     */
    public void setNumProcessIterations(int numProcessIterations)
    {
        this.numProcessIterations = numProcessIterations;
    }
    
    protected abstract class LastRunStats
    {
        public abstract void rest();
    }
    
    public Logger getLogger()
    {
        return logger;
    }
}
