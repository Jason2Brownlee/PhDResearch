
package humint.algorithm;

import humint.Solution;
import humint.problem.Problem;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Type: MutationAlgorithm<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MutationAlgorithm extends Algorithm
{
    protected double stdev;
    
    
    public MutationAlgorithm()
    {}
    
    
    
    public double mutate(double aCoord, double min, double max)
    {
        double dev = stdev * (max-min);        
        double v = aCoord + (r.nextGaussian() * dev);
        
        // a wrap could bounce beyond the opposite end of the domain
        while(v > max || v < min)
        { 
            // too large
            while(v > max)
            {
                // subtract the difference
                double diff = Math.abs(v - max);
                // always smaller
                v = (max - diff);
                
            }
            // too small
            while(v < min)
            {  
                double diff = Math.abs(v - min);
                // always larger
                v = (min + diff);                    
            } 
        }
        
        return v;
    }
    
    public Shape mutateShape(Shape aShape, Dimension bounds)
    {   
        if(aShape instanceof Rectangle2D)
        {
            Rectangle2D.Double o = (Rectangle2D.Double)aShape;
            Rectangle2D.Double s = new Rectangle2D.Double(o.x,o.y,o.width,o.height);
            s.x = mutate(s.x, 0, bounds.width);
            s.y = mutate(s.y, 0, bounds.height);
            s.width = mutate(s.width, 0, bounds.width);
            s.height = mutate(s.height, 0, bounds.height);
            return s;
        }
        else if(aShape instanceof Ellipse2D)
        {
            Ellipse2D.Double o = (Ellipse2D.Double)aShape;
            Ellipse2D.Double s = new Ellipse2D.Double(o.x,o.y,o.width,o.height);
            s.x = mutate(s.x, 0, bounds.width);
            s.y = mutate(s.y, 0, bounds.height);
            s.width = mutate(s.width, 0, bounds.width);
            s.height = mutate(s.height, 0, bounds.height);
            return s;
        }
        
        throw new RuntimeException("Unable to mutate, unknown shape: " + aShape.getClass().getName());        
    }
    
    @Override
    public Solution execute(Solution aSolution, Problem problem)
    {
        Dimension bounds = problem.getDomainBounds();        
        Shape [] shapes = aSolution.getShapes();        
        Shape [] newShapes = new Shape[shapes.length];
        for (int i = 0; i < newShapes.length; i++)
        {
            newShapes[i] = mutateShape(shapes[i], bounds);
        }
        
        Solution s = new Solution();
        s.setShapes(newShapes);
        return s;
    }



    public double getStdev()
    {
        return stdev;
    }



    public void setStdev(double stdev)
    {
        this.stdev = stdev;
    }    
}
