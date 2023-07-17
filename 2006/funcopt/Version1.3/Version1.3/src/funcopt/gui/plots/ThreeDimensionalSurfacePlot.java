
package funcopt.gui.plots;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.SurfacePlot;
import org.opensourcephysics.display2d.SurfacePlotMouseController;

import funcopt.Problem;

/**
 * Type: ThreeDimensionalSurfacePlot<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ThreeDimensionalSurfacePlot extends JPanel
{
    public final static int RESOLUTION = 32;
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected SurfacePlot plot;  	
	
    public ThreeDimensionalSurfacePlot()
    {
        prepareGui();
    }	
    
    protected void prepareGui()
    {
        gridPointData = new GridPointData(RESOLUTION, RESOLUTION, 1);         
        plot = new SurfacePlot(gridPointData);  
        plot.setShowGridLines(true);        
        plot.set2DScaling(7);        
        
        // prepare the panel that holds the plot
        drawingPanel = new DrawingPanel();
        drawingPanel.addDrawable(plot);  
        SurfacePlotMouseController mouseController = new SurfacePlotMouseController(drawingPanel, plot);
        drawingPanel.addMouseListener(mouseController);
        drawingPanel.addMouseMotionListener(mouseController);
        
//        drawingPanel.setPreferredSize(new Dimension(200,200));
        
        setName("3D FuncOpt Plot");        
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);
    }
    
    
    public void setProblem(Problem p)
    {
        if(p.getDimensions() != 2)
        {
            throw new RuntimeException("Unsupported number of dimensions: " + p.getDimensions());
        }
        
        populationGridWithFunction(p);
        plot.setGridData(gridPointData);
        plot.update();
        repaint();
    }

	
	protected void populationGridWithFunction(Problem p)
	{
		// set the scale
	    double [][] minMax = p.getMinmax();
        gridPointData.setScale(minMax[0][0], minMax[0][1], minMax[1][0], minMax[1][1]);
		
		// set the z axis data points
		double [][][] data = gridPointData.getData();		
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				// calculate z
				data[x][y][2] = p.unCountedCost(data[x][y]);
			}
		}
	}
}