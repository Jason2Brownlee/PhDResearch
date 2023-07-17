
package swsom.algorithm;

import java.util.LinkedList;
import java.util.Random;

import swsom.algorithm.maps.TwoDimensionalLatticeMap;

/**
 * Type: MapInitialiser<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class MapInitialiser
{
    
    /**
     * 
     * @param aMap
     * @param aProblem
     * @param rand
     * @param aSample
     */
    public static void initialiseMapUniform(
            TwoDimensionalLatticeMap aMap, 
            Random rand,
            double [][] aSample)
    {
        // create random vector
        ExemplarVector [] v = aMap.getVectors();
        for (int i = 0; i < v.length; i++)
        {            
            int index = rand.nextInt(aSample.length);
            double [] data = new double[aSample[index].length];
            System.arraycopy(aSample[index], 0, data, 0, data.length);
            
//            double [] data = aProblem.getRandomPointInDomain(rand);
//            double [] data = aProblem.getRandomPointInProblemSpace();
            v[i] = new ExemplarVector(i, data);
        }
        // set coordinates
        calculateVectorCoords(aMap);
        // connectivity
        calculateVectorConnectivity(aMap);
    }
    
    protected static void calculateVectorCoords(TwoDimensionalLatticeMap aMap)
    {
        ExemplarVector [] v = aMap.getVectors();
        
        for (int i = 0; i < v.length; i++)
        {
            double [] coord = aMap.calculateCoordinate(v[i].getId());
            v[i].setCoord(coord);
        }
    }    
    
    protected static void calculateVectorConnectivity(TwoDimensionalLatticeMap aMap)    
    {
        ExemplarVector [] v = aMap.getVectors();
        LinkedList<VectorConnection> connections = new LinkedList<VectorConnection>();
        
        for (int i = 0; i < v.length; i++)
        {
            // get all possible connectivity
            ExemplarVector [] allConnections = aMap.getConnectivity(v[i]);
            // process possible connectivity
            for (int j = 0; j < allConnections.length; j++)
            {
                // create a connection
                VectorConnection edge = new VectorConnection(v[i], allConnections[j]);
                // check if it can be added to the first
                if(!v[i].containsEdge(edge))                    
                {
                    // add to both
                    v[i].addConnection(edge);
                    allConnections[j].addConnection(edge);
                    // add to collection
                    connections.add(edge);
                }
            }
        }
        
        // store in the map
        aMap.setConnections(connections.toArray(new VectorConnection[connections.size()]));
    }
}
