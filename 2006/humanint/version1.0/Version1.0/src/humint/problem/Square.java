
package humint.problem;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Type: Square<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Square extends Problem
{
    public final static int INSETS = 100;
    
    protected Shape [] problem;
    
  
    
    @Override
    protected void initialise()
    {
        Dimension b = getDomainBounds();
        problem = new Shape[]{new Rectangle2D.Double(INSETS, INSETS, b.width-(INSETS*2), b.height-(INSETS*2))};
    }

    @Override
    public Shape[] getProblem()
    {
        return problem;
    }
    
    @Override
    public String getName()
    {
        return "Square";
    }
}
