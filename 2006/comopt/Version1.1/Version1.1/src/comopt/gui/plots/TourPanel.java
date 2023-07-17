
package comopt.gui.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JPanel;

import comopt.Problem;


/**
 * Type: TourPanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TourPanel extends JPanel
{
    public final static int NODE_WIDTH = 3;
    public final static int NODE_RADIUS = NODE_WIDTH/2;
    public final static float EDGE_THICKNESS = 1.1f;    
    
    protected final static NumberFormat f = new DecimalFormat();
    
    protected double [][] tspData;
    protected int [] permutation;
    
    protected BasicStroke stroke;
    protected Font emptyFont;
    protected double scaleFactor;
    
    protected double dataWidth;
    protected double dataHeight;
    
    protected double minX;
    protected double maxX;
    protected double minY;
    protected double maxY;
    
    protected double dataWidthZeroOffset;
    protected double dataHeightZeroOffset;   
    

    public TourPanel()
    {            
        prepareGui();
    }    
    
    public void setProblem(Problem p)
    {
        tspData = p.getCities();
        calculateDataSize();
    }
    public void setTour(int [] aPermutation)
    {
        permutation = aPermutation;
    }  

    protected void prepareGui()
    {
        stroke = new BasicStroke(EDGE_THICKNESS);
        emptyFont = new Font("Serif", Font.BOLD, 15);
    }  
    
    protected void calculateDataSize()
    {
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        for (int i = 0; i < tspData.length; i++)
        {
            if(tspData[i][0] < minX)
            {
                minX = tspData[i][0];
            }
            if(tspData[i][0] > maxX)
            {
                maxX = tspData[i][0];
            }
        }
        dataWidth = (maxX - minX);
        
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        for (int i = 0; i < tspData.length; i++)
        {
            if(tspData[i][1] < minY)
            {
                minY = tspData[i][1];
            }
            if(tspData[i][1] > maxY)
            {
                maxY = tspData[i][1];
            }
        }
        dataHeight = (maxY - minY);
    }
    
    @Override
    protected synchronized void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-alias the painting
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, getWidth(), getHeight());
        
        if(permutation != null)
        {             
            // rotate the image
            AffineTransform at = (AffineTransform)(g2d.getTransform().clone());
            at.scale(1,-1);
            at.translate(0, -getHeight());
            g2d.setTransform(at);
            
            // update transform
            updateTransform(g2d);
            // arcs
            drawEdges(g2d);
            // vertex
            drawNodes(g2d);
        }
    }
    

    
    
    protected AffineTransform transform;
    
    protected void updateTransform(Graphics2D g)
    {
        // prepare scale
        double x = ((double)getWidth() / (double)dataWidth);
        double y = ((double)getHeight() / (double)dataHeight);
        scaleFactor = Math.min(x, y);
        
        // update scale factor to give room all around the structure
        x = ((double)getWidth() / ((double)dataWidth + ((double)NODE_WIDTH/(double)scaleFactor)*5.0));        
        y = ((double)getHeight() / ((double)dataHeight + ((double)NODE_WIDTH/(double)scaleFactor)*5.0));        
        scaleFactor = Math.min(x, y);        
        
        // calculate offsets
        double diffX = Math.abs(getWidth() - (dataWidth * scaleFactor));
        dataWidthZeroOffset = (int) ((minX * scaleFactor) - Math.floor(diffX / 2.0));
        
        double diffY = Math.abs(getHeight() - (dataHeight * scaleFactor));
        dataHeightZeroOffset = (int) ((minY * scaleFactor) - Math.floor(diffY / 2.0));
        
        transform = new AffineTransform();
        transform.scale(scaleFactor, scaleFactor);
    }
    


    protected void drawNodes(Graphics2D g)
    {        
        g.setColor(Color.RED);
        
        for (int i = 0; i < tspData.length; i++)
        {
            Point2D p = getTransformedPoint(tspData[i]);
            Ellipse2D r = new Ellipse2D.Double(
                    p.getX()-dataWidthZeroOffset-NODE_RADIUS, 
                    p.getY()-dataHeightZeroOffset-NODE_RADIUS, 
                    NODE_WIDTH, 
                    NODE_WIDTH);
            g.fill(r);
        }
    }

    protected Point2D getTransformedPoint(double x, double y)
    {
        Point2D p = new Point2D.Double(x, y);        
        p = transform.transform(p, null);
        return p;
    }
    
    protected Point2D getTransformedPoint(double [] coord)
    {
        return getTransformedPoint(coord[0], coord[1]);
    }
    
    protected void drawEdges(Graphics2D g)
    {
        if(permutation != null)
        {
            g.setColor(Color.BLUE);       
            g.setStroke(stroke);
            
            for (int i = 1; i < permutation.length; i++)
            {
                drawEdge(g, permutation[i-1], permutation[i]);
            }
            
            // join end with start
            drawEdge(g, permutation[0], permutation[permutation.length-1]);
        }
    }    
    
    protected void drawEdge(Graphics2D g, int c1, int c2)
    {        
        Point2D p1 = getTransformedPoint(tspData[c1]);
        Point2D p2 = getTransformedPoint(tspData[c2]);
        
        Line2D line = new Line2D.Double(
                p1.getX()-dataWidthZeroOffset, p1.getY()-dataHeightZeroOffset, 
                p2.getX()-dataWidthZeroOffset, p2.getY()-dataHeightZeroOffset);
        
        g.draw(line);
    }
}

