package jb.selfregulation.views.competition;

//
//  Test.java
//  huygensClient
//
//  Created by Cara MacNish on 18/05/05.
//  Copyright 2005 CSSE, UWA. All rights reserved.
//

/**
 * <p>
 * Before running the test program, you should register and login at the Huygens
 * website.
 * </p>
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 *  Single point test:
 *    java -cp &quot;huygensClient.jar&quot; myemail@myadmin
 * 
 *  Single point test:
 *    java -cp &quot;huygensClient.jar&quot; myemail@mydomain 0.5 0.5
 * 
 *  Training run:
 *    java -cp &quot;huygensClient.jar&quot; myemail@myadmin 20_101
 * 
 * </pre>
 */
public class Test
{

    public static final int NUM_MOONS = 10;

    public static final int PROBES_PER_MOON = 1000;

    public static void main(String[] args)
    {
        if (args.length == 3)
        {
            // single point test (training run)
            // args are email and two real values
            Client cl = new Client(args[0], "20_1");
            System.out.println(cl.getFitness(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            System.out.println(cl.getIteration());
            System.out.println(cl.getMessage());
        }
        else if (args.length == 2)
        {
            // training run
            // args are email and moon code (eg 20_101)
            Client cl = new Client(args[0], args[1]);
            double minimum = 0;
            double depth;
            for (int i = 1; i <= 100; i++)
            {
                // for full training run set i<= PROBES_PER_MOON
                depth = cl.getFitness(Math.random(), Math.random());
                if (depth < minimum)
                    minimum = depth;
                System.out.println(cl.getIteration() + " \t" + depth + " \t" + minimum);
            }
            System.out.println("Minimum found: " + minimum);
        }
        else if (args.length == 1)
        {
            // single point test (training run)
            // args are email
            System.out.println("\nConnecting to host..");
            Client cl = new Client(args[0], "20_1");
            System.out.println("Probing training moon 20_1, location 0.5 0.5");
            System.out.println("Depth is: " + cl.getFitness(0.5, 0.5));
            System.out.println(cl.getMessage() + "\n");

            /*
             * // benchmark run // arg is email Client cl = new Client(args[0]);
             * double depth; for (int j=1; j <= NUM_MOONS; j++) { double minimum =
             * 0; for (int i=1; i <= PROBES_PER_MOON; i++) { depth =
             * cl.getFitness(Math.random(), Math.random());
             * System.out.print(cl.getIteration() + " ");
             * System.out.println(cl.getMessage()); if (depth < minimum) minimum =
             * depth; } System.out.println(minimum); }
             */
        }

    }

}
