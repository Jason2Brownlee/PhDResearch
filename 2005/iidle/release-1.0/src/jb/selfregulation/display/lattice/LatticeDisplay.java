
package jb.selfregulation.display.lattice;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JPanel;

import jb.selfregulation.Cell;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.Unit;
import jb.selfregulation.display.DrawingCommon;
import jb.selfregulation.impl.tsp.stimulation.TourComparable;
import jb.selfregulation.units.UnitComparatorEnergy;
import jb.selfregulation.units.UnitComparatorSelectionState;

/**
 * Type: LatticeDisplay<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LatticeDisplay extends JPanel
{    
    public static enum DRAW_MODE {ENERGY, FEEDBACK, NETWORK};
    public static enum SORT_MODE {ENERGY, SELECTION_STATE};
    
    protected Lattice lattice;    
    protected UnitComparatorEnergy energyComparator;
    protected UnitComparatorSelectionState selectionComparator;    
    
    protected DRAW_MODE drawMode;
    protected SORT_MODE sortMode;
    protected boolean showSelectionState;
    protected boolean invertScale;
    
    protected int squareWidth;
    protected int squareHeight;
    protected double [] energyGraidentData;
 
    
    public LatticeDisplay()
    {}
    
    public LatticeDisplay(Lattice aLattice)
    {
        lattice = aLattice;
        energyComparator = new UnitComparatorEnergy();
        selectionComparator = new UnitComparatorSelectionState();
        
        drawMode = DRAW_MODE.ENERGY;
        sortMode = SORT_MODE.ENERGY;
        showSelectionState = true;
        invertScale = true;                
    }    
    
    protected void paintComponent(Graphics g)
    {        
        Graphics2D g2d = (Graphics2D) g;      
        // anti-alias
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // clear everything
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        // draw the lattice
        drawLattice(g2d);
    }
    
    protected void drawLattice(Graphics2D g)
    {
        // prepare square size
        prepareSquareSize();
        // prepare graident data
        prepareGraidentData();
        // draw cells
        drawCells(g);
    }
    
    protected void drawCells(Graphics2D g)
    {
        LinkedList<Cell> allCells = lattice.getDuplicateCellList();
        for (int i = 0, x = 0; i < allCells.size(); i++, x+=squareWidth)
        {
            Cell aCell = allCells.get(i);
            LinkedList<Unit> allUnits = aCell.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            drawCell(g, aCell, allUnits, x);
        }
    }
    
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
        orderUnitList(allUnits);
        // draw all units
        for (int j = 0, y = yOffset; j < allUnits.size(); j++, y+=squareHeight)
        {
            Unit aUnit = allUnits.get(j);
            switch(drawMode)
            {
                case ENERGY:
                {
                    drawUnitEnergy(g, x, y, aUnit);     
                    break;
                }
                case FEEDBACK:
                {
                    drawCellFeedback(g, x, y, aUnit, aCell);
                    break;
                }
                case NETWORK:
                {
                    drawCellNetwork(g, x, y, aUnit, aCell);
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid mode: " + drawMode);
                }                
            } 
        }
    }
    
    protected void orderUnitList(LinkedList<Unit> units)
    {
        switch(sortMode)
        {
            case ENERGY:
            {
                Collections.sort(units, energyComparator);
                break;
            }          
            case SELECTION_STATE:
            {
                Collections.sort(units, selectionComparator);
                break;
            }
            default:
            {
                throw new RuntimeException("Invalid mode: " + drawMode);
            }  
        }
    }
    
    protected void prepareGraidentData()
    {
        switch(drawMode)
        {
            case NETWORK:
            case FEEDBACK:
            case ENERGY:
            {
                energyGraidentData = lattice.getRoughEnergyRange();
                break;
            }
            default:
            {
                throw new RuntimeException("Invalid mode: " + drawMode);
            }                
        } 
    }    
    
    protected void prepareSquareSize()
    {
        double maxTailLength = lattice.getRoughMaxTailLength() + 1; // +1 for cells
        double totalCells = lattice.getRoughTotalCells();        
        double width = getWidth();
        double height = getHeight();
        // prepare square size
        squareWidth = (int) Math.floor(width/totalCells);
        squareHeight = (int) Math.floor(height/maxTailLength);
    }   
     
    
    
    protected void drawCellUnit(
            Graphics2D g, 
            int x, 
            int y, 
            Cell aCell)
    {
        Color c = null;
        
        if(aCell.isPortal())
        {
            c = Color.MAGENTA;
        }
        else
        {
            c = Color.GRAY;            
        }
        
        // draw the normal unit
        drawUnit(g, x, y, c);
        
        c = DrawingCommon.determinePartitionColor(0.0f, aCell);        
        drawCellCentre(g, x, y, c);
    } 
    
    protected void drawCellCentre(Graphics2D g, int x, int y, Color c)
    {
        int split = squareHeight / 3;        
        y += split;
        
        // fill the shape
        g.setColor(c);
        g.fillRect(x, y, squareWidth, split);        
        // outline the shape
        g.setColor(Color.BLACK);
        g.drawRect(x, y, squareWidth, split);
    }

    
    protected void drawCellFeedback(
            Graphics2D g, 
            int x, 
            int y, 
            Unit aUnit,
            Cell aCell)
    {        
        // prepare shade
        float colour = DrawingCommon.determineShade(aUnit.getEnergy(), energyGraidentData[0], energyGraidentData[1], invertScale);        
        // get colour
        Color c = DrawingCommon.determinePartitionColor(colour, aCell);
        // do the drawing
        drawUnit(g, x, y, c);
    }     
    
    
    protected void drawCellNetwork(
            Graphics2D g, 
            int x, 
            int y, 
            Unit aUnit,
            Cell aCell)
    {        
        // get colour
        Color c = null;
        if(aUnit.isVisitor())
        {
            c = Color.RED;
        }
        else
        {
            c= Color.BLUE;
        }
        // do the drawing
        drawUnit(g, x, y, c);
    }     
    
    
    protected void drawUnitEnergy(
            Graphics2D g, 
            int x, 
            int y, 
            Unit aUnit)
    {
        // determine shade
        float colour = DrawingCommon.determineShade(aUnit.getEnergy(), energyGraidentData[0], energyGraidentData[1], invertScale);
        // determine color
        Color c = DrawingCommon.determineCommonColor(colour, aUnit, showSelectionState);
        // draw the unit
        drawUnit(g, x, y, c);
    } 
    
    protected void drawUnit(Graphics2D g, int x, int y, Color c)
    {
        // fill the shape
        g.setColor(c);
        g.fillRect(x, y, squareWidth, squareHeight);        
        // outline the shape
        g.setColor(Color.BLACK);
        g.drawRect(x, y, squareWidth, squareHeight);
    }

    public boolean isShowSelectionState()
    {
        return showSelectionState;
    }

    public void setShowSelectionState(boolean showSelectionState)
    {
        this.showSelectionState = showSelectionState;
    }

    public boolean isInvertScale()
    {
        return invertScale;
    }

    public void setInvertScale(boolean invertScale)
    {
        this.invertScale = invertScale;
    }
    
    public DRAW_MODE getDrawMode()
    {
        return drawMode;
    }

    public void setDrawMode(DRAW_MODE drawMode)
    {
        this.drawMode = drawMode;
    }

    public SORT_MODE getSortMode()
    {
        return sortMode;
    }

    public void setSortMode(SORT_MODE sortMode)
    {
        this.sortMode = sortMode;
    }
}
