
package jb.selfregulation.application;

import java.util.logging.Logger;

public abstract class Problem implements Configurable, Loggable
{
    protected final Logger logger;
    
    public Problem()
    {
        logger = Logger.getLogger(LOG_CONFIG);
    }
    
    public Logger getLogger()
    {
        return logger;
    }
    
    public abstract long getTotalEvaluations();

}
