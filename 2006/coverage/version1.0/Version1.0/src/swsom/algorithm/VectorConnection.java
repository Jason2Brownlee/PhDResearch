
package swsom.algorithm;

/**
 * Type: VectorConnection<br>
 * Date: 24/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class VectorConnection implements Edge
{
    protected ExemplarVector node1;
    protected ExemplarVector node2;
    
    public VectorConnection(ExemplarVector v1, ExemplarVector v2)
    {
        node1 = v1;
        node2 = v2;
    }
    
    public boolean equals(Object other)
    {
        VectorConnection e = (VectorConnection) other;
        
        if(node1 == e.node1 && node2 == e.node2)
        {
            return true;
        }
        else if(node2 == e.node1 && node1 == e.node2)
        {
            return true;
        }
        return false;
    }
    
    

    /**
     * (non-Javadoc)
     * @see swsom.algorithm.Edge#getVertex1()
     */
    public Vertex getVertex1()
    {
        return node1;
    }

    /**
     * (non-Javadoc)
     * @see swsom.algorithm.Edge#getVertex2()
     */
    public Vertex getVertex2()
    {
        return node2;
    }

}
