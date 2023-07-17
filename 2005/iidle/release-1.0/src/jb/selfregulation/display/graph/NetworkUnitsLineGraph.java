package jb.selfregulation.display.graph;

import java.util.LinkedList;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessWork;
import jb.selfregulation.processes.work.ProcessPortalInbound;
import jb.selfregulation.processes.work.ProcessPortalOutbound;

import org.jfree.data.xy.XYSeries;


/**
 * Type: NetworkUnitsLineGraph<br/>
 * Date: 2/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class NetworkUnitsLineGraph extends LineGraph
{
    protected long count;
    
    protected ProcessPortalInbound inboundProcess;
    protected ProcessPortalOutbound outboundProcess;
    
    protected long lastInboundStamp;
    protected long lastOutboundStamp;
    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup
        inboundProcess = locateFirstInboundProcess(aState.processes);
        outboundProcess = locateFirstOutboundProcess(aState.processes);
        chart.setTitle("Network Units Inbound/Outbound");
    }
    
    
    protected ProcessPortalInbound locateFirstInboundProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessPortalInbound)
                {
                    return (ProcessPortalInbound) w;
                }
            }
        }
        
        return null;
    }
    protected ProcessPortalOutbound locateFirstOutboundProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessPortalOutbound)
                {
                    return (ProcessPortalOutbound) w;
                }
            }
        }
        
        return null;
    }
    
    
    
    
    

    @Override
    public String getGraphTitle()
    {
        return "Network";
    }

    @Override
    protected String getXAxisLabel()
    {
        return "Time";
    }

    @Override
    protected String getYAxisLabel()
    {
        return "Total Units";
    }

    @Override
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Inbound"),
                prepareTrace("Outbound"),
        };
    }

    @Override
    protected void updatePlotInternal(Lattice lattice)
    {
        boolean wasChange = false;
        
        if(inboundProcess != null)
        {
            if(lastInboundStamp != inboundProcess.getLastRunStamp())
            {
                lastInboundStamp = inboundProcess.getLastRunStamp();
                traces[0].add(count, inboundProcess.getTotalUnitsReceived());
                wasChange = true;
            }
        }
        
        if(outboundProcess != null)
        {
            if(lastOutboundStamp != outboundProcess.getLastRunStamp())
            {
                lastOutboundStamp = outboundProcess.getLastRunStamp();
                traces[1].add(count, outboundProcess.getTotalUnitsSent());
                wasChange = true;
            }
        }
        
        if(wasChange)
        {
            count++;
        }
    }
    
}