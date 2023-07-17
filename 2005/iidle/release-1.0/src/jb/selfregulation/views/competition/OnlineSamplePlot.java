
package jb.selfregulation.views.competition;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Type: SampleDisplayer<br/>
 * Date: 23/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class OnlineSamplePlot extends JFrame
    implements PointNotify
{
    protected LinkedList<double []> coords;
    
    public OnlineSamplePlot()
    {
        super("Sample Plot");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        prepareGUI();
        makeVisible();
    }
    public void centerScreen()
    {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        setVisible(true);
        requestFocus();
    }

    public void makeVisible()
    {
        Runnable run = new Runnable()
        {
            public void run()
            {
                centerScreen();
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    
    
    public void prepareGUI()
    {
        coords = new LinkedList<double []>();
        DrawingPanel p = new DrawingPanel();
        
        Container c = getContentPane();
        c.add(p);
    }
    
    /*
    protected void addPoints(LinkedList<Unit> list)
    {
       //synchronized(coords)
       {
           for(Unit u : list)
           {
               FuncOptUnit f = (FuncOptUnit) u;
               double v [] = f.getVectorData();
               coords.add(new double[]{v[0], v[1], f.getFunctionEvaluation()});
               repaint();
           }
       }
       
    }
    */
    
    public void addPoint(double [] aPoint)
    {
        throw new UnsupportedOperationException("cannot do this");
    }
    
    public void addPoint(double [] aPoint, double fitness)
    {
        synchronized(coords)
        {
            coords.add(new double[]{aPoint[0], aPoint[1], fitness});
        }
        repaint();
    }
    public void clearPoints()
    {
        synchronized(coords)
        {
            coords.clear();
        }
        repaint();
    }
    
    protected class DrawingPanel extends JPanel
    {
        public final static int SIZE = 4;
        
        @Override
        protected void paintComponent(Graphics g)
        {
            int w = getWidth();
            int h = getHeight();
            
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            
            synchronized(coords)
            {
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;
                
                for(double [] c : coords)
                {
                    if(c[2] > max)
                    {
                        max = c[2];
                    }
                    if(c[2] < min)
                    {
                        min = c[2];
                    }
                }
                
                double range = max-min;                
                for(double [] c : coords)
                {
                    float v = (float)((c[2]-min)/range);
                    Color colour = new Color(1.0f-v, 0.0f, 0.0f);
                    int x = (int) (Math.round(c[0] * w) - (SIZE/2));
                    int y = (int) (Math.round(c[1] * h) - (SIZE/2));
                    g.setColor(colour);
                    g.fillOval(x,y,SIZE,SIZE);
                }
            }
        }
    }
}
