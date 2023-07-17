
package jb.selfregulation.processes.listeners;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.initialise.LatticeInitialiser;

/**
 * Type: LocalityModificationListener<br/>
 * Date: 22/09/2005<br/>
 * <br/>
 * Description:
 * 
 * This thing works by randomy adding or removing resources
 * Specify a mean and stdev iteration and a "strike point" will
 * be selected using a Gaussian distribution
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class LocalityModificationListener 
    implements LatticeStatusListener
{    
    // false, and resources are removed    
    protected boolean addResources;
    // number of times the listener is executed (internal cycle count)
    protected int iteration;
    
    protected long mean;
    protected long stdev;
    
    protected long strikePoint;
    protected boolean haveStruck;
    
    protected long totalToRemove;
        
    protected Random rand;
    protected LatticeInitialiser init;
    
    
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        if(haveStruck)
        {
            return;
        }
        
        if(++iteration == strikePoint)
        {
            if(addResources)
            {
                LinkedList<Cell> cells = new LinkedList<Cell>(); 
                for (int i = 0; i < totalToRemove; i++)
                {
                    cells.add(init.generateNewCell(10));
                }
                aLattice.addCells(cells, init);
            }
            else
            {
                aLattice.deleteRandomCells(rand, (int)totalToRemove, init);    
            }            
            haveStruck = true;
            
//            System.out.println("--> WE HAVE BEEN STRUCK");
        }
    }
    

    public String getBase()
    {
        return ".localitymod";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".localitymod";

        addResources = Boolean.parseBoolean(prop.getProperty(b + ".add"));
        totalToRemove = Long.parseLong(prop.getProperty(b + ".total"));
        mean = Long.parseLong(prop.getProperty(b + ".mean"));
        stdev = Long.parseLong(prop.getProperty(b + ".stdev"));
        
    }
    public void setup(SystemState aState)
    {
        rand = aState.rand;
        init = aState.lattice.getInit();
        
        // calculate the strike point
        strikePoint = generateStrikePoint(mean, stdev, rand);        
        haveStruck = false;        
    }
    
    public static long generateStrikePoint(double m, double st, Random r)
    {
        return Math.round(r.nextGaussian() * st + m);
    }
    
    public static void main(String[] args)
    {
        Random r = new Random();
        
        for (int i = 0; i < 100; i++)
        {
            long v = generateStrikePoint(50, 10, r);
            System.out.println(v);
        }
    }
}
