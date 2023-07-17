package comopt.algorithms.utils;

import java.util.Random;

import comopt.Problem;
import comopt.Solution;

/**
 * Type: RandomUtils<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class RandomUtils
{
    /**
     * Generate a random coordinate within the bounds of the problem domain
     * 
     * @param p
     * @param r
     * @return
     */
    public final static Solution randomSolutionRange(Problem p, Random r)
    {
        return new Solution(generateRandomVector(p,r));
    }

    public final static int[] generateRandomVector(Problem p, Random r)
    {
        int[] v = new int[p.getCities().length];
        for (int i = 0; i < v.length; i++)
        {
            v[i] = i;
        }
        shuffle(v, r);
        return v;
    }

    protected final static void shuffle(int[] v, Random r)
    {
        for (int i = 0; i < v.length; i++)
        {
            randomSwap(v, r);
        }
    }

    protected final static void randomSwap(int[] v, Random r)
    {
        int s1 = r.nextInt(v.length);
        int s2 = r.nextInt(v.length);

        int a = v[s1];
        v[s1] = v[s2];
        v[s2] = a;
    }
}
