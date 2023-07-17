
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
 * Type: ProliferationControlPanel<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProliferationControlPanel extends JPanel 
    implements Configurable
{    
    public final static String NAME = "Proliferation Configuration";
    
    public String [] classes;    
    protected LinkedList<ProcessExpansion> expansionProcess;

 
    
    public String getBase()
    {        
        return ".proliferationpanel";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        
        classes = new String[4];
        classes[0] = prop.getProperty(b+".gaclassname");
        classes[1] = prop.getProperty(b+".acoclassname");
        classes[2] = prop.getProperty(b+".mutationclassname");
        classes[3] = prop.getProperty(b+".psoclassname");
    }

    public void setup(SystemState aState)
    {           
        expansionProcess = ProcessUtils.locateAllExpansionProcess(aState.processes);       
        // prepare the GUI
        prepareGUI(aState);
    }   
    
    protected void prepareGUI(SystemState aState)
    {
        JPanel p = prepareControlPanel(aState);
        this.setLayout(new BorderLayout());
        this.add(p);
        this.setName(NAME);
    } 
    
    protected JPanel prepareControlPanel(SystemState aState)
    {
        JPanel p = new JPanel(new BorderLayout());
        
        if(expansionProcess.isEmpty())
        {
            throw new RuntimeException("Unable to locate any expansion processes");
        }
        else if(expansionProcess.size() == 1)
        {
            ProliferationSubControlPanel p1 = new ProliferationSubControlPanel(expansionProcess.getFirst(), aState, classes);
            p.add(p1);
        }
        else
        {
            JTabbedPane tp = new JTabbedPane();
            p.add(tp);
            int count = 0;
            for(ProcessExpansion pe : expansionProcess)
            {
                ProliferationSubControlPanel p1 = new ProliferationSubControlPanel(pe, aState, classes);
                tp.add("Proliferation " + count++, p1);
            }
        }
        
        return p;
    }
}
