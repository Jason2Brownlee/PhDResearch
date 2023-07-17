
package funcopt.algorithms.utls;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import funcopt.Problem;
import funcopt.Solution;


/**
 * Type: GeneticAlgorithmUtils<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class PopulationAlgorithmUtils
{
    
    /**
     * Returns the best in the population, or the current best if 
     * no solution is better
     * 
     * @param pop
     * @param p
     * @param currentBest
     * @return
     */
    public static Solution getBest(LinkedList<Solution> pop, Problem p, Solution currentBest)
    {
        // discard all those without evaluation
        if(!p.isReamainingEvaluations())
        {
            for (Iterator<Solution> iter = pop.iterator(); iter.hasNext();)
            {
                Solution s = iter.next();
                if(Double.isNaN(s.getScore()))
                {
                    iter.remove();
                }                
            }
        }
        if(!pop.isEmpty())
        {
            // sort ascending
            Collections.sort(pop);
            // get potential new best
            Solution newBest = (p.isMinimise()) ? pop.getFirst() : pop.getLast();            
            // no current best
            if(currentBest == null)
            {
                return newBest; // by default
            }
            else if(p.isBetter(newBest, currentBest))
            {
                return newBest; // is better
            }
        }
        
        return currentBest;
    }
    
    /**
     * Wrap coords to the domain - essentiall making a torrid of the domain
     * 
     * @param coord
     * @param p
     */
    public static void wrapCoord(double [] coord, Problem p)
    {
        double [][] minmax = p.getMinmax();        
        
        for (int i = 0; i < coord.length; i++)
        {
            // a wrap could bounce beyond the opposite end of the domain
            while(coord[i] > minmax[i][1] || coord[i] < minmax[i][0])                
            { 
                // too large
                while(coord[i] > minmax[i][1])
                {
                    coord[i] -= minmax[i][1]; // wrap
                }
                // too small
                while(coord[i] < minmax[i][0])
                {
                    coord[i] += minmax[i][1]; // wrap
                } 
            }
        }
    }
    
    /**
     * treats the bounds of the problem as hard refelctive surfaces 
     * 
     * @param coord
     * @param p
     */
    public static void bounceCoord(double [] coord, Problem p)
    {
        double [][] minmax = p.getMinmax();        
        
        for (int i = 0; i < coord.length; i++)
        {            
            // a bounce could bounce beyond the opposite end of the domain
            while(coord[i] > minmax[i][1] || coord[i] < minmax[i][0])
            {                
                // too large
                while(coord[i] > minmax[i][1])
                {
                    // subtract the difference
                    double diff = Math.abs(coord[i] - minmax[i][1]);
                    // always smaller
                    coord[i] = (minmax[i][1] - diff);
                    
                }
                // too small
                while(coord[i] < minmax[i][0])
                {  
                    double diff = Math.abs(coord[i] - minmax[i][0]);
                    // always larger
                    coord[i] = (minmax[i][0] + diff);                    
                } 
            }
        }
    }
}
