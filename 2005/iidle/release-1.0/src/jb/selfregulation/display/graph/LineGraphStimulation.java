
package jb.selfregulation.display.graph;

import java.util.HashMap;
import java.util.Properties;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.work.ProcessExpansion;

import org.jfree.data.xy.XYSeries;

/**
 * Type: LineGraphStimulation<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LineGraphStimulation extends LineGraph
{    
    protected long stimulationId;
    protected ProcessExpansion feedbackProcess;    
    protected long count;

    
    public String getBase()
    {
        return super.getBase() + ".stimulation";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".stimulation";        
        stimulationId = Long.parseLong(prop.getProperty(b + ".id"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup
        feedbackProcess = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).get(stimulationId);
        if(feedbackProcess == null)
        {
            logger.severe("Unable to locate expansion process with id " + stimulationId);
        }
         
        // reset the name
        String name = feedbackProcess.getStimulation().getName();
        chart.setTitle("Stimulation - " + name);
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
        return "Stimulation";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[] 
        {
                prepareTrace("Evaluated"),
                prepareTrace("Selected"),
                prepareTrace("Progeny")                
        };
    }
   
    protected void updatePlotInternal(Lattice lattice)
    {               
        // store
        addPoint(count, feedbackProcess.getTotalEvaluationsLastRun(), traces[0]);
        addPoint(count, feedbackProcess.getTotalSelectionsLastRun(), traces[1]);
        addPoint(count, feedbackProcess.getTotalProgenyLastRun(), traces[2]);
        count++;
    }
}
