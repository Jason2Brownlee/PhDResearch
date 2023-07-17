package jb.selfregulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>
 * Created on 7/12/2003
 * <br>
 * Description:
 * 
 * <br>
 * Copyright (c) Jason Brownlee 2003
 * </p>
 * @author Jason Brownlee
 *
 */
public class FileUtil
{
	
	
	/**
	 * Read in a file as a string
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String loadFile(String filename)
		throws IOException
	{		
		StringBuffer buffer = new StringBuffer(1000);
		FileReader reader = null;
		BufferedReader breader = null;
		
		try
		{
			FileReader freader = new FileReader(filename);			 
			char [] data = new char[1024];
			
			while(freader.ready())
			{
				int len = freader.read(data);
				buffer.append(data, 0, len);
			}
		}
		finally
		{
			if(breader != null)
			{
				breader.close();
			}
			
			if(reader != null)
			{
				reader.close();
			}
		}
		
		return buffer.toString();
	}
	
	public static void writeToFile(String data, String fileout)
		throws Exception
	{
	    FileWriter writer = null;
	    
	    try
	    {
	        writer = new FileWriter(fileout);
	        writer.write(data);
	        writer.flush();
	    }
	    finally
	    {
	        if(writer != null)
	        {
	            writer.close();
	        }
	    }	    
	}
}
