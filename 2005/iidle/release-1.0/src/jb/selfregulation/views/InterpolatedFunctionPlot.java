
package jb.selfregulation.views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JPanel;

import jb.selfregulation.Unit;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.views.competition.PointNotify;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;
import org.opensourcephysics.display2d.SurfacePlotMouseController;


/**
 * Type: InterpolatedFunctionPlot<br/>
 * Date: 20/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class InterpolatedFunctionPlot extends JPanel
    implements PointNotify
{    
	protected Function function;
    protected LinkedList<FuncOptUnit> points;
    protected LinkedList<ViewFunction> views;
    protected int resolution;
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected InterpolatedPlot plot;    
    

    
    public InterpolatedFunctionPlot(
            Function aFunction, 
            int aResolution)
    {   
        // additional configuration
        resolution = aResolution;
        // get things
        function = aFunction;
     
        // double check 
        if(function.getNumDimensions() != 2)
        {
            throw new RuntimeException("Unsupported number of dimensions: " + function.getNumDimensions());
        }
        // prepare the GUI
        points = new LinkedList<FuncOptUnit>();
        views = new LinkedList<ViewFunction>();
        prepareGui();
    }
    

    protected void prepareGui()
    {        
        setName("FuncOpt Plot");
        // prepare plot
        drawingPanel = preparePlot(function, resolution);
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);                
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
    
       
    
    
    public void updateWithView(ViewFunction v)
    {
        views.add(v);
        drawingPanel.repaint();
    }
    
    
    public void clearPoints()
    {
        synchronized (points)
        {
            points.clear();   
        }
        drawingPanel.repaint();
    }
    
    public void addPoint(double [] aPoint)
    {
        FuncOptUnit f = new FuncOptUnit();
        f.setVectorData(aPoint);
        updateWithPoint(f);
    }
    
    public void addPoint(double [] aPoint, double fitness)
    {
        addPoint(aPoint);
    }
    
    
    public void updateWithPoint(LinkedList<Unit> l)
    {
        for(Unit u : l)
        {
            updateWithPoint((FuncOptUnit)u);
        }
    }
    public void updateWithPoint(FuncOptUnit u)
    {
        synchronized (points)
        {
            points.add(u);
        }
        
        // redraw the plot
        drawingPanel.repaint();
    }
    
    public void refreshAllViews(LinkedList<ViewFunction> v)
    {
        views.clear();
        views.addAll(v);
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
            // draw the points
            drawPoints(g2d);
            // draw the views
            drawViews(g2d);
        }
            
        
        protected void drawViews(Graphics2D g)
        {
            for(ViewFunction v : views)
            {
                double [][] minmax = v.getGenotypeMinMax();
                
                double rw = (minmax[0][1] - minmax[0][0]);
                double rh = (minmax[1][1] - minmax[1][0]);
                
                int w = (xToPix(minmax[0][1]) - xToPix(minmax[0][0]));
                int h = (yToPix(minmax[1][0]) - yToPix(minmax[1][1]));                                
                
                int x = xToPix(minmax[0][0]);
                int y = yToPix(minmax[1][0] + rh);
                
                g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.1f));
                g.fillRect(x, y, w, h);
            }
        }
        
        protected void drawPoints(Graphics2D g)
        {             
            synchronized(points)
            {
                for(FuncOptUnit u : points)
                {
                    double [] data = u.getVectorData();
                    drawSolution(g, xToPix(data[0]), yToPix(data[1]));
                }                
            }
        }
        
        
        
        
        protected void drawSolution(Graphics2D g, int x, int y)
        {                        
            // draw the thing
            g.setColor(Color.BLACK);
            g.fillRect(x-SOLUTION_RADIUS, y-SOLUTION_RADIUS, SOLUTION_WIDTH, SOLUTION_WIDTH);
            g.setColor(Color.WHITE);
            g.drawRect(x-SOLUTION_RADIUS, y-SOLUTION_RADIUS, SOLUTION_WIDTH, SOLUTION_WIDTH);
        }
    }



}