
package swsom.algorithm.stats;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import swsom.algorithm.Edge;
import swsom.algorithm.FeatureMap;
import swsom.algorithm.Vertex;

/**
 * Type: GraphStatistics<br>
 * Date: 24/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class GraphStatistics
{
    public static DecimalFormat format = new DecimalFormat();
    
    public static String prepareGraphReport(FeatureMap aMap)
    {
        StringBuffer b = new StringBuffer(1024);
        double [] tmp = null;
        
        b.append("Total Vertices:............."+format.format(aMap.getVectors().length)+"\n");
        b.append("Total Edges:................"+format.format(aMap.getConnections().length)+"\n");
        tmp = calculateMeanDegree(aMap);
        b.append("Mean Degree:................"+format.format(tmp[0])+" ("+format.format(tmp[1])+")"+"\n");
        tmp = calculateMeanGeodesicDistance(aMap);
        b.append("Mean Geodesic Distance:....."+format.format(tmp[0])+" ("+format.format(tmp[1])+")"+"\n");
        
        return b.toString();
    }
    
    
    public static double [] calculateMeanDegree(FeatureMap aMap)
    {
        Vertex [] allNodes = aMap.getVectors();
        double [] scores = new double[allNodes.length];
        
        for (int i = 0; i < allNodes.length; i++)
        {
            scores[i] = allNodes[i].getEdges().length;
        }
        
        return new double[]
        {
              new Mean().evaluate(scores),
              new StandardDeviation().evaluate(scores)
        };
    }
    
    public static double [] calculateMeanGeodesicDistance(FeatureMap aMap)
    {    
        Vertex [] allNodes = aMap.getVectors();
        int count = 0;                
        double [] scores = new double[allNodes.length*allNodes.length]; // too big
        for (int i = 0; i < allNodes.length; i++)
        {
            for (int j = i; j < allNodes.length; j++)
            {
                double numHops = numHopsToNode(allNodes[i], allNodes[j]);
                if(numHops != -1.0)
                {
                    scores[count++] = numHops;
                }
            }
        }
        
        return new double[]
        {
                new Mean().evaluate(scores, 0, count),
                new StandardDeviation().evaluate(scores, 0, count)
        };
    }
    
    public static double numHopsToNode(Vertex self, Vertex other)
    {
        if(self == other)
        {
            return 0;
        }
        
        HashSet<Edge> processedEdgeSet = new HashSet<Edge>();
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        queue.add(self);
        int numHops = 0;
        boolean goalState = false;
        
        do
        {
            numHops++;
            LinkedList<Vertex> tmp = new LinkedList<Vertex>(queue);
            queue.clear();
            
            for(Vertex v : tmp)
            {                
                // get all of v's connections
                Edge [] edges = v.getEdges();
                for (int i = 0; i < edges.length; i++)
                {
                    if(!processedEdgeSet.contains(edges[i]))
                    {
                        processedEdgeSet.add(edges[i]);
                        // add the non-self elements
                        if(edges[i].getVertex1() == v)
                        {
                            if(edges[i].getVertex2() == other)
                            {
                                goalState = true;
                                break;
                            }
                            queue.add(edges[i].getVertex2());
                        }
                        else
                        {
                            if(edges[i].getVertex1() == other)
                            {
                                goalState = true;
                                break;
                            }
                            queue.add(edges[i].getVertex1());
                        }
                    }
                }     
                if(goalState)
                {
                    break;
                }
            }
        }
        while(!goalState && !queue.isEmpty());
        
        // check for no connection
        if(queue.isEmpty())
        {
            return -1.0;
        }
        
        return numHops;
    }
}
