
package jb.selfregulation.application.stats;

import jb.selfregulation.application.Configurable;

/**
 * Type: IRunStatistics<br/>
 * Date: 19/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public interface IRunStatistics extends Configurable
{
    // user in user datum for a single run
    String KEY_RUN_STATISTICS = "RunStatistics";
    
    String toString();
    
    int getTotalRuns();
    
}
