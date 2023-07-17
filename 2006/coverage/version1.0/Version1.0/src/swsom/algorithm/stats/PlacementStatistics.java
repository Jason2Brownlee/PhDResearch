
package swsom.algorithm.stats;

import java.text.DecimalFormat;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.FeatureMap;
import swsom.algorithm.Problem;
import swsom.algorithm.problem.GenericShapeProblem;

/**
 * Type: Statistics<br/>
 * Date: 9/03/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class PlacementStatistics
{
    protected static DecimalFormat format = new DecimalFormat();
    
    public static String preparePlacementStatisticReport(
            Problem aProblem, 
            FeatureMap aMap,
            double [][] aSample,
            int aInterval)
    {
        // store invalid placement statistics
        double invalidReplacements = PlacementStatistics.calculateTotalInvalidPlacements(aProblem, aMap);
        invalidReplacements = invalidReplacements * 100;
        
        // perpare distributions      
        double [][] sampleDistribution = ((GenericShapeProblem)aProblem).calculateDistributionMap(aInterval, aSample);
        sampleDistribution = ((GenericShapeProblem)aProblem).convertHistogramToProbabilityMap(sampleDistribution);
        // calculate distribution for map
        double [][] mapDistribution = ((GenericShapeProblem)aProblem).calculateDistributionMap(aInterval, aMap.getVectors());
        mapDistribution = ((GenericShapeProblem)aProblem).convertHistogramToProbabilityMap(mapDistribution);        
        double distributionError = PlacementStatistics.calculateHistogramError(sampleDistribution, mapDistribution);   
        
        StringBuffer b = new StringBuffer(1024);
        b.append("Distribution Error:....." + format.format(distributionError)+"\n");
        b.append("Invalid Replacements:..." + format.format(invalidReplacements)+"%\n");
        return b.toString();
    }
    

    public static double calculateDistributionError(
            double [][] sample, 
            GenericShapeProblem aProblem,
            FeatureMap map,
            int aInterval)
    {
        // calculate distribution for sample
        double [][] sampleDistribution = aProblem.calculateDistributionMap(aInterval, sample);
        sampleDistribution = aProblem.convertHistogramToProbabilityMap(sampleDistribution);
        // calculate distribution for map
        double [][] mapDistribution = aProblem.calculateDistributionMap(aInterval, map.getVectors());
        mapDistribution = aProblem.convertHistogramToProbabilityMap(mapDistribution);
        // calculate the error between the two maps
        return calculateHistogramError(sampleDistribution, mapDistribution);
    }
    
    public static double calculateHistogramError(
            double [][] h1, 
            double [][] h2)
    {
        double error = 0.0;
        for (int y = 0; y < h1.length; y++)
        {
            for (int x = 0; x < h1[y].length; x++)
            {
                double diff = (h1[y][x] - h2[y][x]);
                error += (diff*diff);
            }
        }
        return Math.sqrt(error);
    }
    
    public static double calculateTotalInvalidPlacements(
            Problem aProblem, 
            FeatureMap aMap)
    {
        int count = 0;
        
        ExemplarVector [] vectors = aMap.getVectors();
        for (int i = 0; i < vectors.length; i++)
        {
            double [] coord = vectors[i].getData();
            if(!((GenericShapeProblem)aProblem).isCoordInProblemSpace((int)Math.round(coord[0]), (int)Math.round(coord[1])))
            {
                count++;
            }
        }        
        
        return ((double)count / (double)vectors.length);
    }
}
