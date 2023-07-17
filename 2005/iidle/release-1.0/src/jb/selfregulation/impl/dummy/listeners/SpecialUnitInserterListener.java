
package jb.selfregulation.impl.dummy.listeners;

import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.dummy.units.DummyUnit;
import jb.selfregulation.impl.dummy.units.DummyUnitFactory;

/**
 * Type: SpecialUnitInserterListener<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * 
 * Inserts a single special unit at a random location at a 
 * specified algorithm iteration
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class SpecialUnitInserterListener 
    implements LatticeStatusListener
{   
    protected int insertionIteration;
    protected int iterationCount;
    protected boolean haveStruck;
    
    protected DummyUnitFactory unitFactory;
    protected Random rand;
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        if(haveStruck)
        {
            return;
        }
        
        if(++iterationCount == insertionIteration)
        {
            // create unit
            DummyUnit du = (DummyUnit) unitFactory.generateNewUnit();
            // make it special
            du.setFitness(1.0);
            du.setEnergy(1.0);
            du.setSpecial(true);
            // insert it
            int localitySelection = rand.nextInt(aLattice.getInitialTotalCells());
            aLattice.getCells().get(localitySelection).getTail().addUnit(du);
            haveStruck = true;
//            System.out.println("Struck in locality " + localitySelection);
        }
    }
    

    public String getBase()
    {
        return ".specialinsert";        
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".specialinsert";        
        insertionIteration = Integer.parseInt(prop.getProperty(b + ".iteration"));        
    }
    public void setup(SystemState aState)
    {        
        unitFactory = (DummyUnitFactory) aState.unitFactory;
        rand = aState.rand;
        haveStruck = false;        
        iterationCount = 0;
    }    
}
