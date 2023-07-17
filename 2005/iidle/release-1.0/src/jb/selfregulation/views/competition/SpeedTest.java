
package jb.selfregulation.views.competition;

import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;


/**
 * 
 * Type: SpeedTest<br/>
 * Date: 22/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SpeedTest
{
    /**
     * 
     * 1001 runs
     * Mean:  266.4765234765235
     * STdev: 119.1886055724361
     * Total: 268055
     * No exception
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        int TOTAL = 1000;
        double [] times = new double[TOTAL];
             
        
        /*
        Client c = new Client(Client.JASON_EMAIL, "20_100");
        for (int i = 0; i < TOTAL; i++)
        {
            long s = System.currentTimeMillis();
            c.getFitness(0.5, 0.5);
            long e = System.currentTimeMillis();
            times[i] = (e-s);
            System.out.println(i + ", " + times[i]);
        }
        */
        
        
        /**
         * Mean:  0.0
         * STdev: 0.0
         * Total: 1.3026333333333333
         */
        
        LinkedList<double []> coords = new LinkedList<double []>();
        for (int i = 0; i < TOTAL; i++)
        {
            coords.add(new double[]{0.5, 0.5});
        }
        LeanTestClient t = new LeanTestClient();
        t.prepareConnection();
        
        long start = System.currentTimeMillis();
        
        double [] results = t.batchProcessPoints(20, 101, coords);
        
       /* for (int i = 0; i < TOTAL; i++)
        {
            long s = System.currentTimeMillis();
            t.evaluate(20, 101, coords.get(i)[0], coords.get(i)[1]);
            long e = System.currentTimeMillis();
            times[i] = (e-s);
            System.out.println(i + ", " + times[i]);
        }*/
        
        long end = System.currentTimeMillis();
        t.shutdown();
        
        
        System.out.println("Mean:  " + new Mean().evaluate(times));
        System.out.println("STdev: " + new StandardDeviation().evaluate(times));
        System.out.println("Total: " + ((end-start)/1000.0)/60.0);
    }
}
