
package comopt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils
{
    /**
     * Read in a file as a string
     * @param filename
     * @return
     * @throws IOException
     */
    public final static String loadFile(String filename)
    {       
        InputStream in = null;
        byte [] b = new byte[1024*5];
        
        int offset = 0;
        
        try
        {
            in = FileUtils.class.getResourceAsStream("/"+filename);
            int t = 0;
            while((t=in.read(b, offset, b.length-offset)) != -1)
            {
                offset += t;
                if(offset == b.length)
                {
                    // not finished, and there are still bytes to read
                    byte [] newbuffer = new byte[b.length*2];
                    System.arraycopy(b, 0, newbuffer, 0, offset);
                    b = newbuffer;
                }
            }
        }
        catch(Exception e)
        {}
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e)
                {}
            }
        }
        
        return new String(b, 0, offset);
    }
    
    

}
