
package funcopt.gui.plots;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;

import funcopt.Problem;
import funcopt.SolutionNotify;


/**
 * 
 * Type: InterpolatedFunctionPlot<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class InterpolatedFunctionPlot extends JPanel
    implements SolutionNotify
{    
    public final static int RESOLUTION = 100;
    
	protected Problem problem;
    protected LinkedList<double []> points;
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected InterpolatedPlot plot;       

    
    public InterpolatedFunctionPlot()
    {   
        points = new LinkedList<double []>();        
        prepareGui();
    }
    protected void prepareGui()
    {        
        setName("FuncOpt Plot");        
        setLayout(new BorderLayout());
        drawingPanel = preparePlot();
        add(drawingPanel, BorderLayout.CENTER);
    }
    public void setProblem(Problem p)
    {
        // double check 
        if(p.getDimensions() != 2)
        {
            throw new RuntimeException("Unsupported number of dimensions: " + p.getDimensions());
        }
        
        problem = p;
        points.clear();
        // populate the grid
        updateBaseImage();
        repaint();
    }
    
    
    
	protected DrawingPanel preparePlot()
	{
        gridPointData = new GridPointData(RESOLUTION, RESOLUTION, 1);		
		plot = new InterpolatedPlot(gridPointData);        
		DrawingPanel dp = new SimpleDrawingPanel();
        dp.addDrawable(plot);
//		SurfacePlotMouseController mouseController = new SurfacePlotMouseController(drawingPanel, plot);
//        dp.addMouseListener(mouseController);
//        dp.addMouseMotionListener(mouseController);		
		return dp;
	} 
    
    
    private void updateBaseImage()
    {
        populationGridWithFunction(gridPointData, problem);
        plot.setGridData(gridPointData);
        plot.update();
    }    

    public void populationGridWithFunction(GridPointData pointdata, Problem p)
	{	
		// set the scale
	    double [][] minMax = p.getMinmax();
		pointdata.setScale(minMax[0][0], minMax[0][1], minMax[1][0], minMax[1][1]);
		
		double [][][] data = pointdata.getData();
		
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				// calculate z
				data[x][y][2] = p.unCountedCost(data[x][y]);
			}
		}
	}
    
    
    public void clearPoints()
    {
        synchronized (points)
        {
            points.clear();   
        }
        drawingPanel.repaint();
    }        
    public void notifyOfPoint(double [] u, double score)
    {
        synchronized (points)
        {
            points.add(u);
        }
        
        // redraw the plot
        drawingPanel.repaint();
    }
    
    
    /**
     * Type: SimpleDrawingPanel<br/>
     * Date: 10/03/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
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
        @Override
        public void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;
            // draw the plot
            super.paintComponent(g);    
            // draw the points
            drawPoints(g2d);
            // draw optima
            drawOptima(g2d);
        }        
        
        protected void drawOptima(Graphics2D g)
        {
            double [][] optima = problem.getGlobalOptima();
            if(optima != null)
            {
                g.setColor(Color.WHITE);
                for (int i = 0; i < optima.length; i++)
                {
                    g.drawOval(xToPix(optima[i][0])-BEST_COORD_RADIUS, yToPix(optima[i][1])-BEST_COORD_RADIUS, BEST_COORD_WIDTH, BEST_COORD_WIDTH);
                }
            }
        }
        
        protected void drawPoints(Graphics2D g)
        {             
            synchronized(points)
            {
                for(double [] u : points)
                {
                    drawSolution(g, xToPix(u[0]), yToPix(u[1]));
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