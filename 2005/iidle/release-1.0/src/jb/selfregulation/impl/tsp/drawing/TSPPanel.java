
package jb.selfregulation.impl.tsp.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jb.selfregulation.application.ConfigurationFile;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;


/**
 * Type: TSPPanel<br/>
 * Date: 2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPPanel extends JPanel
{
    public final static int NODE_WIDTH = 6;
    public final static int NODE_RADIUS = NODE_WIDTH/2;
    public final static float EDGE_THICKNESS = 1.1f;    
    
    protected final static NumberFormat f = new DecimalFormat();
    
    protected final double [][] tspData;
    protected int [] permutation;
    protected TSPUnit unit;
    
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
    
    
   
    

    public TSPPanel(double [][] aData)
    {        
        tspData = aData;
        calculateDataSize();
        prepareGui();
    }
    
    protected void setUnit(TSPUnit aUnit)
    {
        synchronized (this)
        {
            unit = aUnit;
            if(unit == null)
            {
                permutation = null;
            }
            else
            {
                permutation = unit.getData();
            }
        }
    }
    
   

    public void setPermutation(int[] permutation)
    {
        this.permutation = permutation;
    }    
    protected void clearUnit()
    {
        setUnit(null);
    }
    
    protected void prepareGui()
    {
        stroke = new BasicStroke(EDGE_THICKNESS);
        emptyFont = new Font("Serif", Font.BOLD, 15);

        // listeners
        this.addMouseListener(new InternalMouseListener());
    }       
    
    
    protected class InternalMouseListener extends MouseAdapter
    {        
        public synchronized void mouseClicked(MouseEvent evt)
        {                        
            if(unit != null)
            {
                if ((evt.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                {
                    unit.increaseUserStimulation();
                    TSPPanel.this.repaint();
                }
                
                if ( (evt.getModifiers() & InputEvent.BUTTON2_MASK) != 0 ||
                     (evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
                {
                  unit.decreaseUserStimulation();
                  TSPPanel.this.repaint();
                }
            }
        }        
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
    
    protected synchronized void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-alias the painting
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, getWidth(), getHeight());
        
        if(permutation == null)
        {
            g2d.setColor(Color.GRAY);
            g2d.setFont(emptyFont);
            g2d.drawString("Empty", 50, 50);
        }
        else
        {                        
            // draw stimulation
            drawStimulation(g2d); 
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
//        g.scale(scaleFactor,scaleFactor);
        
        // update scale factor to give room all around the structure
        x = ((double)getWidth() / ((double)dataWidth + ((double)NODE_WIDTH/(double)scaleFactor)*5.0));        
        y = ((double)getHeight() / ((double)dataHeight + ((double)NODE_WIDTH/(double)scaleFactor)*5.0));        
        scaleFactor = Math.min(x, y);
        
        
        // calculate offsets
        double diffX = Math.abs(getWidth() - (dataWidth * scaleFactor));
        dataWidthZeroOffset = (int) ((minX * scaleFactor) - Math.floor(diffX / 2.0));
        
        double diffY = Math.abs(getHeight() - (dataHeight * scaleFactor));
        dataHeightZeroOffset = (int) ((minY * scaleFactor) - Math.floor(diffY / 2.0));
        
//        transform = new AffineTransform(g.getTransform());
        transform = new AffineTransform();
        transform.scale(scaleFactor, scaleFactor);
        
    }
    
    protected void drawStimulation(Graphics2D g)
    {
        if(unit == null)
        {
            return;
        }
        
        int centre = (int)Math.floor(getWidth() / 2.0);  
        int stimulation = (int) Math.round(unit.getUserStimulation());        
        int dotWidth = (int)Math.floor(getWidth() / 40.0);               
        int offset = getHeight()-dotWidth-2;
        
        if(stimulation > 0)
        {            
            for (int i = 0, posX = centre; i <stimulation; i++, posX+=(dotWidth*2))
            {
                g.setColor(Color.RED);
                g.fillRect(posX, offset, dotWidth, dotWidth);
                g.setColor(Color.BLACK);
                g.drawRect(posX-1, offset-1, dotWidth, dotWidth);
            }
        }
        else if(stimulation < 0)
        {
            for (int i = 0, posX = centre-dotWidth; i<Math.abs(stimulation); i++, posX-=(dotWidth*2))
            {
                g.setColor(Color.BLUE);
                g.fillRect(posX, offset, dotWidth, dotWidth);
                g.setColor(Color.BLACK);
                g.drawRect(posX-1, offset-1, dotWidth, dotWidth);
            }            
        }        
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
        return transform.transform(p, null);
    }
    
    protected Point2D getTransformedPoint(double [] coord)
    {
        Point2D p = new Point2D.Double(coord[0], coord[1]);
        return transform.transform(p, null);
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
    
    
   
    
    
    
    
    public static void main(String[] args)
    {
        try
        {
            String filename = "experiment1a.batch.config.tsp.52.aco.properties";
            ConfigurationFile config = new ConfigurationFile();
            config.load(filename);            
            SystemState systemState = new SystemState();
            systemState.loadConfig(config); // load configuration
            systemState.setup(); // setup        
            
            TSPProblem problem = new TSPProblem();
            problem.loadConfig("app.system", config.getProp());
//            systemState 
            problem.setup(systemState);
            
            TSPPanel p = new TSPPanel(problem.getCities());
            p.setPermutation(problem.getSolutionCityList());
            
            System.out.println(problem.calculateTourLength(problem.getSolutionCityList()));
            
            JFrame f = new JFrame();
            f.setSize(640, 480);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(p);
            f.setVisible(true);            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }    
}

