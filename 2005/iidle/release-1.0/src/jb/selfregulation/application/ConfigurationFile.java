
package jb.selfregulation.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JApplet;




/**
 * Type: ConfigurationFile<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */

public class ConfigurationFile implements Loggable
{
    protected Properties prop;
    protected Logger logger;
    
    public ConfigurationFile()
    {
        logger = Logger.getLogger(LOG_CONFIG);
    }
    
    public void load(String afilename)
    {
        InputStream in = null;
        try
        {
            in = this.getClass().getResourceAsStream("/" + afilename);
            prop = new Properties();
            prop.load(in);
        }   
        catch(IOException e)
        {
            logger.log(Level.SEVERE,"Unable to load properties file.", e);
            throw new RuntimeException("Unable to load properties file.");
        }
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception e)
                {}
            }
        }
    }
    
    

    public Logger getLogger()
    {
        return logger;
    }

    public Properties getProp()
    {
        return prop;
    }
    
    
}
