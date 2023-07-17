
package jb.selfregulation.application.runner;

import java.util.LinkedList;

import javax.swing.JApplet;

import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.ConfigurationFile;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.frames.IIDLEMainFrame;
import jb.selfregulation.display.frames.MainFrame;
import jb.selfregulation.processes.ParallelProcesses;



/**
 * 
 * Type: AppletRunner<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AppletRunner extends JApplet
{
    public void init()
    {
        String configParam = super.getParameter("config");        
        
        if(configParam == null)
        {
            throw new RuntimeException("Configuration paramter not set.");
        }
        
        try
        {
            //Util.prepareLogger();
            ConfigurationFile config = new ConfigurationFile();
            config.load(configParam);
            SystemState systemState = new SystemState();
            systemState.loadConfig(config); // load configuration
            systemState.setup(); // setup            
            // prepare the gui
            if(systemState.guiEnabled)
            {
                IIDLEMainFrame c = locateMainFrame(systemState.processes);
                c.finalPreperation(systemState);
                getContentPane().add(c);
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
