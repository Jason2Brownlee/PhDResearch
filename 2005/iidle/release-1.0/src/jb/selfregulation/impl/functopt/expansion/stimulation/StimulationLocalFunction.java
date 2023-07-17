
package jb.selfregulation.impl.functopt.expansion.stimulation;

import java.util.Properties;

import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.functopt.problem.Function;

public class StimulationLocalFunction extends StimulationFunctionEvaluation
{
    
    public String getBase()
    {
        return super.getBase() + ".local";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        // normal thing
        super.loadConfig(aBase, prop);
        
        String b = aBase + getBase();        
        try
        {
            function = (Function) Class.forName(prop.getProperty(b + ".function.classname")).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to prepare local function. ", e);
        }
        // load the function
        function.loadConfig(b, prop);        
    }
    
    public void setup(SystemState aState)
    {
        // setup the function
        function.setup(aState);
        // does not call parent
        isMinimise = getIsMinimisation();   
    } 
}
