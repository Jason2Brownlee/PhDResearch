//
//  Client.java
//  huygensClient
//
//  Created by Cara MacNish on 18/05/05.
//  Copyright 2005 CSSE, UWA. All rights reserved.
//

package jb.selfregulation.views.competition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * This class creates a client to the Huygens Benchmark Server.<br>
 * To use the class:<br>
 * <p>
 * 1. Register or login to the Huygens website.
 * </p>
 * <p>
 * 2. Create a client object for training (eg):
 * </p>
 * 
 * <pre>
 * Client client = new Client(email, &quot;20_101&quot;);
 * </pre>
 * 
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;or for benchmarking:
 * </p>
 * 
 * <pre>
 * Client client = new Client(email);
 * </pre>
 * 
 * <p>
 * 3. Call the fitness method for each evalutation (eg):
 * </p>
 * 
 * <pre>
 * fitness = getFitness(x, y);
 * </pre>
 */
public class Client
{
    public final static String SWINBURNE_PROXY_HOST = "wwwproxy.swin.edu.au";
    public final static int SWINBURNE_PROXY_PORT = 8000;
    public final static String JASON_EMAIL = "jbrownlee@ict.swin.edu.au";
    
    public static final String DOMAIN = "karri.csse.uwa.edu.au";
    public static final String LOCAL = "/cara/huygens";

    public static final String URL = "http://" + DOMAIN + LOCAL + "/auto/benchmark.php";
    public static final String SESSION_URL = "http://" + DOMAIN + LOCAL + "/auto/getSession.php";

    HttpClient client;
    HttpState state;
    Cookie cookie;
    String sessionID;
    boolean benchmark = false;
    int boulders = 0;
    int seed = 0;
    private int iteration = 0;
    private double fitness = 0;
    private String message;
    
    
    

    /**
     * Create a client for a training run.
     * 
     * @param email
     *            The email address you registered on the Huygens website.
     * @param moonID
     *            The ID of the moon (for example 20_101);
     */
    public Client(String email, String moonID)
    {
        initialise(email);
        benchmark = false;
        String[] bns = moonID.split("_");
        boulders = Integer.parseInt(bns[0]);
        seed = Integer.parseInt(bns[1]);
    }

    /**
     * Create a client for benchmarking.
     * 
     * @param email
     *            The email address you registered on the Huygens website.
     */
    public Client(String email)
    {
        initialise(email);
        benchmark = true;
    }

    private void initialise(String email)
    {
        client = new HttpClient();
        client.getHostConfiguration().setProxy(SWINBURNE_PROXY_HOST, SWINBURNE_PROXY_PORT);
        
        PostMethod post = new PostMethod(SESSION_URL);
        post.addParameter("email", email);
        try
        {
            int resultCode = client.executeMethod(post);
            BufferedReader br = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));
            String reply = br.readLine();
            // System.out.println(reply);
            if (reply.startsWith("Error"))
            {
                System.out.println(reply);
                System.out.println("Halting execution.");
                throw new RuntimeException();
            }
            br.close();
            sessionID = reply;
        }
        catch (IOException ex)
        {
            System.out.println("Error looking up session information.");
            // ex.printStackTrace();
            throw new RuntimeException("Halting execution.", ex);
        }
        finally
        {
            post.releaseConnection();
        }

        state = new HttpState();
        cookie = new Cookie(DOMAIN, "PHPSESSID", sessionID);
        cookie.setPath("/");
        state.addCookie(cookie);
        client.setState(state);

        // Display the cookies
        // Cookie[] cookies = client.getState().getCookies();
        // System.out.println("Present cookies: ");
        // for (int i = 0; i < cookies.length; i++) {
        // System.out.println(" - " + cookies[i].toExternalForm());
        // }
    }

    /**
     * The method called by your code to evaluate an individual's fitness.
     * 
     * @param x
     *            The x value.
     * @param y
     *            The y value. $return The fitness value.
     */
    public double getFitness(double x, double y)
    {
        PostMethod post = new PostMethod(URL);
        post.addParameter("benchmark", Boolean.toString(benchmark));
        if (!benchmark)
        {
            post.addParameter("boulders", Integer.toString(boulders));
            post.addParameter("seed", Integer.toString(seed));
        }
        post.addParameter("x", Double.toString(x));
        post.addParameter("y", Double.toString(y));

        try
        {
            int resultCode = client.executeMethod(post);
            BufferedReader br = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));
            fitness = Double.parseDouble(br.readLine());
            iteration = Integer.parseInt(br.readLine());
            message = br.readLine();
            br.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            // Release current connection to the connection pool once you are
            // done
            post.releaseConnection();
        }
        return fitness;
    }

    /**
     * Get the number of evaluations in the present run as returned by the
     * server.
     */
    public int getIteration()
    {
        return iteration;
    }

    /**
     * Get the last fitness returned by the server (same as the value returned
     * by getFitness).
     */
    public double getLastFitness()
    {
        return fitness;
    }

    /**
     * Get the message returned by the server, if any (primarily for debugging).
     */
    public String getMessage()
    {
        return message;
    }

}
