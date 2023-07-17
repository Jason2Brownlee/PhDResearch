
package jb.selfregulation.views.competition;

import java.util.LinkedList;

import jb.selfregulation.impl.functopt.problem.Function;


/**
 * Type: CompetitionFunction<br/>
 * Date: 22/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class CompetitionFunction extends Function
{
    protected final static double [][] minmax = {{0,1},{0,1}};
    protected final static int MAX_ITERATIONS = 1000;
    
    protected LeanTestClient client;
    
    protected String moonName;    
    protected boolean benchmark;
    protected int boulders;
    protected int seed;
    
    public CompetitionFunction()
    {
        setBitsPerVariate(63);
        setNumDimensions(2);
    }
    


    public void prepare()
    {        
        client = new LeanTestClient();
        client.prepareConnection();
    }
    /**
     * 
     * @param aMoonName - null for benchmarking
     */
    public void setMoon(String aMoonName)
    {
        moonName = aMoonName;
        
        if(moonName == null)
        {
            benchmark = true;
            seed = -1;
            boulders = -1;
        }
        else
        {
            String[] bns = moonName.split("_");
            boulders = Integer.parseInt(bns[0]);
            seed = Integer.parseInt(bns[1]);
            benchmark = false;
        }
    }
    

    @Override
    public double[][] getGenotypeMinMax()
    {
        return minmax;
    }

    public void reset()
    {
        totalEvaluations = 0;
    }
    
    public double [] evaluate(LinkedList<double []> v)
    {
        if(totalEvaluations+v.size()>MAX_ITERATIONS)
        {
            throw new RuntimeException("Unable to evaluate, will exceede limit");
        }
        
        totalEvaluations += v.size();
        
        if(benchmark)
        {
            return client.batchProcessPoints(v);
        }
        
        return client.batchProcessPoints(boulders, seed, v);
    }
    
    
    
    
    @Override
    public double evaluate(double[] v)
    {
        if(totalEvaluations >= MAX_ITERATIONS)
        {
            throw new RuntimeException("Unable to evaluate, evaluated " + totalEvaluations + " times.");
        }
        
        totalEvaluations++;
        
        if(benchmark)
        {
            return client.evaluate(v[0], v[1]);
        }
        
        return client.evaluate(boulders, seed, v[0], v[1]);        
    }

    @Override
    public double getBestFitness()
    {
        throw new UnsupportedOperationException("Unknown best solution.");
    }

    @Override
    public boolean isMinimisation()
    {
        return true; // always minimise
    }

    @Override
    public double[] getBestCoord()
    {
        throw new UnsupportedOperationException("Unknown best solution.");
    }

    @Override
    public boolean supportsJitter()
    {
        return false;
    }

    @Override
    public boolean supportsDynamic()
    {
        return false;
    }



    public LeanTestClient getClient()
    {
        return client;
    }
    
    
    
}
