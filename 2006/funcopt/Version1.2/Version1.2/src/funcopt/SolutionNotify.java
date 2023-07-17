
package funcopt;

/**
 * Type: SolutionNotify<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public interface SolutionNotify
{
    void notifyOfPoint(double [] v, double score);
}
