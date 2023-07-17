
package funcopt.problem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Properties;
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
 * Type: HuygensClient<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * 
 * Built for reuse.
 * Load the properties once, 
 * startup, run points, shutdown
 * iterate this process as needed
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensClient
{
    public final static String PROPERTIES_FILENAME = "huygens.properties";
    
    protected boolean proxyEnabled;
    protected String proxyHost;
    protected int proxyPort;
    
    protected int serverMaxRetries;
    protected int serverMaxConnections;
    protected String serverLoginUrl;
    protected String serverBenchmarkUrl;
    protected int serverBenchmarkTotalMoons;
    protected int serverBenchmarkProbesPerMoon;
    protected boolean serverDebug;
    
    protected HttpClient client;
    protected MultiThreadedHttpConnectionManager manager;
    protected LinkedBlockingQueue<Runnable> queue;
    protected ThreadPoolExecutor executor;
    
    public HuygensClient()
    {}    
    
    
    public static void main(String[] args)
    {
        HuygensClient client = new HuygensClient();
        client.loadProperties();
        client.startup("jbrownlee@ict.swin.edu.au");
        
        // prepare some dummy coords
        LinkedList<double []> coords = new LinkedList<double []>();
        for (int i = 0; i < client.serverBenchmarkProbesPerMoon; i++)
        {
            coords.add(new double[]{Math.random(), Math.random()});
        }
        long s = System.currentTimeMillis();
        client.batchProcessPoints(20, 1, coords);
        long e = System.currentTimeMillis();
        System.out.println("Completed: " + (((e-s)/1000.0)/60.0)+" minutes ("+(e-s)+" millis)");
        client.shutdown();
    }
    
    public void loadProperties()
    {
        InputStream in = null;
        
        try
        {
            Properties p = new Properties();
            in = HuygensClient.class.getResourceAsStream("/"+PROPERTIES_FILENAME);
            p.load(in);
            // proxy
            proxyEnabled = Boolean.parseBoolean(p.getProperty("proxy.enabled"));
            proxyHost = p.getProperty("proxy.host");
            proxyPort = Integer.parseInt(p.getProperty("proxy.port"));
            // server
            serverMaxConnections = Integer.parseInt(p.getProperty("server.maxconnections"));
            serverMaxRetries = Integer.parseInt(p.getProperty("server.retries"));
            serverLoginUrl = p.getProperty("server.url.login");
            serverBenchmarkUrl = p.getProperty("server.url.benchmark");
            serverBenchmarkTotalMoons = Integer.parseInt(p.getProperty("server.benchmark.totalmoons"));
            serverBenchmarkProbesPerMoon = Integer.parseInt(p.getProperty("server.benchmark.probespermoon"));
            serverDebug = Boolean.parseBoolean(p.getProperty("server.debug"));
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to prepare test client.", e);
        }
        finally
        {
            if(in!=null)
            {
                try
                {
                    in.close();
                }
                catch (Exception e)
                {}
            }
        }
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
    
    public void startup(String email)
    {                
        // prepare threads
        queue = new LinkedBlockingQueue<Runnable>();        
        // allow the pool to empty when not used
        executor = new ThreadPoolExecutor(serverMaxConnections, serverMaxConnections, 10, TimeUnit.SECONDS, queue);
        executor.prestartAllCoreThreads();

        // prepare manager
        manager = new MultiThreadedHttpConnectionManager();        
        manager.getParams().setDefaultMaxConnectionsPerHost(serverMaxConnections);
        manager.getParams().setMaxTotalConnections(serverMaxConnections);
        
        // prepare client
        client = new HttpClient(manager);
        if(proxyEnabled)
        {
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);   
        }        
        manager.getParams().setMaxConnectionsPerHost(client.getHostConfiguration(), serverMaxConnections);
        
        PostMethod post = new PostMethod(serverLoginUrl);
        post.addParameter("email", email);
        
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
        PostMethod post = new PostMethod(serverBenchmarkUrl);
        post.addParameter("benchmark", "true");
        post.addParameter("x", Double.toString(x));
        post.addParameter("y", Double.toString(y));

        return getFitness(post);
    }    
    
    public double evaluate(int boulders, int seed, double x, double y)
    {
        PostMethod post = new PostMethod(serverBenchmarkUrl);
        post.addParameter("benchmark", "false");
        post.addParameter("boulders", Integer.toString(boulders));
        post.addParameter("seed", Integer.toString(seed)); 
        post.addParameter("x", Double.toString(x));
        post.addParameter("y", Double.toString(y));

        return getFitness(post);
    }    
    
    public double getFitness(PostMethod post)
    {
        byte [] data = new byte[1024*3]; // never more than this
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
            while(!haveData && ++totalTries < serverMaxRetries);
            
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
                    if(serverDebug)
                    {
                        System.err.println("f["+lines[i].trim()+"], i["+lines[i+1].trim()+"], m["+lines[i+2].trim()+"]");
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

    public boolean isProxyEnabled()
    {
        return proxyEnabled;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public int getServerBenchmarkProbesPerMoon()
    {
        return serverBenchmarkProbesPerMoon;
    }

    public int getServerBenchmarkTotalMoons()
    {
        return serverBenchmarkTotalMoons;
    }

    public String getServerBenchmarkUrl()
    {
        return serverBenchmarkUrl;
    }

    public boolean isServerDebug()
    {
        return serverDebug;
    }

    public String getServerLoginUrl()
    {
        return serverLoginUrl;
    }

    public int getServerMaxConnections()
    {
        return serverMaxConnections;
    }

    public int getServerMaxRetries()
    {
        return serverMaxRetries;
    }
    
    
}
