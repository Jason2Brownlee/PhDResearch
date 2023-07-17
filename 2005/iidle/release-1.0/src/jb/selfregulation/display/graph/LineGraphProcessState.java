
package jb.selfregulation.display.graph;

import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessWork;
import jb.selfregulation.processes.work.ProcessDecay;
import jb.selfregulation.processes.work.ProcessMovement;
import jb.selfregulation.processes.work.ProcessUnitInsertion;

import org.jfree.data.xy.XYSeries;

/**
 * Type: LineGraphStimulation<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LineGraphProcessState extends LineGraph
{    
    protected ProcessDecay decayProcess;
    protected ProcessMovement movementProcess;
    protected ProcessUnitInsertion insertProcess;
    
    protected long count;
    

    public void setup(SystemState aState)
    {
        super.setup(aState);
        // setup
        decayProcess = locateFirstDecayProcess(aState.processes);
        movementProcess = locateFirstMovementProcess(aState.processes);
        insertProcess = locateFirstInsertProcess(aState.processes);
    }
    
    
    protected ProcessUnitInsertion locateFirstInsertProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessUnitInsertion)
                {
                    return (ProcessUnitInsertion) w;
                }
            }
        }
        
        return null;
    }
    protected ProcessMovement locateFirstMovementProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessMovement)
                {
                    return (ProcessMovement) w;
                }
            }
        }
        
        return null;
    }
    protected ProcessDecay locateFirstDecayProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessDecay)
                {
                    return (ProcessDecay) w;
                }
            }
        }
        
        return null;
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
        return "Process States";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[] 
        {
                prepareTrace("Removed (decay)"),
                prepareTrace("Moved"),
                prepareTrace("Inserted")
        };
    }
   
    protected void updatePlotInternal(Lattice lattice)
    {               
        // store
        addPoint(count, (decayProcess!=null)?decayProcess.getLastDecayTotalRemoved():0, traces[0]);
        addPoint(count, (movementProcess!=null)?movementProcess.getTotalMovementsLastRun():0, traces[1]);
        addPoint(count, (insertProcess!=null)?insertProcess.getInsertionsLastRun():0, traces[2]);
        count++;
    }
}
