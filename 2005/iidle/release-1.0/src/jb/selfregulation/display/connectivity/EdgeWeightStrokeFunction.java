
package jb.selfregulation.display.connectivity;


import java.awt.BasicStroke;
import java.awt.Stroke;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;

public class EdgeWeightStrokeFunction implements EdgeStrokeFunction
{
    public EdgeWeightStrokeFunction()
    {
        
    }


    public Stroke getStroke(Edge arg0)
    {
        BasicStroke stroke = new BasicStroke(1.0f);
        return stroke;
    }

}
