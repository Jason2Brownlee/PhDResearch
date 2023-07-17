
package jb.selfregulation.impl.dummy.display;

import java.util.HashMap;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.processes.work.ProcessExpansion;

import org.jfree.data.xy.XYSeries;

/**
 * Type: ProgenyPerProliforationStrategyGraph<br/>
 * Date: 25/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyPerProliforationStrategyGraph extends LineGraph
{    
    protected long count; 

    ProgenyStrategy [] strategies;
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        chart.setTitle("Progeny Per Strategy");
        
        strategies = new ProgenyStrategy[((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).size()];
        for (int i = 0; i < strategies.length; i++)
        {
            Long id = new Long(i+1);
            strategies[i] = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).get(id).getProgeny();
        }
    }
   
    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Progeny Units";
    }
    public String getGraphTitle()
    {
        return "Progeny Per Strategy";
    }
   
    protected XYSeries[] prepareTraces()
    {
//        int t = strategies.length;
        int t = 3; // TODO HACK HACK HACK
        
        XYSeries [] s = new XYSeries[t];
        for (int i = 0; i < s.length; i++)
        {
            s[i] = prepareTrace("Strategy " + i);
        }
        return s;
    }
   
    protected void updatePlotInternal(Lattice aLattice)
    {
        for (int i = 0; i < strategies.length; i++)
        {
            traces[i].add(count, strategies[i].getTotalProgeny());
        }
        
        count++;
    }    
}
