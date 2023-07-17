
package swsom.algorithm.stats;

import java.awt.Rectangle;
import java.text.DecimalFormat;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.MathUtils;
import swsom.algorithm.Problem;
import swsom.algorithm.SOMMap;

/**
 * Type: CoverageStatistics<br/>
 * Date: 17/03/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class CoverageStatistics
{
    protected static DecimalFormat format = new DecimalFormat();
    
    /**
     * Sum of the vector errors
     * 
     * @param aMap
     * @param sample
     * @return
     */
    public static double calculateQuantisationError(SOMMap aMap, double [][] sample)
    {
        double sum = 0.0;
        for (int i = 0; i < sample.length; i++)
        {
            ExemplarVector bmu = aMap.getBestMatchingUnit(sample[i]);
            sum += MathUtils.distanceEuclidean(bmu.getData(), sample[i]);
        }      
        return sum;
    }

    public static String prepareCoverageReport(
            SOMMap aMap, 
            Problem aProble,
            double [][] sample)
    {
        StringBuffer b = new StringBuffer(1024);
        
        double totalModelArea = calculateTotalModelArea(aMap);     
        
        b.append("Quantisation Error:........" + format.format(calculateQuantisationError(aMap, sample)) + "\n");
        b.append("Total Model Area:.........." + format.format(totalModelArea) + "\n");
        b.append("Overlap Area:.............." + format.format((calculateOverlapArea(aMap)/totalModelArea)*100.0) + "%\n");
        b.append("Discrimination Accuracy:..." + format.format(calculateDiscriminationAccuracy(aMap, sample)) + "%\n");
        
        return b.toString();
    }
    
    public static double calculateOverlapArea(SOMMap aMap)
    {
        double sum = 0.0;
        ExemplarVector [] vectors = aMap.getVectors();
        for (int i = 0; i < vectors.length; i++)
        {
            Rectangle r1 = vectors[i].getSquareRectangle();
            // check for intersections with all remaining rectangules
            for (int j = i+1; j < vectors.length; j++)
            {
                Rectangle r2 = vectors[j].getSquareRectangle();
                if(r1.intersects(r2))
                {
                    Rectangle intersection = r1.intersection(r2);
                    sum += (intersection.width*intersection.height);
                }
            }
        }
        return sum;
    }
    
    public static double calculateDiscriminationAccuracy(SOMMap aMap, double [][] sample)
    {
        double correct = 0.0;
        ExemplarVector [] vectors = aMap.getVectors();
        
        for (int i = 0; i < sample.length; i++)
        {
            boolean located = false;
            for (int j = 0; !located && j < vectors.length; j++)
            {
                Rectangle r1 = vectors[j].getSquareRectangle();
                if(r1.contains((int)Math.round(sample[i][0]), (int)Math.round(sample[i][1])))
                {
                    correct++;
                    located = true;
                }
            }
        }
        
        return (correct / sample.length) * 100.0;
    }
    
    
    public static double calculateTotalModelArea(SOMMap aMap)
    {
        double sum = 0.0;
        ExemplarVector [] vectors = aMap.getVectors();
        
        for (int i = 0; i < vectors.length; i++)
        {
            Rectangle r1 = vectors[i].getSquareRectangle();
            sum += (r1.width * r1.height);
        }
        
        return sum;
    }
    
    
    public static double calculateTotalNonOverlappingArea( 
            double modelArea,
            double modelOverlapArea)
    {
        return (modelArea - modelOverlapArea);
    }
    
    
    
    
    public static double calculateTruePositiveShapeCoverage(SOMMap aMap, Problem aProblem)
    {
        double sum = 0.0;
        
        return sum;
    }
    
    public static double calculateFalsePositiveShapeCoverage(SOMMap aMap, Problem aProblem)
    {
        double sum = 0.0;
        
        return sum;
    }
}
