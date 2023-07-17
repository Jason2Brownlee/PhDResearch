
package maxpath;

import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * Type: Utils
 * Date: 19/11/2004
 * 
 * 
 * @author Jason Brownlee
 */
public class Utils
{
    
	public final static void stringToFile(String aString, String aName)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(aName);
			writer.write(aString);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (Exception e)
			{}
		}
	}
    
    
    private final static DecimalFormat format = new DecimalFormat();
    
    public final static byte[] duplicate(byte[] aPoint)
    {
        byte[] point = new byte[aPoint.length];
        System.arraycopy(aPoint, 0, point, 0, aPoint.length);
        return point;
    }
    
    public final static String calculateTime(long millis)
    {
        if (millis < 1000)
        {
            return millis + " ms";
        }
        else if ((millis / 1000.0) < 60.0)
        {
            double sec = millis / 1000.0;
            return format.format(sec) + " sec";
        }
        else
        {
            double mins = (millis / 1000.0) / 60.0;
            return format.format(mins) + " min";
        }
    }
}
