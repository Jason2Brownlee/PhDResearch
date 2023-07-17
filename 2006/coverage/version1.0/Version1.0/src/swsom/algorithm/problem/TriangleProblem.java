
package swsom.algorithm.problem;

import java.awt.Polygon;
import java.awt.Shape;

/**
 * Type: TriangleProblem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class TriangleProblem extends GenericShapeProblem
{
    protected Polygon triangleProblemSpace;    
    
    
    
    protected void prepareShapeProblem()
    {
        int apex = shapeSpace.x + (shapeSpace.width/2);
        
        triangleProblemSpace = new Polygon(
                new int []{apex, shapeSpace.x, shapeSpace.x+shapeSpace.width},
                new int []{shapeSpace.y, shapeSpace.y+shapeSpace.height, shapeSpace.y+shapeSpace.height},
                3);
    }
    
    public double getAreaProblem()
    {
        // base * h /2
        return SHAPE_SIZE * SHAPE_SIZE / 2;
    }
    
    public Shape getProblem()
    {
        return triangleProblemSpace;
    }
    
    public boolean isCoordInProblemSpace(int x, int y)
    {
        return triangleProblemSpace.contains(x,y);
    }

    public boolean isNormalDistribution()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "Triangle";
    }
}
