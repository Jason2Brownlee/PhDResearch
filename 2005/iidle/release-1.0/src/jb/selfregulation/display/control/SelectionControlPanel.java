
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ProcessUtils;
import jb.selfregulation.processes.work.ProcessExpansion;

/**
 * Type: SelectionControlPanel<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SelectionControlPanel extends JPanel 
    implements Configurable
{
    protected LinkedList<ProcessExpansion> expansionProcess;
   
    
    public String getBase()
    {        
        return ".selectionpanel";
    }
    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {           
        expansionProcess = ProcessUtils.locateAllExpansionProcess(aState.processes);       
        prepareGUI(aState);        
    }   
    
    protected void prepareGUI(SystemState aState)
    {
        JPanel p = prepareControlPanel(aState);
        this.setLayout(new BorderLayout());
        this.add(p);
        this.setName("Selection Panel");
    } 
    
    protected JPanel prepareControlPanel(SystemState aState)
    {
        JPanel p = new JPanel(new BorderLayout());
        
        if(expansionProcess.isEmpty())
        {
            throw new RuntimeException("No expansion processes found!");
        }
        else if(expansionProcess.size() == 1)
        {
            SelectionSubControlPanel p1 = new SelectionSubControlPanel(expansionProcess.getFirst(), aState);
            p.add(p1);
        }
        else
        {
            JTabbedPane tp = new JTabbedPane();
            p.add(tp);
            int count = 0;
            for(ProcessExpansion pe : expansionProcess)
            {
                SelectionSubControlPanel p1 = new SelectionSubControlPanel(pe, aState);
                tp.add("Selection " + count++, p1);
            }
        }
        
        return p;
    }
}
