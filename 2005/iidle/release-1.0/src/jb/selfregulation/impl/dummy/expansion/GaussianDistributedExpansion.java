
package jb.selfregulation.impl.dummy.expansion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Cell;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.work.ProcessExpansion;



/**
 * Type: AdaptiveExpansion<br/>
 * Date: 23/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GaussianDistributedExpansion extends ProcessExpansion
{       
    protected double meanCoefficient;
    protected double stdevCoefficient;
    protected int totalLocalities; 
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        totalLocalities = aState.lattice.getInitialTotalCells();
    }    
    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        
        if(numPartitions != 1)
        {
            throw new RuntimeException("Partitioned configurations are not supported!");
        }
        
        String b = aBase + getBase(); 
        meanCoefficient = Double.parseDouble(prop.getProperty(b + ".mean"));
        stdevCoefficient = Double.parseDouble(prop.getProperty(b + ".stdev"));
    }    
    
    protected Cell selectCell(Lattice aLattice)
    {
        LinkedList<Cell> cells = aLattice.getCells();
        
        double mean = (cells.size() * meanCoefficient);
        double stdev = (cells.size() * stdevCoefficient);

        // make a selection in the range
        int selection = (int) Math.round(rand.nextGaussian() * stdev + mean);
        
        if(selection < 0)
        {
            selection = 0;
        }
        else if(selection >= cells.size())
        {
            selection = cells.size()-1;
        }
        
        Cell cell = cells.get(selection);
        return cell;
    }       
    
    
}
