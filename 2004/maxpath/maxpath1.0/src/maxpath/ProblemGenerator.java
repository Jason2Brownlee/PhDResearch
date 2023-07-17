/**
 * Created on 8/11/2004
 *
 */
package maxpath;
import java.io.FileWriter;
import java.util.Random;

/**
 * Type: ProblemGenerator
 * File: ProblemGenerator.java
 * Date: 8/11/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class ProblemGenerator
{
    protected final String outputFilename;
    
    protected final long randomSeed;
    
    protected final int numDimensions;
    
    protected final int gridWidth;
    
    protected final int pathLength;
    
    
    
    public ProblemGenerator(
            String out, 
            long aSeed, 
            int aNumDimen, 
            int aWidth,
            int aLength)
    {
        outputFilename = out;
        randomSeed = aSeed;
        numDimensions = aNumDimen;
        gridWidth = aWidth;
        pathLength = aLength;
    }
    
    public static void main(String[] args)
    {
        try
        {
            // easy
//            ProblemGenerator easy = new ProblemGenerator(
//                    "easy_problem.txt", 
//                    System.currentTimeMillis(), 
//                    2, 
//                    30, 
//                    60);
//            easy.generate();
//            System.out.println("Generated Easy Problem");
//            
//            
//            
//            // medium
//            ProblemGenerator medium = new ProblemGenerator(
//                    "medium_problem.txt", 
//                    System.currentTimeMillis(), 
//                    3, 
//                    50, 
//                    200);
//            medium.generate();
//            System.out.println("Generated Medium Problem");
            
            
            
            // hard
            ProblemGenerator hard = new ProblemGenerator(
                    "hard_problem.txt", 
                    System.currentTimeMillis(), 
                    5, 
                    15, 
                    10000);
            hard.generate();
            System.out.println("Generated Hard Problem");
        }         
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public void generate()
    	throws Exception
    {
        Random rand = new Random(randomSeed);        
        int numSquares = (int) Math.pow(gridWidth, numDimensions);
        FileWriter writer = null;
        
        try
        {
            writer = new FileWriter(outputFilename, false);
            StringBuffer buffer = new StringBuffer(1024);
            
            // header
            buffer.append(gridWidth);
            buffer.append(" ");
            buffer.append(numDimensions);
            buffer.append(" ");
            buffer.append(pathLength);
            buffer.append("\n");
            
            for (int i = 0; i < numSquares; i++)
            {
                int num = rand.nextInt(16) + 1;  
                buffer.append(num);
                buffer.append("\n");
                
                if((i%50000)==0)
                {
                    writer.write(buffer.toString()); 
                    buffer = new StringBuffer(1024);
                }
            } 
            
            writer.write(buffer.toString()); 
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
