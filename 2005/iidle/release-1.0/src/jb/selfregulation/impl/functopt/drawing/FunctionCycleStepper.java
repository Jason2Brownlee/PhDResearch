
package jb.selfregulation.impl.functopt.drawing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.expansion.stimulation.StimulationLocalFunction;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.processes.work.ProcessExpansion;

/**
 * Type: FunctionCycleStepper<br/>
 * Date: 15/07/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FunctionCycleStepper implements LatticeStatusListener
{
    protected LinkedList<Function> functions;
    
    protected boolean globalMode;
    protected int totalLocalFunctions;
    protected int [] localStimulationIds;
    
    public FunctionCycleStepper()
    {}

    public void latticeChangedEvent(Lattice aLattice)
    {
        for(Function f : functions)
        {
            // only does one thing
            f.updateCyclePosition();
        }
    }

    public String getBase()
    {        
        return ".stepper";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        functions = new LinkedList<Function>();
        
        String b = aBase + getBase();
        globalMode = Boolean.parseBoolean(prop.getProperty(b + ".global"));
        if(!globalMode)
        {
            totalLocalFunctions = Integer.parseInt(prop.getProperty(b + ".total"));
            localStimulationIds = new int[totalLocalFunctions];
            for (int i = 0; i < localStimulationIds.length; i++)
            {
                localStimulationIds[i] = Integer.parseInt(prop.getProperty(b + "." + i));
            }
        }
    }

    public void setup(SystemState aState)
    {
        // the global problem
        functions.add((Function)aState.problem);
        
        if(!globalMode)
        {
            HashMap<Long, ProcessExpansion> map = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION));
            
            // get all other problems
            for (int i = 0; i < localStimulationIds.length; i++)
            {
                ProcessExpansion exp = map.get(new Long(localStimulationIds[i]));
                StimulationLocalFunction local = (StimulationLocalFunction) exp.getStimulation();
                functions.add(local.getFunction());
            }
        }        
    }

}
