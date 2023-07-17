
package swsom.algorithm;

/**
 * Type: Map<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public interface FeatureMap
{
    ExemplarVector[] getVectors();
    
    VectorConnection[] getConnections();
}
