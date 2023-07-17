
package humint.problem;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Type: Doughnut<br/>
 * Date: 6/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Doughnut extends Problem
{
    protected final static int INNER_RADIUS = 100;
    protected final static int INSET = 50;
    
    protected Shape [] problem;
    
    @Override
    protected void initialise()
    {
        Dimension b = getDomainBounds();
        problem = new Shape[]
        {
                new Ellipse2D.Double(INSET,INSET,b.width-INSET*2,b.height-INSET*2),
                new Ellipse2D.Double(INSET+100,INSET+100,INNER_RADIUS,INNER_RADIUS)
        };
    }
    
    @Override
    public Shape[] getProblem()
    {
        return problem;
    }

    @Override
    public String getName()
    {
        return "Doughnut";
    }

}
