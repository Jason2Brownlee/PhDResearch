
package funcopt.problem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import funcopt.Problem;
import funcopt.SolutionNotify;


/**
 * Type: BlindSamplePlot<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BlindSamplePlot extends JPanel
    implements SolutionNotify
{
    protected LinkedList<double []> coords;
    
    public BlindSamplePlot()
    {
        prepareGUI();
    }
    
    public void prepareGUI()
    {
        coords = new LinkedList<double []>();
        DrawingPanel p = new DrawingPanel();
        setLayout(new BorderLayout());
        add(p);
    }
    
    public void notifyOfPoint(double [] v, double score)
    {
        synchronized(coords)
        {
            coords.add(new double[]{v[0], v[1], score});
        }
        repaint();
    }
    
    public void setProblem(Problem p)
    {
        // nothing
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
        public final static int SIZE = 2;
        
        @Override
        protected void paintComponent(Graphics g)
        {
            int w = getWidth();
            int h = getHeight();
            
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            
            synchronized(coords)
            {
                double max = Double.NEGATIVE_INFINITY;
                double min = Double.POSITIVE_INFINITY;
                
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
