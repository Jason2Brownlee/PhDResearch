
package swsom.algorithm.problem;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

/**
 * Type: HProblem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class HProblem extends GenericShapeProblem
{
    protected GeneralPath primaryShape;   
    
    public final static int SEGMENT_SIZE = 100;
    
    protected Rectangle r1;
    protected Rectangle r2;
    protected Rectangle r3;
   
    
    protected void prepareShapeProblem()
    {      
        // prepare rectangles
        r1 = new Rectangle(shapeSpace.x, shapeSpace.y, SEGMENT_SIZE, shapeSpace.height);
        r2 = new Rectangle(shapeSpace.x+shapeSpace.width-SEGMENT_SIZE, shapeSpace.y, SEGMENT_SIZE, shapeSpace.height);
        r3 = new Rectangle(shapeSpace.x+SEGMENT_SIZE, shapeSpace.y+(SEGMENT_SIZE+50), SEGMENT_SIZE*2, SEGMENT_SIZE);
                
        primaryShape = new GeneralPath();       
        // first leg
        primaryShape.append(r1, false);
        // second leg
        primaryShape.append(r2, false);
        // middle
        primaryShape.append(r3, false);
        
    }
    
    public double getAreaProblem()
    {        
        return (r1.width*r1.height) + (r2.width*r2.height) + (r3.width*r3.height);
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
        return "Letter H";
    }
}
