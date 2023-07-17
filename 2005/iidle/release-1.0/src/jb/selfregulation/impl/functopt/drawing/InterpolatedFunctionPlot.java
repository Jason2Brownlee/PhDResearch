
package jb.selfregulation.impl.functopt.drawing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.DrawingCommon;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;
import org.opensourcephysics.display2d.SurfacePlotMouseController;


/**
 * Type: InterpolatedFunctionPlot<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class InterpolatedFunctionPlot extends JPanel 
    implements LatticeStatusListener, ActionListener
{
    public static enum MODE {SELECTION, PARTITION};
    
	protected Function function;
    protected int resolution;
    protected Lattice lattice;
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected InterpolatedPlot plot;
	
    protected JCheckBox drawSelected;
    protected JCheckBox drawEvaluated;
    
    protected JRadioButton drawSelection;
    protected JRadioButton drawPartition;
    
    protected boolean indicateBest;    
    protected MODE mode;
    protected boolean showControls;
    protected double lastJitter = 0; 
    
    
    public InterpolatedFunctionPlot()
    {}
    
    public InterpolatedFunctionPlot(
            Function aFunction, 
            int aResolution)
    {   
        // additional configuration
        showControls = false;
        indicateBest = false;
        resolution = aResolution;
        // get things
        function = aFunction;
        lattice = null;        
        // double check 
        if(function.getNumDimensions() != 2)
        {
            throw new RuntimeException("Unsupported number of dimensions: " + function.getNumDimensions());
        }
        // prepare the GUI
        prepareGui();
    }
    
    
    
  

    public String getBase()
    {        
        return ".plot";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        resolution = Integer.parseInt(prop.getProperty(b + ".resolution"));
    }

    public void setup(SystemState aState)
    {
        // additional configuration
        showControls = true;
        indicateBest = true;
        // get things
        function = (Function) aState.problem;
        lattice = aState.lattice;        
        // double check 
        if(function.getNumDimensions() != 2)
        {
            throw new RuntimeException("Unsupported number of dimensions: " + function.getNumDimensions());
        }
        // add to problem panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS)).add(this);
        // prepare the GUI
        prepareGui();
    }    
    
    
    public boolean isIndicateBest()
    {
        return indicateBest;
    }

    public void setIndicateBest(boolean indicateBest)
    {
        this.indicateBest = indicateBest;
    }

    protected void prepareGui()
    {        
        setName("FuncOpt Plot");
        // prepare plot
        drawingPanel = preparePlot(function, resolution);
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);        
        // prepare controls
        if(showControls)
        {
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2, 1));            
            p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Interpolated Plot Control"));
            p.add(prepareModePanel());
            p.add(prepareDisplayPanel());            
            add(p, BorderLayout.SOUTH);
        }
    }
    
    protected JPanel prepareModePanel()
    {
        JPanel p = new JPanel();
        
        mode = MODE.SELECTION;
        drawSelection = new JRadioButton("Colour Selected", true);
        drawPartition = new JRadioButton("Colour Partition", false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(drawSelection);
        group.add(drawPartition);
        
        drawSelection.addActionListener(this);
        drawPartition.addActionListener(this);
        
        p.add(drawSelection);
        p.add(drawPartition);
        
        return p;
    }
    
    protected JPanel prepareDisplayPanel()
    {
        JPanel p = new JPanel();
        
        drawSelected = new JCheckBox("Draw Selected", true); 
        drawEvaluated = new JCheckBox("Draw Evaluated", true);
        
        drawSelected.addActionListener(this);
        drawEvaluated.addActionListener(this);
        
        p.add(drawSelected);
        p.add(drawEvaluated);
        
        return p;
    }
    
	protected DrawingPanel preparePlot(Function aFunction, int aResolution)
	{
        gridPointData = new GridPointData(aResolution, aResolution, 1);			
		// populate the grid
		populationGridWithFunction(gridPointData, aFunction);		
		// prepare plot
		plot = new InterpolatedPlot(gridPointData);        
		DrawingPanel dp = new SimpleDrawingPanel();
        dp.addDrawable(plot);
		SurfacePlotMouseController mouseController = new SurfacePlotMouseController(drawingPanel, plot);
        dp.addMouseListener(mouseController);
        dp.addMouseMotionListener(mouseController);
		
		return dp;
	} 
    
	public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == drawSelection)
        {
            mode = MODE.SELECTION;
        }
        else if(src == drawPartition)
        {
            mode = MODE.PARTITION;
        }
        // other thing simply get drawn or not drawn
        
        latticeChangedEvent(null);
    }
    
    private void updateBaseImage()
    {
        populationGridWithFunction(gridPointData, function);
        plot.setGridData(gridPointData);
    }    

    public void populationGridWithFunction(
            GridPointData pointdata, 
            Function aFunction)
	{	
		// set the scale
	    double [][] minMax = aFunction.getGenotypeMinMax();
		pointdata.setScale(minMax[0][0], minMax[0][1], minMax[1][0], minMax[1][1]);
		
		double [][][] data = pointdata.getData();
		
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				// calculate z
				data[x][y][2] = aFunction.evaluate(data[x][y]);
			}
		}
	}
    
       
    
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        // check if the function needs to be recalculated
        if(function.supportsDynamic() && function.getCycleLength()>0)
        {
            updateBaseImage(); // recalculate
        }
        // check for jitter change or non zero
        else if(function.supportsJitter() && (function.getJitterPercentage()!=lastJitter || function.getJitterPercentage()>0))
        {
            updateBaseImage(); // recalculate
        }
        lastJitter = function.getJitterPercentage();
        // redraw the plot
        drawingPanel.repaint();
    }
    
    
    protected class SimpleDrawingPanel extends DrawingPanel
    {
        public final static int BEST_COORD_WIDTH = 50;
        public final static int BEST_COORD_RADIUS = BEST_COORD_WIDTH/2;
        public final static int SOLUTION_WIDTH = 4;
        public final static int SOLUTION_RADIUS = SOLUTION_WIDTH/2;
        
        public SimpleDrawingPanel()
        {
            setSquareAspect(true);
            enableInspector(true);
        }
        
        public void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;
            // draw the plot
            super.paintComponent(g);    
            // draw the best solution
            drawBestCoord(g2d);
            // draw the lattice
            drawLattice(g2d);
        }
        protected void drawBestCoord(Graphics2D g)
        {
            // never draw best if we do not want it, or the problem is dynamic
            if(!indicateBest || function.supportsDynamic())
            {
                return;
            }            
            // draw a circle for the maximum
            double [] bestCoord = function.getBestCoord();
            g.setColor(Color.BLACK);
            g.drawOval(xToPix(bestCoord[0])-BEST_COORD_RADIUS, yToPix(bestCoord[1])-BEST_COORD_RADIUS, BEST_COORD_WIDTH, BEST_COORD_WIDTH);
        }        
        protected void drawLattice(Graphics2D g)
        {
            if(lattice == null)
            {
                return;
            }                
            // process all cells
            LinkedList<Cell> cellList = lattice.getDuplicateCellList();                                
            for(Cell c : cellList)
            {
                // process all units
                LinkedList<Unit> unitList = c.getDuplicateTailList();                
                for(Unit u : unitList)
                {
                    FuncOptUnit fu = (FuncOptUnit) u;
                    if(fu.isHasFunctionEvaluation())
                    {
                        double [] data = fu.getVectorData();
                        drawSolution(fu, c, g, xToPix(data[0]), yToPix(data[1]));
                    }
                }                
            }
        }
        protected void drawSolution(FuncOptUnit u, Cell cell, Graphics2D g, int x, int y)
        {
            // check for not draw selected
            if(u.isSelected() && !drawSelected.isSelected())
            {
                return;
            }
            // check for not draw evaluated - still draw selected if we can
            if(u.isEvaluated() && !u.isSelected() && !drawEvaluated.isSelected())
            {
                return;
            }
            
            Color c = null;
            float shade = 0.0f;
            
            switch(mode)
            {
                case SELECTION:
                {
                    c = DrawingCommon.determineCommonColor(shade, u, true);
                    break;
                }
                case PARTITION:
                {
                    c = DrawingCommon.determinePartitionColor(shade, cell);
                    break;
                }
                default:
                {
                    throw new RuntimeException("Unknown mode: " + mode);
                }
            }
            
            // draw the thing
            g.setColor(c);
            g.fillRect(x-SOLUTION_RADIUS, y-SOLUTION_RADIUS, SOLUTION_WIDTH, SOLUTION_WIDTH);
            g.setColor(Color.BLACK);
            g.drawRect(x-SOLUTION_RADIUS, y-SOLUTION_RADIUS, SOLUTION_WIDTH, SOLUTION_WIDTH);
        }
    }



}