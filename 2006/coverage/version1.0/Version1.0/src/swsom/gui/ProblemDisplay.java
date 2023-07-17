
package swsom.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.FeatureMap;
import swsom.algorithm.IterationEventListener;
import swsom.algorithm.MathUtils;
import swsom.algorithm.Problem;
import swsom.algorithm.VectorConnection;
import swsom.algorithm.problem.GenericShapeProblem;

/**
 * Type: ProblemDisplay<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class ProblemDisplay extends JPanel implements IterationEventListener
{
    protected final static int NODE_WIDTH = 4;
    protected final static int GRID_SIZE = 20;
    
    protected final static boolean DRAW_NODES = true;    
    protected final static boolean DRAW_VERTICES = true;
    protected final static boolean DRAW_SATELLITES = true;
    protected final static boolean DRAW_GRID = true;    
    
    protected Problem problem;    
    protected FeatureMap map;    
    protected double [][] sample;
    
    public ProblemDisplay()
    {
        Rectangle domain = GenericShapeProblem.shapeSpace;
        Dimension d = new Dimension(domain.width+50, domain.height);
        setMinimumSize(d);
        setPreferredSize(d);
    }
    
    public void iterationEvent(int iteration)
    {
        if((iteration%5) == 0)
        {
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);    
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // draw grid
        drawGrid(g2d);
        // draw problem
        drawProblem(g2d);
        // draw solution
        drawMap(g2d);
        // draw sample 
        drawSample(g2d);
    }
    
    protected void drawSample(Graphics2D g2d)
    {
        if(sample != null)
        {
            g2d.setColor(Color.RED);
            for (int i = 0; i < sample.length; i++)
            {
                g2d.fillRect((int)sample[i][0], (int)sample[i][1], 1, 1);
            }
        }
    }
    
    protected void drawProblem(Graphics2D g2d)
    {
        if(problem == null)
        {
            return;
        }
        
        Dimension domain = problem.getDomain();
        Shape shape = ((GenericShapeProblem)problem).getShapeSpace();
        Shape prob = problem.getProblem();
        
        // draw world        
        g2d.setColor(Color.BLACK);    
        g2d.drawRect(0, 0, domain.width, domain.height);
        
        // draw shape space
        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
        
        // draw problem  
        g2d.setColor(Color.BLACK);
        g2d.draw(prob);
    }
    
    protected void drawGrid(Graphics2D g2d)
    {
        if(DRAW_GRID)
        {
            Rectangle domain = GenericShapeProblem.shapeSpace;
            g2d.setColor(Color.WHITE);
            g2d.fill(domain);
            
            Color newColor = new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 100);
            g2d.setColor(newColor);
            
            // draw up downs
            for (int x = domain.x; x <= domain.x+domain.width; x+=GRID_SIZE)
            {
                g2d.drawLine(x, domain.y, x, domain.y+domain.height);
            }
            
            // draw left rights
            for (int y = domain.y; y <= domain.y+domain.height; y+=GRID_SIZE)
            {
                g2d.drawLine(domain.x, y, domain.x+domain.width, y);
            }
        }
    }
    
    protected void drawMap(Graphics2D g2d)
    {
        drawSingleMap(map, g2d, 0);
    }
    
    
    protected void drawMapEdges(FeatureMap aMap, Graphics2D g2d, int index)
    {
        if(DRAW_VERTICES)
        {
            if(index == 0)
            {
                g2d.setColor(new Color(0, 0, 255, 50));
            }
            else
            {                
                g2d.setColor(new Color(255, 0, 0, 50));
                return;
            }
            
	        VectorConnection [] connections = aMap.getConnections();	        
	        for (int i = 0; i < connections.length; i++)
	        {
	            double [] c1 = ((ExemplarVector)connections[i].getVertex1()).getData();
	            double [] c2 = ((ExemplarVector)connections[i].getVertex2()).getData();
	            g2d.drawLine((int)Math.round(c1[0]), (int)Math.round(c1[1]), (int)Math.round(c2[0]), (int)Math.round(c2[1]));
	        }
        }
    }
    
    protected void drawMapNodes(FeatureMap aMap, Graphics2D g2d, int index)
    {
        if(DRAW_NODES)
        {
            ExemplarVector [] vectors = aMap.getVectors();	
            
            if(index == 0)
            {
                for (int i = 0; i < vectors.length; i++)
    	        {
    	            drawNode(g2d, vectors[i], Color.BLUE);
    	        }
            }
            else
            {
                for (int i = 0; i < vectors.length; i++)
    	        {
    	            drawNode(g2d, vectors[i], Color.RED);
    	        }
            }	        
        }   
    }
    
    /*
    protected void drawMapSatellites(FeatureMap aMap, Graphics2D g2d, int index)
    {
        if(DRAW_SATELLITES)
        {
            ExemplarVector [] vectors = aMap.getVectors();	   
            int half = NODE_WIDTH/2;
            
            // check for any satellites
            if(vectors[0].getSatellites() == null)
            {
                return;
            }            
            
            g2d.setColor(new Color(0, 255, 0, 50));
//            g2d.setColor(Color.GREEN);
            
            for (int i = 0; i < vectors.length; i++)
            {
                ExemplarVector [] satellites = vectors[i].getSatellites();                
                // draw nodes           
               
                for (int j = 0; j < satellites.length; j++)
                {
                    // draw node
                    double [] coords = satellites[j].getData();   
                    g2d.fillOval((int)Math.round(coords[0])-half, (int)Math.round(coords[1])-half, NODE_WIDTH, NODE_WIDTH);
                    // draw line to parent
                    double [] c1 = satellites[j].getData();
    	            double [] c2 = vectors[i].getData();
    	            g2d.drawLine((int)Math.round(c1[0]), (int)Math.round(c1[1]), (int)Math.round(c2[0]), (int)Math.round(c2[1]));
                }
                
                // prepare a shape
                Polygon shape = new Polygon();
                for (int j = 0; j < satellites.length; j++)
                {
                    double [] coord = satellites[j].getData();
                    shape.addPoint((int)Math.round(coord[0]), (int)Math.round(coord[1]));
                }
                
                // draw coverage area
                g2d.draw(shape);
                g2d.fill(shape);
            }
        }
    }
    */
    
    protected void drawSingleMap(FeatureMap aMap, Graphics2D g2d, int index)
    {       
        if(map == null)
        {            
            return;
        }
        
        // draw all connections
        drawMapEdges(aMap, g2d, index);
        
        // draw all nodes
        drawMapNodes(aMap, g2d, index);
        
        // draw satellites
        //drawMapSatellites(aMap, g2d, index);
    }
    
    
    protected void drawNode(
            Graphics2D g2d, 
            ExemplarVector v, 
            Color n)
    {
        int half = NODE_WIDTH/2;
        Color c = new Color(n.getRed(), n.getGreen(), n.getBlue(), 20);
        
        double [] coords = v.getData();
        g2d.setColor(n);
        g2d.fillOval((int)Math.round(coords[0])-half, (int)Math.round(coords[1])-half, NODE_WIDTH, NODE_WIDTH);
        
        if(v.isSquare() && v.hasSquareRadius())
        {            
            Rectangle r = v.getSquareRectangle();
            g2d.draw(r);
            g2d.setColor(c);
            g2d.fill(r);
        }
        else if(v.isSatellite())
        {
            Polygon p = new Polygon();
            ExemplarVector [] satellites = v.getSatellites();
//            for (int i = 0; i < satellites.length; i++)
//            {
//                double [] data = satellites[i].getData();
//                p.addPoint((int)data[0], (int)data[1]);
//            }
//            
//            g2d.draw(p);
//            g2d.setColor(c);
//            g2d.fill(p);
            
            
            // add the first
            double [] data = satellites[0].getData();
            p.addPoint((int)data[0], (int)data[1]);
            
            // add the next as the closest to the first
            double bestLength = Double.POSITIVE_INFINITY;
            int bestIndex1 = -1;
            for (int i = 1; i < satellites.length; i++)
            {
                double d = MathUtils.distanceEuclidean(satellites[0].getData(), satellites[i].getData());
                if(d < bestLength)
                {
                    bestLength = d;
                    bestIndex1 = i;
                }
            }
            // add the second node
            data = satellites[bestIndex1].getData();
            p.addPoint((int)data[0], (int)data[1]);
            
            // locate the node closest to the second node
            bestLength = Double.MAX_VALUE;
            int bestIndex2 = -1;
            for (int i = 1; i < satellites.length; i++)
            {
                if(i == bestIndex1)
                {
                    continue;
                }
                
                double d = MathUtils.distanceEuclidean(satellites[bestIndex1].getData(), satellites[i].getData());
                if(d < bestLength)
                {
                    bestLength = d;
                    bestIndex2 = i;
                }
            }
            // add the second node
            data = satellites[bestIndex2].getData();
            p.addPoint((int)data[0], (int)data[1]);            
            
            // add the remaining node
            for (int i = 1; i < satellites.length; i++)
            {
                if(i == bestIndex1 || i == bestIndex2)
                {
                    continue;
                }
               
	            data = satellites[i].getData();
	            p.addPoint((int)data[0], (int)data[1]);
	            break;
            }
            
            // draw the thing
          g2d.draw(p);
          g2d.setColor(c);
          g2d.fill(p);
        }
    }

    public void setMap(FeatureMap map)
    {
        this.map = map;
        repaint();        
    }

    public void setProblem(Problem problem)
    {
        this.problem = problem;
        repaint();
    }

    public void setSample(double[][] sample)
    {
        this.sample = sample;
        repaint();
    }
    
    
}
