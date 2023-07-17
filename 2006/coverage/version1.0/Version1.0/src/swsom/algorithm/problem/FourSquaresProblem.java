
package swsom.algorithm.problem;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

/**
 * Type: FourSquaresProblem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class FourSquaresProblem extends GenericShapeProblem
{
    protected final static int SEGMENT_SIZE = 150;
    
    protected GeneralPath primaryShape;    
   
    
    protected void prepareShapeProblem()
    {           
        primaryShape = new GeneralPath();
        // top left
        primaryShape.append(new Rectangle(shapeSpace.x, shapeSpace.y, SEGMENT_SIZE, SEGMENT_SIZE), false);
        // top right
        primaryShape.append(new Rectangle(shapeSpace.x+shapeSpace.width-SEGMENT_SIZE, shapeSpace.y, SEGMENT_SIZE, SEGMENT_SIZE), false);
        // bottom left
        primaryShape.append(new Rectangle(shapeSpace.x, shapeSpace.y+shapeSpace.height-SEGMENT_SIZE, SEGMENT_SIZE, SEGMENT_SIZE), false);
        // bottom right
        primaryShape.append(new Rectangle(shapeSpace.x+shapeSpace.width-SEGMENT_SIZE, shapeSpace.y+shapeSpace.height-SEGMENT_SIZE, SEGMENT_SIZE, SEGMENT_SIZE), false);
    }
    
    public double getAreaProblem()
    {
        return (SEGMENT_SIZE*SEGMENT_SIZE) * 4.0;
    }
    
    
    public Shape getProblem()
    {
        return primaryShape;
    }
    
    public boolean isCoordInProblemSpace(int x, int y)
    {
        return primaryShape.contains(x,y);
    }
    
    public boolean isNormalDistribution()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "Four Squares";
    }
}
