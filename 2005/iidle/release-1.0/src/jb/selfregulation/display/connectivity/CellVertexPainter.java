
package jb.selfregulation.display.connectivity;


import java.awt.Color;
import java.awt.Paint;

import jb.selfregulation.Cell;
import jb.selfregulation.display.DrawingCommon;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;



public class CellVertexPainter implements VertexPaintFunction
{    
    
    public CellVertexPainter()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    public Paint getFillPaint(Vertex v)
    {
        Cell cell = (Cell) v.getUserDatum(ConnectivityGraph.CELL_KEY);
        Color c = DrawingCommon.determinePartitionColor(0.0f, cell);
        return c;
    }

    public Paint getDrawPaint(Vertex arg0)
    {
        return Color.BLACK;
    }

}
