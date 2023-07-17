
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessUtils;
import jb.selfregulation.processes.work.ProcessDecay;
import jb.selfregulation.processes.work.ProcessPortalInbound;
import jb.selfregulation.processes.work.ProcessPortalOutbound;

/**
 * Type: NetworkProcessControlPanel<br/>
 * Date: 15/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class NetworkProcessControlPanel extends JPanel 
    implements Configurable, ChangeListener
{
    public final static String NAME = "Network Process Configuration";


    protected SliderPanel networkFrequency;
    protected SliderPanel networkOutboundAmplitude;    

    protected LinkedList<ParallelProcesses> processes;
    protected LinkedList<ProcessPortalOutbound> outboundProcess;
    protected LinkedList<ProcessPortalInbound> inboundProcess;
    protected ParallelProcesses networkProcess;
    
    public String getBase()
    {
        return ".networkprocesspanel";
    }

    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {   
        processes = aState.processes;
        outboundProcess = ProcessUtils.locateAllPortalOutbound(aState.processes);
        inboundProcess = ProcessUtils.locateAllPortalInbound(aState.processes);
        networkProcess = ProcessUtils.locateNetworkProcess(aState.processes);        
        
        // prepare the GUI
        prepareGUI();
    }

    public void stateChanged(ChangeEvent e)
    {
        Object src = e.getSource();
        
        if(src == networkFrequency.slider)
        {
            networkProcess.setWaitTime(networkFrequency.slider.getValue());
            networkFrequency.refreshCurrentValue();
        }
        else if(src == networkOutboundAmplitude.slider)
        {
            for(ProcessPortalOutbound p : outboundProcess)
            {
                p.setTotal(networkOutboundAmplitude.slider.getValue());    
            }
            
            networkOutboundAmplitude.refreshCurrentValue();
        }
    }
    
    protected void prepareGUI()
    {
        JPanel p = prepareConfigPanel();
        this.setLayout(new BorderLayout());
        this.add(p, BorderLayout.NORTH);
        this.setName(NAME);
    } 
    
    
    protected JPanel prepareConfigPanel()
    {          
        SliderPanel.MODE m = SliderPanel.MODE.HORIZONTAL;
        int totalSliders = 2;
        
        // create sliders
        networkFrequency = new SliderPanel(m, "Network Frequency", new JSlider(1000, 5000, (int)networkProcess.getWaitTime()));
        networkOutboundAmplitude = new SliderPanel(m, "Outbound Amplitude", new JSlider(0, 20, outboundProcess.getFirst().getTotal()));
        
        // configure sliders
        networkFrequency.prepareSlider(this, 100, 1000);
        networkOutboundAmplitude.prepareSlider(this, 1, 5);
        
        JPanel p = new JPanel(new GridLayout(totalSliders, 1));        
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), NAME));
        
        // processes
        p.add(networkFrequency);
        p.add(networkOutboundAmplitude);
        
        return p;
    }
}
