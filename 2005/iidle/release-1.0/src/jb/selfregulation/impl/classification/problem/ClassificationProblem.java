
package jb.selfregulation.impl.classification.problem;

import java.io.FileReader;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Normalize;

/**
 * Type: ClassificationProblem<br/>
 * Date: 29/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ClassificationProblem extends Problem
{
    protected Random rand;
    protected long totalEvaluations;
    protected String filename;
    protected double percentageSplit;
    protected boolean normalise;
    
    // datasets
    protected Instances allInstances;
    protected Instances training;
    protected Instances testing;
    
    
    @Override
    public long getTotalEvaluations()
    {
        return totalEvaluations;
    }

    public String getBase()
    {
        return ".classification";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + ".problem.classification";
        filename = prop.getProperty(b + ".filename");
        percentageSplit = Double.parseDouble(prop.getProperty(b + ".split"));
        normalise = Boolean.parseBoolean(prop.getProperty(b + ".normalise"));
    }

    public void setup(SystemState aState)
    {
        rand = aState.rand;
        
        // prepare the dataset
        loadDatafile();
        // partition the dataset into training and testing
        partitionDataset();
    }

    
    protected void partitionDataset()
    {
        int total = (int) Math.round(allInstances.numInstances() * percentageSplit);
        
        // fill both sets
        testing = new Instances(allInstances);
        training = new Instances(allInstances);
        // clear training so we can fill it up from testing
        training.delete();
        
        // remove from testing and add to training until training is at perscribed level
        while(training.numInstances() < total)
        {
            int selection = rand.nextInt(testing.numInstances());
            training.add(testing.instance(selection));
            testing.delete(selection);
        }
        
        logger.info("Training patterns - " + training.numInstances());
        logger.info("Testing patterns - " + testing.numInstances());
    }
         
    protected void loadDatafile()
    {
        try
        {
            allInstances = new Instances(new FileReader(filename));
            allInstances.setClassIndex(allInstances.numAttributes()-1);
            if(normalise)
            {
                Normalize filter = new Normalize();
                filter.setInputFormat(allInstances);
                allInstances = Filter.useFilter(allInstances, filter);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to preapre dataset "+filename+".", e);
        }
        
        logger.info("Datafile loaded - " + filename+", total patterns - " + allInstances.numInstances());
    }

    public Instances getAllInstances()
    {
        return allInstances;
    }

    public Instances getTesting()
    {
        return testing;
    }

    public Instances getTraining()
    {
        return training;
    }
    
    
    
}
