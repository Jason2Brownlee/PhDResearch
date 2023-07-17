
package jb.selfregulation.application.runner;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import jb.selfregulation.application.Loggable;


/**
 * Type: Util<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Util
{    
   public static final void prepareLogger()
   {
       try 
       {
           FileHandler fh = new FileHandler("log.txt");
           fh.setFormatter(new SimpleFormatter());
           
           Logger logger = Logger.getLogger(Loggable.LOG_CONFIG);    
           logger.addHandler(fh);      
//           logger.setLevel(Level.INFO);
//           logger.setLevel(Level.WARNING);
           logger.setLevel(Level.SEVERE);
       } 
       catch (IOException e) 
       {
           throw new RuntimeException("Failed to prepare logger.", e);
       }       
   }
}
