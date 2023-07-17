
package swsom.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Type: HistogramDisplay<br/>
 * Date: 9/03/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HistogramDisplay extends JPanel
{
    protected final static int SIZE = 600;
    
    protected double [][] histogram;
    
    public HistogramDisplay()
    {}
    
    public void setHistogram(double [][] aHistogram)
    {
        histogram = aHistogram;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        // background
        int width = getWidth();
        int height = getHeight();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width, height);
        
        if(histogram == null)
        {
            return;
        }
        
        int xSegment = (int) Math.round((double)width / (double)histogram.length );
        int ySegment = (int) Math.round((double)height / (double)histogram[0].length );
        
        // draw the squares
        for (int y = 0; y < histogram.length; y++)
        {
            for (int x = 0; x < histogram[y].length; x++)
            {
                float v = (float) (1 - histogram[y][x]);
                if(v<0||v>1)
                {
                    throw new RuntimeException("Invalid histogram value: " + v);
                }
                
                Color c = new Color(v, v, 1);
                g2d.setColor(c);
                g2d.fillRect(x*xSegment, y*ySegment, xSegment, ySegment);
            }
        }            
        
        // up down grid lines
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < width; i+=xSegment)
        {
            g2d.drawLine(i, 0, i, height);
        }
        // left right grid lines
        for (int i = 0; i < height; i+=ySegment)
        {
            g2d.drawLine(0, i, width, i);
        }
    }
}
