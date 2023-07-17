
package jb.selfregulation.display.graph;

import jb.selfregulation.Lattice;

import org.jfree.data.xy.XYSeries;

/**
 * Type: LineGraphCurrentPopulationSize<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LineGraphCurrentPopulationSize extends LineGraph
{
    protected long count;

    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Units";
    }
    public String getGraphTitle()
    {
        return "Population Size";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[] 
        {
                prepareTrace("Units"),
                prepareTrace("Evaluated"),
                prepareTrace("Selected"),
                prepareTrace("Cells"),
                prepareTrace("Visitors")
        };      
    }
   
    protected void updatePlotInternal(Lattice lattice)
    {
        // [0]total units, [1]total cells, [2]total system evaled, [3]total system selected, visitors
        int [] scores = lattice.getRoughSystemState();
        // store
        addPoint(count, scores[0], traces[0]); // units
        addPoint(count, scores[2], traces[1]); // system evaluated
        addPoint(count, scores[3], traces[2]); // selected
        addPoint(count, scores[1], traces[3]); // cells
        addPoint(count, scores[4], traces[4]); // visiting units
        count++;
    }
}
