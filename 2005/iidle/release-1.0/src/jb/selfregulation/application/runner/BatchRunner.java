
package jb.selfregulation.application.runner;

import java.util.logging.Level;
import java.util.logging.Logger;

import jb.selfregulation.application.ConfigurationFile;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.stats.IRunStatistics;
import jb.selfregulation.processes.ParallelProcesses;

/**
 * Type: BatchRunner<br/>
 * Date: <br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BatchRunner implements Loggable
{    
    protected final Logger logger;
    protected final String configFilename;
    protected ConfigurationFile config;
    protected IRunStatistics stats;
    protected int testNumber;
    
    public BatchRunner(String aConfigFilename)
    {
        // prepare the logger
        Util.prepareLogger();
        logger = Logger.getLogger(LOG_CONFIG);
        configFilename = aConfigFilename;
    }
    
    public static void checkUsage(String [] args)
    {
        if(args.length!=1)
        {
            System.out.println("Usage: java Runner <config file>");
            System.exit(0);
        }
    }
    
    public static void main(String[] args)
    {
        checkUsage(args);        
        BatchRunner r = new BatchRunner(args[0]);
        r.run();
    }
    
    protected void prepareConfig()
    {        
        config = new ConfigurationFile();
        config.load(configFilename);
    }
    
    protected void prepareRunStats()
        throws Exception
    {        
        stats = (IRunStatistics) Class.forName(config.getProp().getProperty(SystemState.APPLICATION_BASE + ".runs.statistics")).newInstance();
        stats.loadConfig(SystemState.APPLICATION_BASE, config.getProp());
        stats.setup(null);
    }
    
    public void run()
    {
        try
        {
            // prepare the configuration
            prepareConfig();
            // prepare the run statistics
            prepareRunStats();
            // run the main loop
            mainLoop();
            // do something with the results
            outputResults();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Fatal exception during batch run", e);
        }
    }
    
    protected void outputResults()
    {
        System.out.println("Results: ");
        System.out.println(stats.toString());
    }
    
    protected void mainLoop()
    {
        int runNumber = 0;
        int totalRuns = stats.getTotalRuns();
        long start = System.currentTimeMillis();
        long end;
        
        // do all the work            
        do
        {
            // perform run
            doRun();
            
            double percent = ((double)(runNumber+1) / (double)totalRuns) * 100.0; 
            if((percent%10)==0)
            {
                end = System.currentTimeMillis();
                System.out.println("Time: " + (end-start) + "ms, ("+percent+"%)");
                start = end;
            }            
        }
        while(++runNumber < totalRuns);
    }
    
    protected void preSetupInterception(SystemState aState)
    {
        aState.seed = ++testNumber;
    }
    
    protected void preConfigInterception(SystemState aState)
    {
        //  add stats as user datum
        aState.addUserDatum(IRunStatistics.KEY_RUN_STATISTICS, stats); 
    }
    
    protected void doRun()
    {
        SystemState systemState = new SystemState();
        // insertcept state before config;
        preConfigInterception(systemState);
        systemState.loadConfig(config); // load configuration      
        // intercept state before setup
        preSetupInterception(systemState);
        // setup
        systemState.setup(); // setup           
        // start things
        for(ParallelProcesses p : systemState.processes)
        {
            p.start();
        }               
        // wait for the processes to complete
        for(ParallelProcesses p : systemState.processes)
        {
            p.waitForStop();
        }  
    }
    
    public Logger getLogger()
    {
        return logger;
    }
}
