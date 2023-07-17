
package humint.problem;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Type: LetterH<br/>
 * Date: 6/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LetterH extends Problem
{
    public final static int SEGMENT_SIZE = 100;
    public final static int INSETS = 50;
    
    protected Shape [] problem;

    @Override
    protected void initialise()
    {
        Dimension b = getDomainBounds();
        problem = new Shape[]
        {
                new Rectangle2D.Double(INSETS, INSETS, SEGMENT_SIZE, b.height-(INSETS*2)),
                new Rectangle2D.Double(b.width-SEGMENT_SIZE-INSETS, INSETS, SEGMENT_SIZE, b.height-(INSETS*2)),
                new Rectangle2D.Double(INSETS+SEGMENT_SIZE, (SEGMENT_SIZE+100)-INSETS, SEGMENT_SIZE*2-(INSETS*2), SEGMENT_SIZE)

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
        return "Letter H";
    }

}
