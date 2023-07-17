
package swsom.algorithm.problem;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Type: CircleProblem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class CircleProblem extends GenericShapeProblem
{
    protected final static int INNER_RADIUS = 200;
    protected final static int INSET = 100;
    
    protected Ellipse2D innerEllipse;    
    protected Ellipse2D ellipseProblemSpace;   
    protected GeneralPath primaryShape;
    
    
    protected void prepareShapeProblem()
    {
        ellipseProblemSpace = new Ellipse2D.Double(shapeSpace.x,shapeSpace.y,shapeSpace.width,shapeSpace.height);
        innerEllipse = new Ellipse2D.Double(shapeSpace.x+INSET,shapeSpace.y+INSET,INNER_RADIUS,INNER_RADIUS);
        
        primaryShape = new GeneralPath();
        primaryShape.append(ellipseProblemSpace, false);        
        primaryShape.append(innerEllipse, false);
    }
    
    public double getAreaProblem()
    {       
        // PI * (outerR + innerR) (outerR - innerR)
        double outer = SHAPE_SIZE / 2.0;
        double inner = INNER_RADIUS / 2.0;
        return Math.round(Math.PI * (outer+inner) * (outer-inner));
    }
    
    public Shape getProblem()
    {
        return primaryShape;
    }
    
    public boolean isCoordInProblemSpace(int x, int y)
    {
        return ellipseProblemSpace.contains(x,y) && !innerEllipse.contains(x,y);
    }
    
    public boolean isNormalDistribution()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return "Circle";
    }
}
