
package comopt.gui.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JPanel;

import comopt.Problem;
import comopt.SolutionNotify;


/**
 * Type: SamplePanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AdjacencyMatrixPanel extends JPanel implements SolutionNotify
{
public final static int MIN_SQUARE_SIZE = 4;
    
    protected volatile int [][] adjacencyMatrix;
    protected volatile int globalMax;
    protected volatile int squareSize;
    protected Problem problem;
    
    public AdjacencyMatrixPanel()
    {
        prepareGUI();
    }
    protected void prepareGUI()
    {}
    
    public void setProblem(Problem p)
    {
        problem = p;
        int totalCities = problem.getCities().length;        
        squareSize = (int) ((double)Math.min(getHeight(),getWidth()) / (double)totalCities);
        if(squareSize >= MIN_SQUARE_SIZE)
        {
            adjacencyMatrix = new int[totalCities][totalCities];
        }
        else
        {
            adjacencyMatrix = null;
        }
        repaint();
    }
    public void clearPoints()
    {
        if(adjacencyMatrix!=null)
        {
            for (int i = 0; i < adjacencyMatrix.length; i++)
            {
                Arrays.fill(adjacencyMatrix[i], 0);
            }
        }
        repaint();
    }
    
    
    public void notifyOfPoint(int[] v, double score)
    {  
        if(adjacencyMatrix == null)
        {
            return;
        }
        
        for (int i = 0; i < v.length; i++)
        {
            int x = v[i];
            int y = -1;
            if(i == v.length-1)
            {
                y = v[0];
            }
            else
            {
                y = v[i+1];
            }
            
            if(x > y)
            {
                // swap
                int a = x;
                x = y;
                y = a;
            }
            // update and kep track of global max frequency            
            if(++adjacencyMatrix[x][y] > globalMax)
            {
                globalMax = adjacencyMatrix[x][y];
            }
        }
        
        repaint();
    }
    
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getWidth(),getHeight());
        
        if(adjacencyMatrix != null)
        {
            double max = globalMax;
            // draw the squares
            for (int i = 0, y = 0; i < adjacencyMatrix.length; i++, y+=squareSize)
            {
                for (int j = 0, x=0; j < adjacencyMatrix[i].length; j++, x+=squareSize)
                {
                    if(x>y)
                    {
                        double v = adjacencyMatrix[i][j];
                        float f = 1.0f;
                        if(max > 0 && v<=max)
                        {
                            f = (float) (1.0-(v / max));
                        }
                        Color c = new Color(1.0f,f,f);
                        g2d.setColor(c);
                        g2d.fillRect(x, y, squareSize, squareSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(x, y, squareSize, squareSize);
                    }
//                    else
//                    {
//                        g2d.setColor(Color.BLACK);
//                        g2d.drawRect(x, y, squareSize, squareSize);
//                    }
                }                
            }
        }
    }
}
