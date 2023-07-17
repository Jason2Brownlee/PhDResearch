
package swsom.algorithm;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Type: Vector<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class ExemplarVector implements Vertex
{
    protected final double [] data;    
    protected final int id;
    protected double [] coord;
    protected LinkedList<VectorConnection> connections;
    
    protected boolean positive;    
    protected ExemplarVector [] satellites;
    
    // array order: left, right, down, up
    protected double [] squareRadii;
    protected boolean hasSquare;
    
    
    public ExemplarVector(ExemplarVector aCopy)
    {
        data = new double[aCopy.data.length];
        System.arraycopy(aCopy.data,0,data,0,data.length);
        id = aCopy.id;
        prepare();
    }
    
    public ExemplarVector(int aId, int size)    
    {
        this(aId, new double[size]);
    }
    
    public ExemplarVector(int aId, double [] aData)
    {
        data = aData;
        id = aId;
        prepare();
    }
    
    protected void prepare()
    {
        connections = new LinkedList<VectorConnection>();
        positive = true;
        hasSquare = false;
        squareRadii = new double[data.length*2];
    }
    
    public void setPositive()
    {
        positive = true;
    }    
    public void setNegative()
    {
        positive = false;
    }    
    public boolean isPositive()
    {
        return positive;
    }
    public boolean isNegative()
    {
        return !positive;
    }
    
    
    public boolean isSatellite()
    {
        return (satellites != null);
    }
    
    
    public boolean isSquare()
    {
        return hasSquare;
    }
    public void setSquare(boolean b)
    {
        hasSquare = b;
    }
    
    public boolean hasSquareRadius()
    {
        for (int i = 0; i < squareRadii.length; i++)
        {
            if(squareRadii[i] != 0.0)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Rectangle getSquareRectangle()
    {        
        int width = (int) Math.round(squareRadii[0] + squareRadii[1]);
        int height = (int) Math.round(squareRadii[2] + squareRadii[3]);        
        Rectangle r = new Rectangle(
                (int) Math.round(data[0]-squareRadii[0]), 
                (int) Math.round(data[1]-squareRadii[2]),
                width,
                height);
        return r;
    }
    
    
    
    
    /**
     * @return Returns the squareRadii.
     */
    public double[] getSquareRadii()
    {
        return squareRadii;
    }
    /**
     * @return Returns the radius.
     */
//    public double getRadius()
//    {
//        return radius;
//    }
//    /**
//     * @param radius The radius to set.
//     */
//    public void setRadius(double radius)
//    {
//        this.radius = radius;
//    }
    
    
    /**
     * (non-Javadoc)
     * @see swsom.algorithm.Vertex#getEdges()
     */
    public Edge[] getEdges()
    {
        return connections.toArray(new VectorConnection[connections.size()]);
    }
    
    
    public void addConnection(VectorConnection edge)    
    {
        connections.add(edge);
    }
    
    public boolean containsEdge(VectorConnection edge)
    {
        for(VectorConnection v : connections)            
        {
            if(v.equals(edge))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public void deleteEdge(VectorConnection edge)
    {        
        for (Iterator<VectorConnection> iter = connections.iterator(); iter.hasNext();)
        {
            VectorConnection v = iter.next();
            if(v.equals(edge))
            {
                iter.remove();
                return;
            }
        }
        
        throw new RuntimeException("Unable to remove edge, does not exist.");
    }
    
    
    
    /**
     * 
     * @return Returns the data.
     */
    public double[] getData()
    {
        return data;
    }
    
    
    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return id;
    }
    
    

    
    
    /**
     * @return Returns the coord.
     */
    public double[] getCoord()
    {
        return coord;
    }
    /**
     * @param coord The coord to set.
     */
    public void setCoord(double[] coord)
    {
        this.coord = coord;
    }
    
    
    
    /**
     * @return Returns the satellites.
     */
    public ExemplarVector[] getSatellites()
    {
        return satellites;
    }
    /**
     * @param satellites The satellites to set.
     */
    public void setSatellites(ExemplarVector[] satellites)
    {
        this.satellites = satellites;
    }
}
