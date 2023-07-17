
package swsom.algorithm;

/**
 * Type: NeighbourhoodFunction<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public interface NeighbourhoodFunction
{
    boolean isDistanceInRadius(double aDistance, double aNeighbourhoodSize);
    
    double calculateNeighbourhoodAdjustedLearningRate(double aCurrentLearningRate, double aDistance, double aCurrentNeighbourhoodSize);
}
