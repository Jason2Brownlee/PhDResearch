
package funcopt;

/**
 * Type: AlgorithmIterationNotification<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public interface AlgorithmIterationNotification
{
    void iterationComplete(double bestEver, double currentBest, double currentWorst, double currentMean);
}
