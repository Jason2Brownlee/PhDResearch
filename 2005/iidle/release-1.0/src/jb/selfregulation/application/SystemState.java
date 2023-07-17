
package jb.selfregulation.application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import jb.selfregulation.Lattice;
import jb.selfregulation.UnitFactory;
import jb.selfregulation.application.network.P2PLattice;
import jb.selfregulation.application.stats.SimpleRunStatistics;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.work.ProcessExpansion;


/**
 * Type: SystemState<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SystemState implements Configurable, Loggable
{
    public final static String KEY_FEEDBACK_PANELS = "FEEDBACK_PANELS";
    public final static String KEY_COMMON_PANELS = "COMMON_PANELS";
    public final static String KEY_PROBLEM_PANELS = "PROBLEM_PANELS";
    public final static String KEY_GRAPH_PANELS = "GRAPH_PANELS";
    
    public final static String KEY_PROCESS_STIMULATION = "STIMULATION_PROCESS";
    
    
    public final static String APPLICATION_BASE = "app.system";    
        
    protected final Logger logger;
    public final LinkedList<ParallelProcesses> processes;
    public final HashMap<String, Object> userDatum;    
    
    public long seed;
    public String name;
    public int totalProcesses;
    
    public boolean guiEnabled;
    public boolean startOnRun;
    
    public Lattice lattice;
    public Problem problem;
    public Random rand;
    public UnitFactory unitFactory;
    public P2PLattice p2pNetwork;

    
    
    public SystemState()
    {
        logger = Logger.getLogger(LOG_CONFIG);   
        userDatum = new HashMap<String, Object>(); 
        processes = new LinkedList<ParallelProcesses>();
        // prepare gui things
        userDatum.put(KEY_FEEDBACK_PANELS, new LinkedList<JComponent>());
        userDatum.put(KEY_COMMON_PANELS, new LinkedList<JComponent>());
        userDatum.put(KEY_PROBLEM_PANELS, new LinkedList<JComponent>());
        userDatum.put(KEY_GRAPH_PANELS, new LinkedList<LineGraph>());
        userDatum.put(KEY_PROCESS_STIMULATION, new HashMap<Long, ProcessExpansion>());
    }
    
    
    public void loadConfig(ConfigurationFile aFile)
    {
        loadConfig("", aFile.getProp());
        logger.info("Configuration loaded.");
    }
    public void setup()
    {
        setup(null);
        logger.info("Setup complete.");
    }
    
    public String getBase()
    {
        return APPLICATION_BASE;
    }
    
    public void setup(SystemState aState)
    {
        prepareRandom();
        problem.setup(this);
        unitFactory.setup(this);
        lattice.setup(this);
        p2pNetwork.setup(this);
        // processes
        for(ParallelProcesses p : processes)
        {
            p.setup(this);
        }
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        String b = (aBase + getBase());
        
        try
        {
            // create things
            seed = loadSeed(b, prop);            
            name = prop.getProperty(b + ".name");
            guiEnabled = Boolean.parseBoolean(prop.getProperty(b + ".gui.enabled"));
            startOnRun = Boolean.parseBoolean(prop.getProperty(b + ".startonrun"));
            totalProcesses = Integer.parseInt(prop.getProperty(b + ".processes.total"));
            unitFactory = (UnitFactory) Class.forName(prop.getProperty(b + ".unitfactory.classname")).newInstance();
            lattice = (Lattice) Class.forName(prop.getProperty(b + ".lattice.classname")).newInstance();
            problem = (Problem) Class.forName(prop.getProperty(b + ".problem.classname")).newInstance();
            p2pNetwork = new P2PLattice();
            // load things
            unitFactory.loadConfig(b, prop);
            lattice.loadConfig(b, prop);
            problem.loadConfig(b, prop);
            p2pNetwork.loadConfig(b, prop);
            
            // do processes
            for (int i = 0; i < totalProcesses; i++)
            {
                // create
                ParallelProcesses process = new ParallelProcesses(i);
                // load
                process.loadConfig(b, prop);
                // store
                processes.add(process);
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to prepare configuration", e);
        }        
    }

    
    protected long loadSeed(String b, Properties prop)
    {
        String s = prop.getProperty(b + ".random.seed");
        if(s.equalsIgnoreCase("time"))
        {
            return System.currentTimeMillis();
        }
        return Long.parseLong(s);
    }
    
    public Logger getLogger()
    {
        return logger;
    } 
    
    protected void prepareRandom()
    {
        rand = new Random(seed);
    }
    
    public void addUserDatum(String key, Object data)
    {
        userDatum.put(key, data);
    }
    public Object getUserDatum(String key)
    {
        return userDatum.get(key);
    }
    
    
}
