
package comopt.algorithms.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import comopt.Problem;
import comopt.Solution;

/**
 * Type: AlgorithmUtils<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AlgorithmUtils
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
            Solution newBest = pop.getFirst(); // always minimise          
            // no current best
            if(currentBest == null)
            {
                return newBest; // by default
            }
            else if(newBest.getScore() < currentBest.getScore()) // always minimise
            {
                return newBest; // is better
            }
        }
        
        return currentBest;
    }    
    
    /**
     * Generate a solution of all nearest neighbour connections
     * @param p
     * @return
     */
    public final static Solution generateNearestNeighbourSolution(Problem p, Random r)
    {
        double [][] distanceMatrix = p.getDistanceMatrix();
        int [] permutation = new int[distanceMatrix.length];
        HashSet<Integer> set = new HashSet<Integer>();
        
        permutation[0] = r.nextInt(permutation.length);
        set.add(new Integer(permutation[0]));
        
        for (int i = 1; i < permutation.length; i++)
        {
            // select the best neighbour
            double min = Double.POSITIVE_INFINITY;
            Integer best = null;
            for (int j = 0; j < distanceMatrix[i].length; j++)
            {
                if(!set.contains(j))
                {
                    if(distanceMatrix[i][j] < min)
                    {
                        min = distanceMatrix[i][j];
                        best = j;
                    }
                }
            }
            set.add(best); // cannot revisit this city
            permutation[i] = best.intValue();
        }        
        
        return new Solution(permutation);
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
                if(s.getScore() < best.getScore())
                {
                    best = s;
                }
            }
            selected.add(best);
        }
        
        return selected;
    }    
    
    
    // a bit of a hack, but it seems to work
    public final static Solution edgeRecombination(
            Solution p1, Solution p2, Random r, double probability)
    {
        int [] v1 = p1.getPermutation();
        int [] v2 = p2.getPermutation(); 
        int totalCitites = v1.length;
        
        int [][] matrix = new int[totalCitites][totalCitites];
        // process the parents
        for (int i = 0; i < v1.length; i++)
        {
            int x = v1[i];
            int y = -1;
            if(i==v1.length-1)
            {
                y = v1[0];
            }
            else
            {
                y = v1[i+1];
            }
            matrix[x][y]++;
            matrix[y][x]++;
        }
        for (int i = 0; i < v2.length; i++)
        {
            int x = v2[i];
            int y = -1;
            if(i==v2.length-1)
            {
                y = v2[0];
            }
            else
            {
                y = v2[i+1];
            }
            matrix[x][y]++;
            matrix[y][x]++;
        }
        HashSet<Integer> set = new HashSet<Integer>();
        int [] c = new int[totalCitites];
        // pick a random initial random city
        c[0] = r.nextInt(totalCitites);
        set.add(c[0]);
        // clear this city 
        for (int k = 0; k < matrix.length; k++)
        {
            matrix[k][c[0]] = 0;
        }        
        
        for (int i = 1; i < c.length; i++)
        {
            LinkedList<Integer> list = new LinkedList<Integer>();
            boolean finished = false;
            
            // check the current edge list for a shared edge
            for (int j = 0; !finished && j < matrix[c[i-1]].length; j++)
            {
                // got one
                if(matrix[c[i-1]][j] > 1)
                {
                    c[i] = j; // take it     
                    if(set.contains(c[i])){throw new RuntimeException("Invalid permutation!");}
                    set.add(c[i]);
                    // clear from the entire matrix
                    for (int k = 0; k < matrix.length; k++)
                    {
                        matrix[k][j] = 0;
                    }
                    finished = true;
                }
                else if(matrix[c[i-1]][j] != 0)
                {
                    list.add(new Integer(j));
                }
            }
            if(finished)
            {
                continue; // next city
            }
            // check for an empty terminal
            if(list.isEmpty())
            {
//                LinkedList<Integer> listWithOutbound = new LinkedList<Integer>();
                
                // build a list of cities not visited, and select one as a new starting point
                for (int k = 0; k < matrix.length; k++)
                {
                    if(!set.contains(k))
                    {
                        list.add(k);
                        // process looking for any neighbours
//                        for (int j = 0; j < matrix[k].length; j++)
//                        {
//                            if(matrix[k][j] > 0)
//                            {
//                                listWithOutbound.add(k);
//                                break;
//                            }
//                        }
                    }
                }
                if(list.isEmpty())
                {
                    throw new RuntimeException("No cities remaining i["+i+"], l["+(c.length-1)+"]");
                }
                
                // randomly pick one of the adjacent cities
//                Integer s = (listWithOutbound.isEmpty()) ? list.get(r.nextInt(list.size())) : listWithOutbound.get(r.nextInt(listWithOutbound.size()));
                Integer s = list.get(r.nextInt(list.size()));
                c[i] = s.intValue(); // take it
                if(set.contains(c[i])){throw new RuntimeException("Invalid permutation!");}
                set.add(c[i]);
                // clear 
                for (int k = 0; k < matrix.length; k++)
                {
                    matrix[k][s.intValue()] = 0;
                }                
            }
            // the case where there is a connection            
            else
            {
                // randomly pick one of the adjacent cities
                Integer s = list.get(r.nextInt(list.size()));
                c[i] = s.intValue(); // take it
                if(set.contains(c[i])){throw new RuntimeException("Invalid permutation!");}
                set.add(c[i]);
                // clear 
                for (int k = 0; k < matrix.length; k++)
                {
                    matrix[k][s.intValue()] = 0;
                }
            }            
        }
        
        return new Solution(c);
    }
    
    public final static void completeTour(int [] p , int offset, Random r)
    {
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < offset; i++)
        {
            set.add(new Integer(p[i]));
        }
        // now build list of all remaining citites
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < p.length; i++)
        {
            Integer a = new Integer(i);
            if(!set.contains(a))
            {
                list.add(a);
            }
        }
        for (int i = offset; i < p.length; i++)
        {
            Integer s = list.remove(r.nextInt(list.size()));
            p[i] = s.intValue();            
        }
    }
    
    
    public final static Solution [] simpleCrossover(Solution p1, Solution p2, Random r, double probability)
    {    
        int [] v1 = p1.getPermutation();
        int [] v2 = p2.getPermutation();     
        // select cross point, or not as it were
        int cutPoint = (r.nextDouble()<probability) ? r.nextInt(v1.length) : 0;        
        // perform crossover
        int [] c1 = getCrossedPermutation(v1, v2, cutPoint);
        int [] c2 = getCrossedPermutation(v2, v1, cutPoint); 
        // return progeny
        return new Solution[]{new Solution(c1),new Solution(c2)};
    }
    
    public final static int [] getCrossedPermutation(
            int [] d1, 
            int [] d2, 
            int point)
    {
        int [] data = new int[d1.length];
        
        // copy the first block
        for (int i = 0; i < point; i++)
        {
            data[i] = d1[i];
        }
        
        // copy the remaining from the other        
        for (int i = point, offset = point; i < data.length; )
        {
            int next = d2[offset++];
            if(offset >= data.length)
            {
                offset = 0;
            }
            // check if the piece is useful
            if(!exists(next, data, i))
            {
                data[i++] = next;
            }
        } 
        return data;
    }    
    
    /**
     * Check the point already exists in the permutation
     * @param a
     * @param v
     * @param end
     * @return
     */
    public final static boolean exists(int a, int [] v, int end)
    {
        for (int i = 0; i < end; i++)
        {
            if(v[i] == a)
            {
                return true;
            }
        }
        
        return false;
    }    
    
    
    
    public final static void twoOpt(Solution s, Random r, double probability)
    {
        if(r.nextDouble() <= probability)
        {
            int [] data = s.getPermutation();
            int c1 = r.nextInt(data.length);
            int c2;
            do{c2=r.nextInt(data.length);}while(c2==c1);
            
            // ensure c1 is low, and c2 is high
            if(c1 > c2)
            {
                int a = c2;
                c2 = c1;
                c1 = a;
            }
            
            // now reverse the permutation between the points
            int half = (int) Math.floor((c2-c1)/2.0);
            for (int i = c1; i < c1+half; i++)
            {
                swap(i, c2-i, data);
            }
        }
    }
    
    
    /**
     * Swap selections in the permutation
     * @param s
     * @param r
     * @param probability
     */
    public final static void mutatePermutation(Solution s, Random r, double probability)
    {        
        int [] data = s.getPermutation();        
        for (int i = 0; i < data.length; i++)
        {
            if(r.nextDouble() <= probability)
            {
                // random swap
                swap(i, r.nextInt(data.length), data);
            } 
        }
    }
    
    /**
     * Perform a swap on the permutation
     * @param i
     * @param j
     * @param d
     */
    public final static void swap(int i, int j, int [] d)
    {
        int t = d[i];
        d[i] = d[j];
        d[j] = t;
    }
}
