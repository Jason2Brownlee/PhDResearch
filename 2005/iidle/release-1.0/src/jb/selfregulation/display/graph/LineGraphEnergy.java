
package jb.selfregulation.display.graph;

import jb.selfregulation.Lattice;

import org.jfree.data.xy.XYSeries;

/**
 * Type: LineGraphEnergy<br/>
 * Date: 22/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LineGraphEnergy extends LineGraph
{
    protected long count;

    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Energy";
    }
    public String getGraphTitle()
    {
        return "System Energy";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[] 
        {
                prepareTrace("Energy")
        };      
    }
   
    protected void updatePlotInternal(Lattice lattice)
    {
        addPoint(count++, lattice.getRoughSystemEnergy(), traces[0]);
    }
}
