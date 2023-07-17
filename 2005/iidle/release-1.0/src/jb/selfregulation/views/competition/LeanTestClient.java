
package jb.selfregulation.views.competition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Type: LeanTestClient<br/>
 * Date: 22/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LeanTestClient
{
    public final static boolean DEBUG = false;
    public final static int MAX_RETRIES = 20;    
    public final static int MAX_CONNECTIONS = 50;
    
    public final static String SWINBURNE_PROXY_HOST = "wwwproxy.swin.edu.au";
    public final static int SWINBURNE_PROXY_PORT = 8000;
    public final static String EMAIL = "jbrownlee@ict.swin.edu.au";
    
    public static final String URL         = "http://karri.csse.uwa.edu.au/cara/huygens/auto/benchmark.php";
    public static final String SESSION_URL = "http://karri.csse.uwa.edu.au/cara/huygens/auto/getSession.php";

    protected HttpClient client;
    protected MultiThreadedHttpConnectionManager manager;
    protected LinkedBlockingQueue<Runnable> queue;
    protected ThreadPoolExecutor executor;

    
    public static void main(String[] args)
    {
        Random r = new Random();
        LeanTestClient t = new LeanTestClient();
        t.prepareConnection();
        
        // one off test
//        for (int i = 0; i < 100; i++)
//        {
//            double f = t.evaluate(20, 100, r.nextDouble(), r.nextDouble());
//            System.out.println(f);
//        }
                
        
        // batch test        
        LinkedList<double []> coords = new LinkedList<double []>();
        for (int i = 0; i < 100; i++)
        {
            coords.add(new double[]{r.nextDouble(), r.nextDouble()});
        }
        double [] results = t.batchProcessPoints(20, 101, coords);
        
        t.shutdown();
        System.out.println("completed");         
    }    
    
    protected class RequestFitness implements Runnable
    {
        public final double x;
        public final double y;
        public final double [] results;
        public final int index;
        public final CountDownLatch latch;
        public final boolean benchmark;
        public final int boulders;
        public final int seed;
        
        public RequestFitness(double aX, double aY, CountDownLatch aLatch, double [] aResults, int aIndex)
        {
            x = aX;
            y = aY;
            latch = aLatch;
            benchmark = true;
            results = aResults;
            seed = -1;
            boulders = -1;
            index = aIndex;
        }
        public RequestFitness(double aX, double aY, CountDownLatch aLatch, double [] aResults, int aIndex, int aBoulders, int aSeed)
        {
            x = aX;
            y = aY;
            latch = aLatch;
            benchmark = false;
            seed = aSeed;
            boulders = aBoulders;
            results = aResults;
            index = aIndex;
        }
        
        public void run()
        { 
            if(benchmark)
            {
                results[index] = evaluate(x, y);
            }
            else
            {
                results[index] = evaluate(boulders, seed, x, y);
            }
            
            latch.countDown();
        }
    }
    
   
    
    protected double [] batchProcessPoints(LinkedList<double []> coords)
    {
        double [] results = new double[coords.size()];
        CountDownLatch latch = new CountDownLatch(coords.size());
        
        for (int i = 0; i < coords.size(); i++)
        {
            double [] coord = coords.get(i);
            RequestFitness rf = new RequestFitness(coord[0], coord[1], latch, results, i);
            executor.execute(rf);
        }
        try{latch.await();}catch (InterruptedException e){}
        return results;
    }
    
    protected double [] batchProcessPoints(
            final int boulders, 
            final int seed, 
            LinkedList<double []> coords)
    {        
        double [] results = new double[coords.size()];
        CountDownLatch latch = new CountDownLatch(coords.size());
        
        for (int i = 0; i < coords.size(); i++)
        {
            double [] coord = coords.get(i);
            RequestFitness rf = new RequestFitness(coord[0], coord[1], latch, results, i, boulders, seed);
            executor.execute(rf);
        }
        try{latch.await();}catch (InterruptedException e){}
        return results;
    }
    
    
    public void shutdown()
    {
        if(client != null)
        {
            executor.shutdown();
            manager.shutdown();
            manager = null;
            client = null;
            executor = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        shutdown();
    } 
    
    public void prepareConnection()
    {                
        // prepare threads
        queue = new LinkedBlockingQueue<Runnable>();        
        // allow the pool to empty when not used
        executor = new ThreadPoolExecutor(MAX_CONNECTIONS, MAX_CONNECTIONS, 10, TimeUnit.SECONDS, queue);
        executor.prestartAllCoreThreads();

        // prepare manager
        manager = new MultiThreadedHttpConnectionManager();        
        manager.getParams().setDefaultMaxConnectionsPerHost(MAX_CONNECTIONS);
        manager.getParams().setMaxTotalConnections(MAX_CONNECTIONS);
        
        // prepare client
        client = new HttpClient(manager);
        client.getHostConfiguration().setProxy(SWINBURNE_PROXY_HOST, SWINBURNE_PROXY_PORT);
        manager.getParams().setMaxConnectionsPerHost(client.getHostConfiguration(),MAX_CONNECTIONS);
        
        PostMethod post = new PostMethod(SESSION_URL);
        post.addParameter("email", EMAIL);
        
        String sessionID = null;
        
        try
        {
            int resultCode = client.executeMethod(post);
            if(resultCode != 200)
            {
                throw new RuntimeException("Invalid HTTP response code, expected OK (200): " + resultCode);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));
            String reply = br.readLine();
            if (reply.startsWith("Error"))
            {
                throw new RuntimeException("Error in reply from server: " + reply);
            }
            sessionID = reply;
            br.close();            
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Error looking up session information.", ex);
        }
        finally
        {
            post.releaseConnection();
        }

        // prepare cookie
        Cookie cookie = new Cookie("karri.csse.uwa.edu.au", "PHPSESSID", sessionID);
        cookie.setPath("/");
        // prepare state
        HttpState state = new HttpState();
        state.addCookie(cookie);
        // store state in http client
        client.setState(state);
    }
    
    
    public double evaluate(double x, double y)
    {
        PostMethod post = new PostMethod(URL);
        post.addParameter("benchmark", "true");
        post.addParameter("x", Double.toString(x));
        post.addParameter("y", Double.toString(y));

        return getFitness(post);
    }    
    
    public double evaluate(int boulders, int seed, double x, double y)
    {
        PostMethod post = new PostMethod(URL);
        post.addParameter("benchmark", "false");
        post.addParameter("boulders", Integer.toString(boulders));
        post.addParameter("seed", Integer.toString(seed)); 
        post.addParameter("x", Double.toString(x));
        post.addParameter("y", Double.toString(y));

        return getFitness(post);
    }    
    
    public double getFitness(PostMethod post)
    {
        byte [] data = new byte[1024*2]; // never more than this
        int total = 0;
        
        try
        {
            int totalTries = 0;
            boolean haveData = false;
            do
            {
                if(client.executeMethod(post) == HttpStatus.SC_OK)
                {
                    haveData = true;
                    InputStream in = post.getResponseBodyAsStream();
                    total = in.read(data, 0, data.length);
                    in.close();
                }
                // always release connection ASAP so another thread can have a go
                post.releaseConnection();
            }
            while(!haveData && ++totalTries < MAX_RETRIES);
            
            if(!haveData)
            {
                throw new RuntimeException("Failed to download data to get fitness - problem with webserver!");
            }
        }
        catch (Exception ex)
        {
            post.releaseConnection();
            throw new RuntimeException("Error getting fitness.", ex);            
        }
        // process web page data to get fitness
        double fitness = processFitness(data, total);
        return fitness;
    }
    
    
    protected double processFitness(byte [] data, int totalDataBytes)
    {
        double fitness = 0.0;
        
        String s = new String(data, 0, totalDataBytes);
        String [] lines = s.split("\n");
        
        try
        {            
            boolean found = false;
            for (int i = 0; !found && i < lines.length; i++)
            {
                if(!lines[i].startsWith("<"))
                {
                    fitness = Double.parseDouble(lines[i]); // process fitness
                    if(DEBUG)
                    {
                        System.out.println("f["+lines[i].trim()+"], i["+lines[i+1].trim()+"], m["+lines[i+2].trim()+"]");
                    }
                    found = true;
                }
            }
            if(!found)
            {
                throw new RuntimeException("Unexpected return value when getting fitness.\n" + s);
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unexpected return value when getting fitness.\n" + s, e);
        }
        
        return fitness;
    }
}
