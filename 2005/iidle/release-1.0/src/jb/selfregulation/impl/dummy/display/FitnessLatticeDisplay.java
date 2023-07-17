
package jb.selfregulation.impl.dummy.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.lattice.LatticeDisplay;
import jb.selfregulation.impl.dummy.units.DummyUnit;
import jb.selfregulation.impl.dummy.units.UnitComparatorFitness;
import jb.selfregulation.units.UnitComparatorEnergy;
import jb.selfregulation.units.UnitComparatorSelectionState;

/**
 * Type: FitnessLatticeDisplay<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FitnessLatticeDisplay extends LatticeDisplay
    implements LatticeStatusListener
{   
    protected UnitComparatorFitness fitnessComparator;
    
    
    protected void prepareGraidentData()
    {}
    
    
    @Override
    protected void drawCell(Graphics2D g, Cell aCell, LinkedList<Unit> allUnits, int x)
    {
        int yOffset = 0;
        // draw the cell
        drawCellUnit(g, x, yOffset, aCell);
        yOffset += squareHeight;
        // check that there are some units to draw
        if(allUnits.isEmpty())
        {
            return;
        }
        // order the unit list
        Collections.sort(allUnits, fitnessComparator);
        // draw all units
        for (int j = 0, y = yOffset; j < allUnits.size(); j++, y+=squareHeight)
        {
            DummyUnit aUnit = (DummyUnit) allUnits.get(j);
            drawUnitFitness(g, x, y, aUnit);  
        }
    }
    
    protected void drawUnitFitness(
            Graphics2D g, 
            int x, 
            int y, 
            DummyUnit aUnit)
    {
        // determine shade
        float colour = (float) aUnit.getFitness();
        
        // determine color
        Color c = null;
        if(aUnit.isSpecial())
        {
            c = new Color(colour, colour, 0.0f);
        }
        else
        {
            c = new Color(0.0f, colour, colour);
        }
        // draw the unit
        drawUnit(g, x, y, c);
    }

    
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        repaint();
    }
    public String getBase()
    {
        return "latticepanel";
    }
    public void loadConfig(String aBase, Properties prop)
    {}
    public void setup(SystemState aState)
    {        
//        energyComparator = new UnitComparatorEnergy();
//        selectionComparator = new UnitComparatorSelectionState();        
//        drawMode = DRAW_MODE.ENERGY;
//        sortMode = SORT_MODE.ENERGY;
//        showSelectionState = true;
//        invertScale = true;  
        
        fitnessComparator = new UnitComparatorFitness();
        lattice = aState.lattice;
        
//        latticeDisplay = new LatticeDisplay(aState.lattice);
        setName("Lattice");
//        prepareGui();
        // add to common panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_COMMON_PANELS)).add(this);
    }  
    
    
    
}
