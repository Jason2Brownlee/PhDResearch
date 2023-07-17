
package jb.selfregulation.processes;

import java.util.LinkedList;

import jb.selfregulation.Lattice;

/**
 * Type: Process<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BaseProcess
{
    protected final long processId;
    protected final LinkedList<ProcessWork> work;
    
    protected long processIterations;    
    
    
    public BaseProcess(long aProcessId)
    {
        work = new LinkedList<ProcessWork>();
        processId = aProcessId;
    }
    
    public BaseProcess(long aProcessId, ProcessWork aWork)
    {
        this(aProcessId);
        addWork(aWork);
    }
    
    public void stepProcess(Lattice aLattice)
    {
        runProcessStep(aLattice);
        processIterations++;
    }
    
    protected void runProcessStep(Lattice aLattice)
    {
        for(ProcessWork p : work)
        {
            p.executeProcess(aLattice);
        }
    }    
    
    public void addWork(ProcessWork w)
    {
        if(w != null)
        {
            work.add(w);
        }
    }
   
    public LinkedList<ProcessWork> getWork()
    {
        return work;
    }
    
    public void setWork(LinkedList<ProcessWork> w)
    {
        this.work.addAll(w);
    }
    
    public long getProcessIterations()
    {
        return processIterations;
    }
}
