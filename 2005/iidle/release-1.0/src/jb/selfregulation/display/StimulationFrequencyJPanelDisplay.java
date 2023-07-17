
package jb.selfregulation.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.work.AdaptiveExpansion;
import jb.selfregulation.processes.work.ProcessExpansion;



/**
 * Type: StimulationFrequencyJPanelDisplay<br/>
 * Date: 28/09/2005<br/>
 * <br/>
 * Description:
 * 
 * Display the frequency of stimulation across the entire lattice structure
 * 
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class StimulationFrequencyJPanelDisplay extends JPanel implements LatticeStatusListener
{   
//    protected LinkedList<Cell> allCells;
    
    protected Lattice lattice;
    
    public StimulationFrequencyJPanelDisplay()
    {}   
   
    public void latticeChangedEvent(Lattice aLattice)
    {
        // capture locality information from the lattice
//        allCells = aLattice.getDuplicateCellList();        
        // request a repaint
        this.repaint();
    }    

    public String getBase()
    {
        return ".stimulationfreq.display";
    }    

    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {        
        // critical things todo
        lattice = aState.lattice;
        setName("StimulationFrequency");        
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_COMMON_PANELS)).add(this);
    }
   
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        int width = 0;
        int y = 0;
        
        // draw all localities
        LinkedList<Cell> allCells = lattice.getDuplicateCellList();

        width = getWidth() / allCells.size();
        
        // calculate the max stimulation before normalising
        double [] stim = new double[allCells.size()]; // capture to prevent changes
        double max = -1;
        for (int i = 0; i < stim.length; i++)
        {
            if((stim[i]=allCells.get(i).getStimulationCount()) > max)
            {
                max = stim[i];
            }
        }            
        
        // draw the stimulation for each cell
        for (int i = 0,  x = 0; i < allCells.size(); i++,x+=width)
        {
            Cell c = allCells.get(i);
            
            // fill the shape
            float shade = DrawingCommon.determineShade(stim[i], 0, max, false);
            //g.setColor(DrawingCommon.determinePartitionColor(shade, c));
            g.setColor(new Color(shade,shade,shade));
            g.fillRect(x, y, width, width);
            // outline the shape
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, width);
        }
        
        y += width;
    }
}
