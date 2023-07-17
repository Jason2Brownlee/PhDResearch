
package jb.selfregulation.application.runner;

import java.util.LinkedList;

import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.ConfigurationFile;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.frames.IIDLEMainFrame;
import jb.selfregulation.display.frames.MainFrame;
import jb.selfregulation.processes.ParallelProcesses;


/**
 * Type: Runner<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Runner
{
    public static void main(String[] args)
    {
        if(args.length<1)
        {
            System.out.println("Usage: java Runner <config file>");
            System.exit(0);
        }
        
        try
        {
            //Util.prepareLogger();
            ConfigurationFile config = new ConfigurationFile();
            config.load(args[0]);
            SystemState systemState = new SystemState();
            systemState.loadConfig(config); // load configuration
            systemState.setup(); // setup            
            // prepare the gui
            if(systemState.guiEnabled)
            {
                IIDLEMainFrame c = locateMainFrame(systemState.processes);
                c.finalPreperation(systemState);
                MainFrame f = new MainFrame(c);
                f.makeVisible();
            }
            // start things
            if(systemState.startOnRun)
            {
                for(ParallelProcesses p : systemState.processes)
                {
                    p.start();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    protected static IIDLEMainFrame locateMainFrame(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(LatticeStatusListener w : p.getListeners())
            {
                if(w instanceof IIDLEMainFrame)
                {
                    return (IIDLEMainFrame) w;
                }
            }
        }
        
        return null;
    }
}
