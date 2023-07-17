
package jb.selfregulation.processes;

import java.util.LinkedList;

import jb.selfregulation.processes.work.ProcessDecay;
import jb.selfregulation.processes.work.ProcessExpansion;
import jb.selfregulation.processes.work.ProcessMovement;
import jb.selfregulation.processes.work.ProcessPortalInbound;
import jb.selfregulation.processes.work.ProcessPortalOutbound;
import jb.selfregulation.processes.work.ProcessUnitInsertion;



/**
 * Type: Utils<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessUtils
{
    ///
    // consistant naming convetion for the network process
    //
    public final static String NETWORK_PROCESS_NAME = "Network";
    public final static String USER_STIMULATION_PROCESS_NAME = "UserStimulation";
    
    
    
    
    /**
     * Locate all expansion processes
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessExpansion> locateAllExpansionProcess(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessExpansion> list = new LinkedList<ProcessExpansion>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessExpansion)
                {
                    list.add((ProcessExpansion) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Locate all expansion processes EXCEPT the user feedback process
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessExpansion> locateAllExpansionProcessExceputUserFeedback(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessExpansion> list = new LinkedList<ProcessExpansion>();
        
        for(ParallelProcesses p : processes)
        {
            if(p.getName().equalsIgnoreCase(USER_STIMULATION_PROCESS_NAME))
            {
                continue;
            }
            
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessExpansion)
                {
                    list.add((ProcessExpansion) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Get all insertion processes
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessUnitInsertion> locateAllInsertProcess(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessUnitInsertion> list = new LinkedList<ProcessUnitInsertion>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessUnitInsertion)
                {
                    list.add((ProcessUnitInsertion) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Locate all movement processes
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessMovement> locateAllMovementProcess(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessMovement> list = new LinkedList<ProcessMovement>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessMovement)
                {
                    list.add((ProcessMovement) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Locate all decay processes
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessDecay> locateAllDecayProcess(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessDecay> list = new LinkedList<ProcessDecay>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessDecay)
                {
                    list.add((ProcessDecay) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Locate all inbound network process
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessPortalInbound> locateAllPortalInbound(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessPortalInbound> list = new LinkedList<ProcessPortalInbound>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessPortalInbound)
                {
                    list.add((ProcessPortalInbound) w);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Locate all outbound network processes
     * 
     * @param processes
     * @return
     */
    public final static LinkedList<ProcessPortalOutbound> locateAllPortalOutbound(LinkedList<ParallelProcesses> processes)
    {
        LinkedList<ProcessPortalOutbound> list = new LinkedList<ProcessPortalOutbound>();
        
        for(ParallelProcesses p : processes)
        {
            for(ProcessWork w : p.getWork())
            {
                if(w instanceof ProcessPortalOutbound)
                {
                    list.add((ProcessPortalOutbound) w);
                }
            }
        }
        
        return list;
    }
    
    
    
    /**
     * Locates the network process - assumes a consistant naming convetion
     * 
     * @param processes
     * @return
     */
    public final static ParallelProcesses locateNetworkProcess(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            if(p.getName().equalsIgnoreCase(NETWORK_PROCESS_NAME))
            {
                return p;
            }
        }
        
        return null;
    }
    
    
    public final static void pauseAll(LinkedList<ParallelProcesses> processes)
    {
        if(processes.getFirst().isPaused())
        {
            for(ParallelProcesses p : processes)
            {
                p.setPaused(false);
            }
        }
        else 
        {
            for(ParallelProcesses p : processes)
            {
                p.setPaused(true);
            }
        }
    }
    
    public final static void stopAll(LinkedList<ParallelProcesses> processes)
    {
        for(ParallelProcesses p : processes)
        {
            p.stopAndWait();
        }
    }    
    
    public final static void startAll(LinkedList<ParallelProcesses> processes)
    {   
        for(ParallelProcesses p : processes)
        {
            p.start();
        }
    }

}
