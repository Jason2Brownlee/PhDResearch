
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessUtils;

/**
 * Type: SimulationControlPanel<br/>
 * Date: 14/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimulationControlPanel extends JPanel 
    implements Configurable, ActionListener
{
    public final static String NAME = "Simulation Control";
    
    protected JButton startButton;
    protected JButton stopButton;
    protected JToggleButton pauseButton;
    
    protected LinkedList<ParallelProcesses> processes;

    public String getBase()
    {
        return ".simulationpanel";
    }

    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {
        processes = aState.processes;
        // prepare the GUI
        prepareGUI();        
        if(aState.startOnRun)
        {
            // set to start state
            setGUIStart();
        }
        else
        {
            // set GUI to stop state
            setGUIStop();
        }
    }
    
    protected void prepareGUI()
    {
        JPanel p = prepareControlPanel();
        this.setLayout(new BorderLayout());
        this.add(p);
        this.setName(NAME);
    }    

    protected JPanel prepareControlPanel()
    {
        JPanel master = new JPanel();
        master.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), NAME));
        
        startButton = new JButton("Start System");
        stopButton = new JButton("Stop System");
        pauseButton = new JToggleButton("Pause System",false);
        
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        pauseButton.addActionListener(this);
        
        master.add(startButton);
        master.add(stopButton);
        master.add(pauseButton);
        
        return master;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == startButton)
        {
            if(!processes.getFirst().isCanRun())
            {
                ProcessUtils.startAll(processes);
                setGUIStart();
            }
        }
        else if(src == stopButton)
        {
            if(processes.getFirst().isCanRun())
            {
                ProcessUtils.stopAll(processes);
                setGUIStop();
            }
        }
        else if(src == pauseButton)
        {
            if(processes.getFirst().isCanRun())
            {
                ProcessUtils.pauseAll(processes);
            }
        }        
    }    
    
    protected void setGUIStart()
    {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        pauseButton.setEnabled(true);
        pauseButton.setSelected(false);
    }
    protected void setGUIStop()
    {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);
        pauseButton.setSelected(false);
    }    
}
