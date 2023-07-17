/**
 * Created on 9/11/2004
 *
 */
package maxpath;
import java.util.Random;


/**
 * Type: RandomPathGenerator
 * File: RandomPathGenerator.java
 * Date: 9/11/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class RandomPathGenerator
{
    
    protected final MaxPathProblem problem;
    
    protected final Random rand;
    
    
    
    public RandomPathGenerator(MaxPathProblem aProblem)
    {
        problem = aProblem;
        rand = new Random(); // systime
    }
    
    
    public static void main(String[] args)
    {
        try
        {
            MaxPathProblem problem = null;

            // pick your problem
            problem = ProblemReader.readProblem("easy_problem.txt");
//            problem = ProblemReader.readProblem("medium_problem.txt");
//            problem = ProblemReader.readProblem("hard_problem.txt");
            
            System.out.println("Problem loaded.");
            RandomPathGenerator generator = new RandomPathGenerator(problem);
            
            int numPaths = 1000;
            int [][] bestPath = null;
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < numPaths; i++)
            {
                int [][] path = generator.generateRandomPath();
                int score = generator.evaulatePath(path);
                System.out.println("Score: " + score);
                
                if(score > bestScore)
                {
                    bestScore = score;
                    bestPath = path;
                }
            }
            
            System.out.println("Best Score: " + bestScore);
            System.out.println("Path: " + MaxPathProblem.pathToHumanReadable(bestPath));
            System.out.println(MaxPathProblem.pathToSolutionFileFormat(bestPath));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public int [][] generateRandomPath()
    {
        int [][] path = new int[problem.getPathLength()][];
        
        // select random starting point
        path[0] = getRandomPoint();
        
        for (int i = 1; i < path.length; i++)
        {
            // duplicate the last movement
            path[i] = duplicate(path[i-1]);
            
            // select one dimension at random to adjust
            int dimension = rand.nextInt(path[i].length);  
            if(path[i][dimension] == problem.getGridWidth()-1)
            {
                path[i][dimension]--;
            }
            else if(path[i][dimension] == 0)
            {
                path[i][dimension]++;
            }
            else
            {
                if(rand.nextBoolean())
                {
                    path[i][dimension]--;
                }
                else
                {
                    path[i][dimension]++;
                }
            }
            
            if(!problem.isCoordWithinBounds(path[i]))
            {
                throw new RuntimeException("Coord is invalid!");
            }
        }
        
        if(!problem.isPathContiguous(path))
        {
            throw new RuntimeException("Generate a path that is not contigious!");
        }
        
        return path;
    }
    
    public final static int [] duplicate(int [] aPoint)
    {
        int [] point = new int[aPoint.length];
        System.arraycopy(aPoint, 0, point, 0, aPoint.length);
        return point;
    }
    
    protected int [] getRandomPoint()
    {
        int [] point = new int[problem.getNumDimensions()];
        
        for (int i = 0; i < point.length; i++)
        {
            point[i] = rand.nextInt(problem.getGridWidth());
        }
        
        return point;
    }    
    
    public int evaulatePath(int [][] aPath)
    {
        return problem.evaulatePath(aPath);
    }    
}
