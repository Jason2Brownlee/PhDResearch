
package jb.selfregulation.display.connectivity;

import java.awt.Shape;

import jb.selfregulation.Cell;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;



/**
 * Type: CellVertexSizePainter<br/>
 * Date: 24/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class CellVertexSizePainter extends AbstractVertexShapeFunction 
    implements VertexSizeFunction
{
    
    public CellVertexSizePainter()
    {
        setSizeFunction(this);
    }

    public Shape getShape(Vertex v)
    {
        return factory.getEllipse(v);        
    }   
    
    public int getSize(Vertex v)
    {
        Cell cell = (Cell) v.getUserDatum(ConnectivityGraph.CELL_KEY);
        int size = cell.getTail().getUnits().size();
        return  10 + size;
    }    
}