
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;


/**
 * Type: MasterControlPanel<br/>
 * Date: 14/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MasterControlPanel extends JPanel
    implements LatticeStatusListener
{
    public final static String NAME = "Control Panel";
    
    protected NetworkProcessControlPanel networkProcessPanel;
    protected ProcessControlPanel processPanel;
    protected SelectionControlPanel selectionPanel;
    protected ProliferationControlPanel proliferationPanel;
    
        
    public MasterControlPanel()
    {
        processPanel = new ProcessControlPanel();
        selectionPanel = new SelectionControlPanel();
        proliferationPanel = new ProliferationControlPanel();
        networkProcessPanel = new NetworkProcessControlPanel();
    }
    
    
    public void latticeChangedEvent(Lattice aLattice)
    {}

    public String getBase()
    {
        return ".masterpanel";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        
        processPanel.loadConfig(b, prop);
        selectionPanel.loadConfig(b, prop);
        proliferationPanel.loadConfig(b, prop);
        networkProcessPanel.loadConfig(b, prop);
    }

    public void setup(SystemState aState)
    {
        processPanel.setup(aState);
        selectionPanel.setup(aState);
        proliferationPanel.setup(aState);
        if(aState.p2pNetwork.isNetworkEnabled())
        {
            networkProcessPanel.setup(aState);
        }
        
        prepareGUI(aState);
    }

    protected void prepareGUI(SystemState aState)
    {        
        this.setLayout(new BorderLayout());
        this.setName(NAME);
        
        JTabbedPane tabbed = new JTabbedPane();
        tabbed.add("Processes", processPanel);        
        tabbed.add("Selection", selectionPanel);
        tabbed.add("Proliferation", proliferationPanel);        
        
        if(aState.p2pNetwork.isNetworkEnabled())
        {
            tabbed.add("Network Processes", networkProcessPanel);
        }
        
        add(tabbed, BorderLayout.CENTER);
    }   
}

