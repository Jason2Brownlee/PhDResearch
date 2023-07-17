
package swsom.algorithm.neighbourhood;

import swsom.algorithm.NeighbourhoodFunction;

/**
 * Type: BubbleNeighbourhoodFunction<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class BubbleNeighbourhoodFunction implements NeighbourhoodFunction
{
    @Override
    public String toString()
    {
        return "Bubble";
    }
   
    /**
     * Must be within the bubble 
     * 
     * (non-Javadoc)
     * @see swsom.algorithm.NeighbourhoodFunction#isDistanceInRadius(double, double)
     */
    public boolean isDistanceInRadius(double aDistance, double aNeighbourhoodSize)
    {
        return (aDistance <= aNeighbourhoodSize);
    }
    
    /**
     * Uniform across the bubble 
     * 
     * (non-Javadoc)
     * @see swsom.algorithm.NeighbourhoodFunction#calculateNeighbourhoodAdjustedLearningRate(double, double, double)
     */
	public double calculateNeighbourhoodAdjustedLearningRate(double aCurrentLearningRate, double aDistance, double aCurrentNeighbourhoodSize)
	{		
		return aCurrentLearningRate;
	}
}
