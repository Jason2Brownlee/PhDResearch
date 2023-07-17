
package humint.gui;

import humint.Solution;
import humint.problem.Problem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;


/**
 * Type: ProblemPanel<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProblemPanel extends JPanel
{
    public final static Color BACKGROUND_DRAW = Color.BLACK;
    public final static Color BACKGROUND_FILL = Color.WHITE;
    
    public final static Color PROBLEM_DRAW = Color.RED;
    public final static Color PROBLEM_FILL = new Color(1.0f, 0.0f, 0.0f, 0.2f);
    
    public final static Color SOLUTION_DRAW = Color.GREEN;
    public final static Color SOLUTION_FILL = new Color(0.0f, 1.0f, 0.0f, 0.2f);
    
    protected volatile Problem problem;
    protected volatile Solution solution;
    protected volatile Dimension lastSize;
    
    
    public void setProblem(Problem p)
    {
        problem = p;
        solution = null;
        lastSize = null;
        repaint();
    }

    public void setSolution(Solution solution)
    {
        this.solution = solution;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;        
        drawBackground(g2d);
        if(problem != null)
        {
            prepareScaledTransformation(g2d);
            drawProblem(g2d);
            drawSolution(g2d);
        }
    }
    
    protected void drawBackground(Graphics2D g2d)
    {
        g2d.setColor(BACKGROUND_FILL);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(BACKGROUND_DRAW);
        g2d.drawRect(0, 0, getWidth(), getHeight());
    }
    
    protected void prepareScaledTransformation(Graphics2D g2d)
    {              
        Dimension size = this.getSize();
        Dimension bounds = problem.getDomainBounds();
        
        double scaledWidth = ((double)size.width / (double)bounds.width);
        double scaledHeight = ((double)size.height / (double)bounds.height);
        
        AffineTransform at = (AffineTransform)g2d.getTransform().clone();
        at.scale(scaledWidth, scaledHeight);
        g2d.setTransform(at);        
    }
    
    protected void drawProblem(Graphics2D g2d)
    {        
        Shape [] shapes = problem.getProblem(); 
        g2d.setColor(PROBLEM_FILL);
        for (int i = 0; i < shapes.length; i++)
        {
            g2d.fill(shapes[i]);
        }
        g2d.setColor(PROBLEM_DRAW);
        for (int i = 0; i < shapes.length; i++)
        {
            g2d.draw(shapes[i]);
        }
    }
    
    protected void drawSolution(Graphics2D g2d)
    {
        if(solution == null)
        {
            return;
        }
        
        Shape [] shapes = solution.getShapes(); 
        g2d.setColor(SOLUTION_FILL);
        for (int i = 0; i < shapes.length; i++)
        {
            g2d.fill(shapes[i]);
        }
        g2d.setColor(SOLUTION_DRAW);
        for (int i = 0; i < shapes.length; i++)
        {
            g2d.draw(shapes[i]);
        }
    }
}
