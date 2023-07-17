
package jb.selfregulation.application;

import java.util.logging.Logger;

public interface Loggable
{    
    final static String LOG_CONFIG = "com.mycompany.BasicLogging";
    
    Logger getLogger();
}
