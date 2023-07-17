
package jb.selfregulation.impl.dummy.display;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.functopt.problem.Function;

import org.jfree.data.xy.XYSeries;

/**
 * Type: SpecialUnitGraph<br/>
 * Date: 18/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SpecialUnitGraph extends LineGraph
{    
    protected SpecialUnitLatticeEvaluator eval;
    protected long count; 

    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        eval = new SpecialUnitLatticeEvaluator();
        chart.setTitle("Special Unit Statistics");
    }
   
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
        return "Special Unit";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Special"),  
                prepareTrace("Not Special"),
        };
    }
   
    protected void updatePlotInternal(Lattice aLattice)
    {        
        // clear old stats
        eval.reset();
        // collect stats
        aLattice.getPerformRoughVisit(eval);
        // complete collection
        eval.finished();
        
        // store stats
        traces[0].add(count, eval.totalSpecial);
        traces[1].add(count, eval.totalNotSpecial);
        count++;
    }    
}
