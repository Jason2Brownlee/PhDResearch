
package swsom.gui;

import java.util.Random;

import swsom.algorithm.Algorithm;
import swsom.algorithm.Problem;
import swsom.algorithm.SOMMap;
import swsom.algorithm.problem.SquareProblem;
import swsom.algorithm.problem.TriangleProblem;
import swsom.algorithm.problem.HProblem;
import swsom.algorithm.problem.CircleProblem;
import swsom.algorithm.problem.FourSquaresProblem;

/**
 * Type: Configuration<br/>
 * Date: 26/02/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Configuration
{    
    protected final static Problem [] PROBLEMS = 
    {
        new SquareProblem(),
        new TriangleProblem(),
        new HProblem(),
        new CircleProblem(),
        new FourSquaresProblem()
    };
    
    
    protected Problem problem;
    protected double [][] sample;
    protected Algorithm algorithm;
    protected SOMMap map;
    protected Random rand;
    
    protected long wait;
    protected long iterations;
    protected String name;
    protected int sampleSize;
    
    
    public void setup(Problem aProblem, Random aRand)
    {
        // store required elements
        problem = aProblem;
        rand = aRand;
        // set things up
        sample = problem.generateSampleWithoutReplacement(rand, sampleSize);
        // algorithm is always last
        prepareAlgorithm();
    }
    
    public void setup()
    {
        setup(PROBLEMS[1], new Random());
    }
    
    public Configuration()
    {
        wait = 0;
        iterations = 50000;
        sampleSize = 2000;
        name = "Unknown System";
    }
    
   
    public abstract void prepareAlgorithm();
        
    
    public Problem getProblem()
    {
        return problem;
    }
    
    public Algorithm getAlgorithm()
    {
        return algorithm;
    }
    
    public SOMMap getMap()
    {
        return map;
    }
    
    /**
     * @return Returns the iterations.
     */
    public long getIterations()
    {
        return iterations;
    }
    /**
     * @return Returns the wait.
     */
    public long getWait()
    {
        return wait;
    }
    
    
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    
    
    /**
     * @return Returns the rand.
     */
    public Random getRand()
    {
        return rand;
    }
    
    
    /**
     * @return Returns the sample.
     */
    public double[][] getSample()
    {
        return sample;
    }
}
