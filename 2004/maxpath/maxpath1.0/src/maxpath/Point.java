package maxpath;

/**
 * Type: Point File: ExhaustiveSearch.java Date: 10/11/2004
 * 
 * Description:
 * 
 * @author Jason Brownlee
 * 
 */
public class Point implements Comparable<Point>
{
	protected static boolean ORDER_POTENTIAL_DESCENDING = true;
	
	
    public final byte[] coord;

    public final byte score;

    public final int customHash;
    
    public int id;    

    public Point[] neighbours;
    
    public double potential;
    

    public Point(byte[] aCoord, byte aScore)
    {
        coord = aCoord;
        score = aScore;
        customHash = calculateCustomHash(aCoord);
    }
    
    public static void setPotentialOrderingDecending(boolean aBool)
    {
    	ORDER_POTENTIAL_DESCENDING = aBool;
    }
    
    public void calculatePotential()
    {
        double sum = 0.0;
        
        for(Point p : neighbours)
        {
            sum += p.score;
        }            
        
        potential = score + sum;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(1024);

        buffer.append("[");
        for (int j = 0; j < coord.length; j++)
        {
            buffer.append(coord[j]);
            if (j < coord.length - 1)
            {
                buffer.append(", ");
            }
        }
        buffer.append("]");

        return buffer.toString();
    }

    public int hashCode()
    {
        return customHash;
    }

    public boolean equals(Object o)
    {
        return (customHash == ((Point) o).customHash);
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * 
     * Sorts decending instead of ascending
     */
    public int compareTo(Point p)
    {
        // this is better
        if (score > p.score)
        {
            return -1;
        }
        // other is better
        else if (score < p.score)
        {
            return +1;
        }
        
        // scores are equal
        // - decending is better when close to the wire, 
        // other wise ascending will be faster when a long way out
        if(ORDER_POTENTIAL_DESCENDING)
        {
	        // equal - this is better         
	        if(potential > p.potential)
	        {
	            return -1;
	        }
	        // equal - other is better
	        else if(potential < p.potential)
	        {
	            return +1;
	        }
        }
        else
        {
	        // equal - this is worse         
	        if(potential < p.potential)
	        {
	            return -1;
	        }
	        // equal - other is worse
	        else if(potential > p.potential)
	        {
	            return +1;
	        }
        }
        
        // totally equal
        return 0;
    }
    
    
    public final static int calculateCustomHash(byte[] aCoord)
    {
        int h = 1;

        for (int i = 0; i < aCoord.length; i++)
        {
            h = (104729 * h) + aCoord[i];
        }

        return h;
    }
}
