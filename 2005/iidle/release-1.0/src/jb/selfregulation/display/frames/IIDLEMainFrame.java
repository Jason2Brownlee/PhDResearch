package jb.selfregulation.display.frames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.control.MasterControlPanel;
import jb.selfregulation.display.control.SimulationControlPanel;

/**
 * Type: IIDLEMainFrame<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class IIDLEMainFrame extends JPanel
    implements LatticeStatusListener
{   
    protected MasterControlPanel controlPanel;    
    protected SimulationControlPanel simulationPanel;
    protected String title;
    
    public IIDLEMainFrame()
    {
        controlPanel = new MasterControlPanel();
        simulationPanel = new SimulationControlPanel();
    }    
    
    public void latticeChangedEvent(Lattice aLattice)
    {} // Nothing
    
    public String getBase()
    {
        return ".mainframe";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        controlPanel.loadConfig(aBase+getBase(), prop);
        simulationPanel.loadConfig(aBase+getBase(), prop);
    }

    public void setup(SystemState aState)
    {
        controlPanel.setup(aState);
        simulationPanel.setup(aState);
    }
    
    public void finalPreperation(SystemState aState)
    {
        prepareGUI(aState);
        setName("IIDLE");
        title = "IIDLE: " + aState.name;
    }

    protected void prepareGUI(SystemState aState)
    {          
        JPanel centrePanel = getCentrePanel(aState);
        
        setLayout(new BorderLayout());   
        add(centrePanel, BorderLayout.CENTER);
        add(simulationPanel, BorderLayout.SOUTH);  
    }
    
    public JPanel getCentrePanel(SystemState aState)
    {
        JPanel p = new JPanel(new BorderLayout());
        JTabbedPane masterTabbedPane = new JTabbedPane();
        p.add(masterTabbedPane);
        
        // process feedback panels
        LinkedList<JComponent> feedback = (LinkedList<JComponent>) aState.getUserDatum(SystemState.KEY_FEEDBACK_PANELS);
        if(feedback!=null && !feedback.isEmpty())
        {
            JTabbedPane feedbackPane = new JTabbedPane();
            masterTabbedPane.add("Feedback", feedbackPane);
            for(JComponent c : feedback)
            {
                feedbackPane.add(c.getName(), c);
            }
        }
                
        // process problem panels
        LinkedList<JComponent> problem = (LinkedList<JComponent>) aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS);
        if(problem!=null)
        {
            JTabbedPane problemPane = new JTabbedPane();
            masterTabbedPane.add("Problem", problemPane);
            
            for(JComponent c : problem)
            {
                problemPane.add(c.getName(), c);
            }
        }        
        
        // control
        masterTabbedPane.add("Control", controlPanel);

        
        // process common panels
        LinkedList<JComponent> common = (LinkedList<JComponent>) aState.getUserDatum(SystemState.KEY_COMMON_PANELS);
        if(common!=null)
        {
            JTabbedPane commonPane = new JTabbedPane();
            masterTabbedPane.add("Common", commonPane);
            
            for(JComponent c : common)            
            {
                commonPane.add(c.getName(), c);
            }
        }
        // process graphs
        LinkedList<JComponent> graphs = (LinkedList<JComponent>) aState.getUserDatum(SystemState.KEY_GRAPH_PANELS);
        if(graphs!=null)
        {
            JTabbedPane graphsPane = new JTabbedPane();
            masterTabbedPane.add("Graphs", graphsPane);
            
            for(JComponent c : graphs)            
            {
                graphsPane.add(c.getName(), c);
            }
        }
        
        return p;
    }
    
    public String getTitle()
    {
        return title;
    }
}
