
package swsom.algorithm.problem;

import java.awt.Shape;

/**
 * Type: SquareProblem<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class SquareProblem extends GenericShapeProblem
{        
    
    
    public Shape getProblem()
    {
        return shapeSpace;
    }
    
    public boolean isCoordInProblemSpace(int x, int y)
    {
        return shapeSpace.contains(x, y);
    }
    
    protected void prepareShapeProblem()
    {}
    
    public boolean isNormalDistribution()
    {
        return false;
    }
    
    public double getAreaProblem()
    {
        return SHAPE_SIZE*SHAPE_SIZE;
    }
    
    @Override
    public String toString()
    {
        return "Square";
    }
}
