
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
 * Type: AdaptiveJPanelDisplay<br/>
 * Date: 26/09/2005<br/>
 * <br/>
 * Description:
 * 
 * Display for the adaptive expansion process
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class AdaptiveExpansionJPanelDisplay extends JPanel implements LatticeStatusListener
{   
    protected LinkedList<AdaptiveExpansion> allExpansionProcesses;    
//    protected LinkedList<Cell> allCells;
    protected LinkedList<double[]> expansionProbabilities;
    
    protected Lattice lattice; 
    
    
    public AdaptiveExpansionJPanelDisplay()
    {}
    
    

    public void latticeChangedEvent(Lattice aLattice)
    {
        // capture locality information from the lattice
//        allCells = aLattice.getDuplicateCellList();

        // capture locality probabilties fromt the expansion processes
//        expansionProbabilities.clear();
//        for(AdaptiveExpansion p : allExpansionProcesses)
//        {
//            double [] prob = p.getAbsoluteSelectionProbabilities();
//            expansionProbabilities.add(prob);
//        }
        
        // request a repaint
        this.repaint();
    }
    

    public String getBase()
    {
        return ".adaptive.expansion.display";
    }
    

    public void loadConfig(String aBase, Properties prop)
    {
        // TODO Auto-generated method stub
    }

    public void setup(SystemState aState)
    {
        allExpansionProcesses = new LinkedList<AdaptiveExpansion>();
        Collection<ProcessExpansion> all = (Collection<ProcessExpansion>) ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).values();
        for(ProcessExpansion p : all)
        {
            if(p instanceof AdaptiveExpansion)
            {
                allExpansionProcesses.add((AdaptiveExpansion)p);
            }
        }
        
        
        lattice = aState.lattice; 
        expansionProbabilities = new LinkedList<double[]>();
        setName("AdaptiveExpansion");        
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_COMMON_PANELS)).add(this);
    }
    
    
   
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        int width = 0;
        int y = 0;
        LinkedList<Cell> allCells = lattice.getCells();        
        width = getWidth() / allCells.size();
        
        for (int i = 0,  x = 0; i < allCells.size(); i++,x+=width)
        {
            Cell c = allCells.get(i);            
            // fill the shape
            g.setColor(DrawingCommon.determinePartitionColor(0.0f, c));
            g.fillRect(x, y, width, width);
            // outline the shape
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, width);
        }
        
        y += width;
        
        expansionProbabilities.clear();
        for(AdaptiveExpansion p : allExpansionProcesses)
        {
            double [] prob = p.getAbsoluteSelectionProbabilities();
            expansionProbabilities.add(prob);
        }
        
        // draw the probability list for each adaptive expansion process
        for (int i = 0; i < expansionProbabilities.size(); i++, y+=width)
        {
            double [] prob = expansionProbabilities.get(i);
            if(prob != null)
            {               
                for (int j = 0, x=0; j < prob.length; j++,x+=width)
                {
                    // determine shade
                    float shade = DrawingCommon.determineShade(prob[j], 0, 1.0, false);
                    // fill the shape
                    g.setColor(new Color(shade, shade, shade));
                    g.fillRect(x, y, width, width);
                    // outline the shape
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, width, width);
                }
                
                String name = allExpansionProcesses.get(i).getProgeny().getClass().getName();
                name = name.substring(name.lastIndexOf('.')+1);
                g.setColor(Color.MAGENTA);
                g.drawString(name, 2, y+10);
            }
        }
    }

}
