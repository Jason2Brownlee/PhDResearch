
package jb.selfregulation.impl.proteinfolding.drawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;


/**
 * Type: SolutionDrawer<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SolutionDrawer extends JPanel
{
    protected HPModelEval problem;    
    protected volatile byte [][] map;
    protected volatile byte [] path;    
    
    public SolutionDrawer(HPModelEval aProb)
    {
        problem = aProb;
    }
    
    protected void paintComponent(Graphics graph)
    {
        // house keeping
        Graphics2D g = (Graphics2D) graph;
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        
        if(map != null)
        {
            int divisor = Math.min(w,h);
            // check for less pixels than squares
            if(divisor >= map.length)
            {
                // draw the map
                drawMap(g, divisor);
                // draw the path
                drawPath(g, divisor);
            }
        }
    }
    
    
    protected void drawMap(Graphics2D g, int divisor)
    {
        // do not draw all the map, just the middle 
        int offset = (map.length/2)/2;
        // only drawing half the map in each dimension (1/4)
        int squareSize = divisor / (map.length/2); 
        int x = 0, y = 0;
        
        for (int i = offset; i < map.length-offset; i++)
        {
            for (int j = offset; j < map[i].length-offset; j++)
            {
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, squareSize-1, squareSize-1);
                g.setColor(Color.WHITE);
                g.fillRect(x, y, squareSize-1, squareSize-1);
                
                switch(map[j][i])
                {
                    case HPModelEval.EMPTY:
                    {
                        break;
                    }
                    case HPModelEval.H:
                    {
                        g.setColor(Color.BLACK);
                        g.fillOval(x, y, squareSize-1, squareSize-1);
                        break;
                    }
                    case HPModelEval.P:
                    {
                        g.setColor(Color.GRAY);
                        g.drawOval(x, y, squareSize-1, squareSize-1);
                        break;
                    }
                    default:
                    {
                        throw new RuntimeException("Invalid value: " + map[j][i]);
                    }
                }                    
                
                x += squareSize;
            }
            y += squareSize;
            x = 0;
        }
    }
    
    protected void drawPath(Graphics2D g, int divisor)
    {
        // do not draw all the map, just the middle 
        int offset = (map.length/2)/2;
        // only drawing half the map in each dimension (1/4)
        int squareSize = divisor / (map.length/2);         
        int x = offset*squareSize;
        int y = offset*squareSize;
        
        int half = squareSize/2;
        
        g.setColor(Color.GRAY);
        g.drawRect(x-1, y-1, squareSize, squareSize);
        
        for (int i = 0; i < path.length; i++)
        {
            int x2, y2;
            
            switch(path[i])
            {
                case HPModelEval.LEFT:
                {
                    x2 = x - squareSize;
                    y2 = y;              
                    break;
                }
                case HPModelEval.RIGHT:
                {
                    x2 = x + squareSize;
                    y2 = y;             
                    break;
                }
                case HPModelEval.BACK:
                {
                    x2 = x;
                    y2 = y - squareSize;                   
                    break;
                }
                case HPModelEval.FOWARD:
                {
                    x2 = x;
                    y2 = y + squareSize; 
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid path value " + path[i]);
                }
            }
            
            g.drawLine(x+half, y+half, x2+half, y2+half);
            x = x2;
            y = y2;
        }
    }
    
    public void updateMap(byte [][] aMap, byte [] aPath)
    {
        if(aPath==null || aMap==null)
        {
            throw new RuntimeException("invalid map or path");
        }
        map = aMap;
        path = aPath;
        this.repaint();
    }    
}
