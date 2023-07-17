package jb.selfregulation.views.competition;

import java.util.Properties;

//
//  UniformSearch.java
//  huygensClient
//
//  Created by Cara MacNish on 18/05/05.
//  Copyright 2005 CSSE, UWA. All rights reserved.
//

/**
 * Sample program showing how to plug in your algorithm. Please view the source
 * code directly. See also Test.java for another example.
 * 
 * <p>
 * This program samples the terrain at uniform grid points.
 * </p>
 * 
 * <p>
 * There are really only two important lines that show use of the client:
 * </p>
 * 
 * <pre>
 * Client cl = new Client(args[0], args[1]);
 * depth = cl.getFitness(xx * increment, yy * increment);
 * </pre>
 * 
 * <p>
 * The rest of the code handles the logic of the uniform search, as well as some
 * print statements to see what is happening.
 * </p>
 */
public class UniformSearch
{
    public static final int NUM_MOONS = 10;
    public static final int PROBES_PER_MOON = 1000;

    public static void main(String[] args)
    {
        
        args = new String[]{Client.JASON_EMAIL, "20_99"};
        
//        Properties systemSettings = System.getProperties();
//        systemSettings.put("proxySet", "true");
//        systemSettings.put("proxyHost", "wwwproxy.swin.edu.au");
//        systemSettings.put("proxyPort", "8000");
//        System.setProperties(systemSettings);        
        
//      Properties systemSettings = System.getProperties();
//      systemSettings.put("proxySet", "true");
//      systemSettings.put("http.proxyHost", "wwwproxy.swin.edu.au");
//      systemSettings.put("http.proxyPort", "8000");
//      System.setProperties(systemSettings);
        
        
        
        if (args.length == 2)
        {
            // training run
            // args[1] is moon code, eg 20_101
            Client cl = new Client(args[0], args[1]);
            double minimum = 0;
            double depth;
            // calculate number of samples per dimension
            int numPerDim = (int) Math.ceil(Math.sqrt(PROBES_PER_MOON));
            double increment = 1.0 / numPerDim;
            System.out.println(numPerDim + " " + increment);
            int count = 1;
            // loop in both directions
            for (int yy = 0; yy < numPerDim; yy++)
            {
                for (int xx = 0; xx < numPerDim && count <= PROBES_PER_MOON; xx++)
                {
                    depth = cl.getFitness(xx * increment, yy * increment);
                    if (depth < minimum)
                        minimum = depth;
                    System.out.println(cl.getIteration() + " \t" + depth + " \t" + minimum);
                    count++;
                }
            }
            System.out.println("Minimum found: " + minimum);
        }
        else if (args.length == 1)
        {
            // benchmark run
            Client cl = new Client(args[0]);
            double depth;
            for (int j = 1; j <= NUM_MOONS; j++)
            {
                double minimum = 0;
                int numPerDim = (int) Math.ceil(Math.sqrt(PROBES_PER_MOON));
                double increment = 1.0 / numPerDim;
                int count = 1;
                for (int yy = 0; yy < numPerDim; yy++)
                {
                    for (int xx = 0; xx < numPerDim && count <= PROBES_PER_MOON; xx++)
                    {
                        depth = cl.getFitness(xx * increment, yy * increment);
                        if (depth < minimum)
                            minimum = depth;
                        System.out.print(cl.getIteration() + " ");
                        System.out.println(cl.getMessage());
                        count++;
                    }
                }
            }
        }
    }

    

    
    
}
