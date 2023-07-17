
package funcopt;

import java.util.LinkedList;

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
    void iterationComplete(Problem p, LinkedList<Solution> currentPop);
}
