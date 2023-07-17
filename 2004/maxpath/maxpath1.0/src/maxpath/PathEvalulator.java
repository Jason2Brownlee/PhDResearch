
package maxpath;

import java.util.LinkedList;

/**
 * Created on 9/11/2004
 *
 */
/**
 * Type: PathEvalulator
 * File: PathEvalulator.java
 * Date: 9/11/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class PathEvalulator
{
    protected final String problemFilename;  
    protected final String solutionFilename;
    
    protected MaxPathProblem problem;
    protected int [][] solution;
    
    public PathEvalulator(String aProblem, String aSolution)
    {
        problemFilename = aProblem;
        solutionFilename = aSolution;
    }    
    
    public static void main(String[] args)
    {
        try
        {
            if(args.length != 2)
            {
                throw new Exception("Invalid usage, expect: PathEvalulator <problem_filename> <solution_filename>");
            }
            
            PathEvalulator eval = new PathEvalulator(args[0], args[1]);
            
            try
            {
                eval.load();
            }
            catch(Exception e)
            {
               throw new Exception("Failed to load problem and solution files.",e);
            }
            
            try
            {
                int score = eval.score();
                System.out.println("Problem File:  " + args[0]);
                System.out.println("Solution File: " + args[1]);
                System.out.println("Path:          " + MaxPathProblem.pathToHumanReadable(eval.getSolution()));
                System.out.println("Path Score:    " + score);
            }
            catch(Exception e)
            {
               throw new Exception("Failed to calculate path score.",e);
            } 
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public int score()
    {
        /*
        int score = 0;
        System.out.println("point, coord, offset, score, running score");
        for (int i = 0; i < solution.length; i++)
        {
            int offset = problem.getOffset(solution[i]);
            score += problem.getValue(solution[i]);
            String coord = solution[i][0]+" "+solution[i][1]+" "+solution[i][2];
            System.out.println(i+", ("+coord+"), "+offset+", "+problem.getValue(solution[i])+", "+score);
        }        
        */
        
        return problem.evaulatePath(solution);
    }
    
    
    public void load() throws Exception
    {
        // load problem
        problem = ProblemReader.readProblem(problemFilename);
        
        // load solution
        String solutionData = ProblemReader.loadFile(solutionFilename);
        int [][] pathData = parseSolutionFile(solutionData);
        
        // ensure the solution path is valid
        if(!problem.isPathValid(pathData))
        {
            throw new Exception("Provided solution file contains invalid path. " +
            		"Expected Length["+problem.getPathLength()+"] Length["+pathData.length+"], " +
            		"Contiguous["+problem.isPathContiguous(pathData)+"], " +
            		"Check that all coords are within the problem bounds!");
        }
        
        // store solution
        solution = pathData;
    }    
    
    
    protected int [][] parseSolutionFile(String solutionFileData)
    	throws Exception
    {
        int lineNumber = 0;
        LinkedList<int []> pathList = new LinkedList<int []>();
        
        // split into lines
        String [] lines = solutionFileData.trim().split("\n");
        
        for(String aLine : lines)
        {
            lineNumber++;
            
            if(aLine== null)
            {
                continue;
            }
            else if((aLine=aLine.trim()).length() < 1)
            {
                continue;
            }
            else if(aLine.charAt(0) == ProblemReader.COMMENT)                
            {
                continue;
            }
            
            // break into parts
            String [] valueData = aLine.split(" ");
            
            if(valueData.length != problem.numDimensions)
            {
                throw new Exception("Line "+lineNumber+" contains invid number of dimensions. Expected["+problem.numDimensions+"], Found["+valueData.length+"].");
            }
            
            int [] values = new int[problem.numDimensions];
            for (int i = 0; i < values.length; i++)
            {
                values[i] = Integer.parseInt(valueData[i]);
            }
            
            // add to path
            pathList.add(values);
        }
        
        return pathList.toArray(new int[pathList.size()][]);
    }
    
    
    
    
    /**
     * @return Returns the problem.
     */
    public MaxPathProblem getProblem()
    {
        return problem;
    }
    /**
     * @return Returns the problemFilename.
     */
    public String getProblemFilename()
    {
        return problemFilename;
    }
    /**
     * @return Returns the solution.
     */
    public int[][] getSolution()
    {
        return solution;
    }
    /**
     * @return Returns the solutionFilename.
     */
    public String getSolutionFilename()
    {
        return solutionFilename;
    }
}
