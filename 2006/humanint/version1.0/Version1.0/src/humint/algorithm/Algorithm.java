
package humint.algorithm;

import humint.Solution;
import humint.problem.Problem;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Type: Algorithm<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Algorithm
{
    protected long seed;
    protected Random r;
    
    
    public Algorithm()
    {
        seed = System.currentTimeMillis();        
        r = new Random(seed);
    }
    
    public abstract Solution execute(Solution aSolution, Problem problem);
    
    
    public Solution randomSolution(Problem p)
    {
        Dimension bounds = p.getDomainBounds();
        Solution s = new Solution();
        Shape [] shapes = p.getProblem();        
        Shape [] newShapes = new Shape[shapes.length];
        for (int i = 0; i < newShapes.length; i++)
        {
            newShapes[i] = randomShape(shapes[i], bounds);
        }
        s.setShapes(newShapes);
        return s;
    }
    
    
    public Shape randomShape(Shape aShape, Dimension bounds)
    {   
        if(aShape instanceof Rectangle2D)
        {
            Rectangle2D.Double o = (Rectangle2D.Double)aShape;
            Rectangle2D.Double s = new Rectangle2D.Double(o.x,o.y,o.width,o.height);
            s.x = random(0, bounds.width);
            s.y = random(0, bounds.height);
            s.width = random(0, bounds.width);
            s.height = random(0, bounds.height);
            return s;
        }
        else if(aShape instanceof Ellipse2D)
        {
            Ellipse2D.Double o = (Ellipse2D.Double)aShape;
            Ellipse2D.Double s = new Ellipse2D.Double(o.x,o.y,o.width,o.height);
            s.x = random(0, bounds.width);
            s.y = random(0, bounds.height);
            s.width = random(0, bounds.width);
            s.height = random(0, bounds.height);
            return s;
        }
        
        throw new RuntimeException("Unable to mutate, unknown shape: " + aShape.getClass().getName());        
    }
    
    
    public double random(double min, double max)
    {        
        return (max-min) * r.nextDouble(); // anywhere in range
    }
}
