/**
 * Created on 8/11/2004
 *
 */
package maxpath;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;


/**
 * Type: ProblemReader
 * File: ProblemReader.java
 * Date: 8/11/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class ProblemReader
{
    public final static char COMMENT = '#';
    private final static boolean LOG = false;
    
    public static void main(String[] args)
    {
        try
        {
            MaxPathProblem aGrid = null;
            long startTime = System.currentTimeMillis();            
            
            // load easy
            aGrid = readProblem("easy_problem.txt");
            System.out.println("Loaded easy!");
            
            // load medium
            aGrid = readProblem("medium_problem.txt");
            System.out.println("Loaded medium!");
            
            // load hard
            aGrid = readProblem("hard_problem.txt");
            System.out.println("Loaded hard!");
            
            long endTime = System.currentTimeMillis();            
            System.out.println("Finished: " + Utils.calculateTime(endTime-startTime) + " sec");
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    

    
    public final static MaxPathProblem readProblem(String aFilename)
    	throws Exception
    {        
        BufferedReader reader = null;
        byte [] grid = null;
        int gridWidth = 0;
        int dimensions = 0;
        int pathLength = 0;
        
        // load the data into memory
        String data = loadFile(aFilename);
        
        try
        {
	        reader = new BufferedReader(new StringReader(data));
	        data = null;
	        
	        // locate header
	        String [] header = retrieveHeader(reader);
	        // parse header data
            gridWidth = Integer.parseInt(header[0]);
            dimensions = Integer.parseInt(header[1]);
            pathLength = Integer.parseInt(header[2]);
            int totalSquares = (int) Math.pow(gridWidth, dimensions);
            
            if(LOG)
            {
                System.out.println("Problem["+aFilename+"] Dimensions["+dimensions+"], GridWidth["+gridWidth+"], TotalSquares["+totalSquares+"], PathLength["+pathLength+"].");                
            }            
	        
	        // load the data	        
	        grid = new byte[totalSquares];
	        int totalElementsReader = loadData(reader, grid);	        
	        if(totalElementsReader != grid.length)
	        {
	            throw new Exception("Failed to read expected number of squares. Read["+totalElementsReader+"], Expected["+grid.length+"].");
	        }
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
	        
        // prepare 
        MaxPathProblem problem = new MaxPathProblem(grid, pathLength, dimensions, gridWidth);        
        return problem;  
    }    
    
    
    private final static int loadData(BufferedReader aReader, byte [] aDataArray)
    	throws Exception
    {
        int offset = 0;
        String line = null;
        
        // load the next line
        while(offset<aDataArray.length && (line = aReader.readLine()) != null)
        {                 
            if((line = line.trim()).length() > 0 && line.charAt(0)!=COMMENT)
            {
                aDataArray[offset++] = Byte.parseByte(line);
                if(!MaxPathProblem.isSquareValueValid(aDataArray[offset-1]))
                {
                    throw new Exception("Invalid square value read ["+aDataArray[offset-1]+"], at position ["+(offset-1)+"].");
                }
                
                if(LOG)
                {
                    if((offset%100000)==0)
                    {
                        System.out.println("Loaded ["+offset+"] nodes.");
                    }
                }
            }           
        }        
        
        if(LOG)
        {
            System.out.println("Loaded ["+offset+"] nodes.");
        }
        
        return offset;
    }
    
    private final static String [] retrieveHeader(BufferedReader aReader)
    	throws Exception
    {
        String line = null;
        boolean haveHeader = false;
        String [] header = null;
        
        while(!haveHeader && (line=aReader.readLine()) != null)
        {
            line = line.trim();
            if(line.length() > 1 && line.charAt(0)!=COMMENT)
            {
                if((header = line.split(" ")).length == 3)
                {
                    haveHeader = true;
                }
                else
                {
                    throw new Exception("First non-empty line in file is not a valid header.");
                }
            }
        }
        
        if(!haveHeader)
        {
            throw new Exception("Failed to locate header in file.");
        }
        
        return header;
    }
    
    
    public final static String loadFile(String aFilename)
    	throws Exception
	{
        FileReader reader = null;
        StringBuffer buffer = new StringBuffer(4096);
        
        try
        {
            reader = new FileReader(aFilename);
            char [] b = new char[4096];
            int numRead = 0;
            while((numRead=reader.read(b)) > 0)
            {
                buffer.append(b, 0, numRead);
            }
        } 
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                } 
                catch (IOException e)
                {} // ignore
            }
        }
        
        return buffer.toString();
	}
    
    
   
}
