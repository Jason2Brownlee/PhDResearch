
package funcopt.algorithms.utls;

import java.util.LinkedList;
import java.util.Random;

import funcopt.Problem;
import funcopt.Solution;

/**
 * Type: GAUtils<br/>
 * Date: 24/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GAUtils
{    
    public final static void binaryMutate(boolean [] string, Random r, double mutation)
    {        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }
    
    public final static boolean [][] binaryCrossover(boolean [] p1, boolean [] p2, Random r, double crossover)
    {      
        // make a cut - or no cut as it were
        int cutPoint = (r.nextDouble()<crossover) ? r.nextInt(p1.length) : 0;
        // create vectors
        boolean [] v1 = new boolean[p1.length];
        boolean [] v2 = new boolean[p1.length];
        // prepare vectors
        prepareVector(v1, p1, p2, cutPoint); // normal
        prepareVector(v2, p2, p1, cutPoint); // reversed
        // store children
        return new boolean[][]{v1,v2};
    }
    
    protected final static void prepareVector(boolean [] v, boolean [] p1, boolean [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }    
    
    public final static double hammingDistance(boolean [] b1, boolean [] b2)
    {        
        int diff = 0;        
        for (int i = 0; i < b1.length; i++)
        {
            if(b1[i] != b2[i])
            {
                diff++;
            }
        }
        
        return diff;
    }
    
    public final static double hammingRatioDistance(boolean [] b1, boolean [] b2)
    {         
        return ((double)hammingDistance(b1,b2) / (double)b1.length);
    }
   
    public final static double euclideanDistance(Solution s1, Solution s2)
    {
        double [] c1 = s1.getCoordinate();
        double [] c2 = s2.getCoordinate();
        
        // root of the sum of the squared differences (Euclidean)
        
        double sum = 0.0;
        for (int i = 0; i < c2.length; i++)
        {
            sum += Math.pow((c1[i] - c2[i]), 2);
        }
        return Math.sqrt(sum);
    }
    
    
    /**
     * 
     * @param pop
     * @param numToSelection
     * @param p
     * @param r
     * @param boutSize
     * @return
     */
    public final static LinkedList<Solution> tournamentSelection(
            LinkedList<Solution> pop, 
            int numToSelection,
            Problem p,
            Random r,
            int boutSize)
    {
        LinkedList<Solution> selected = new LinkedList<Solution>();
        
        if((numToSelection%2) != 0)
        {
            numToSelection++; // need one more for luck (even)
        }
        
        // permits reselection!!!
        while(selected.size() < numToSelection)
        {
            Solution best = pop.get(r.nextInt(pop.size()));
            for (int i = 1; i < boutSize; i++)
            {
                Solution s = pop.get(r.nextInt(pop.size()));
                if(p.isBetter(s, best))
                {
                    best = s;
                }
            }
            selected.add(best);
        }
        
        return selected;
    }    
    
    
    public final static void realValueGlobalGaussianMutate(Solution s, Problem a, Random r, double mutation, double stdev)
    {   
        double [][] minmax = a.getMinmax();
        double [] p = s.getCoordinate();
        for (int i = 0; i < p.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                double range = (minmax[i][1] - minmax[i][0]); 
                p[i] += (r.nextGaussian() * (range * stdev));
            }
        }
        PopulationAlgorithmUtils.bounceCoord(s.getCoordinate(), a);       
    }
    
    public final static Solution [] realValueUniformCrossover(Solution s1, Solution s2, Random r, double crossover)
    {
        double [] p1 = s1.getCoordinate();
        double [] p2 = s2.getCoordinate();
        double [] child1 = new double[p1.length];
        double [] child2 = new double[p1.length];
        if(r.nextDouble() < crossover)
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = r.nextBoolean() ? p1[i] : p2[i];
                child2[i] = r.nextBoolean() ? p1[i] : p2[i];
            }     
        }
        else
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = p1[i];
                child2[i] = p2[i];
            }
        }
        return new Solution[]{new Solution(child1), new Solution(child2)};       
    }
}
