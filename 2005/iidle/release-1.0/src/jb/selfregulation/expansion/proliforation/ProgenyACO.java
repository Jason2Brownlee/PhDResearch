
package jb.selfregulation.expansion.proliforation;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;



/**
 * Type: ProgenyACO<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class ProgenyACO extends ProgenyStrategy
{
    protected final static double MAX_PROB = 1.0;
    protected final static double MIN_PROB = 0.0;
    protected final static double IMPOSSIBLE = MIN_PROB;
    
    protected Random rand;
    
    protected double historyExponent;
    protected boolean multiply;
    
    
    
    
    public String getBase()
    {
        return super.getBase() + ".aco";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        
        String b = aBase + super.getBase() + ".aco";
        multiply = Boolean.parseBoolean(prop.getProperty(b+".usemultiplication"));
        historyExponent = Double.parseDouble(prop.getProperty(b+".history.exp"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
    }
    
    
    
    protected abstract double [][] prepareHistoryMatrix(LinkedList<Unit> units, StimulationStrategy aStimulationStrategy);
    protected abstract Unit generateChild(double [][] aHistory);
    
    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected,
            StimulationStrategy aStimulationStrategy)
    {
        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        // check if there is anything to do
        if(selected.isEmpty())
        {
            return progeny;
        }
        
        // all are actually selected
        selected = aCell.getTail().getUnits();
        
        // prepare bits
        double [][] history = prepareHistoryMatrix(selected, aStimulationStrategy);
        
        // create progeny
        for (int i = 0; i < totalProgeny; i++)
        {   
            // turn into unit
            Unit unit = generateChild(history);
            // store
            progeny.add(unit);
        }

        return progeny;
    }
    
    protected boolean isAllZero(double [] v)
    {
        for (int i = 0; i < v.length; i++)
        {
            if(v[i] != 0)
            {
                return false;
            }
        }
        
        return true;
    }
    
    protected void normaliseColumn(double [] v)
    {        
        // calculate max for column
        double max = Double.MIN_VALUE;
        for (int i = 0; i < v.length; i++)
        {
            if(v[i] > max)
            {
                max = v[i];
            }
        }        
        
        // normalise the column
        for (int i = 0; i < v.length; i++)
        {
            v[i] /= max;
        }
    }
    
    
    protected double sum(double [] v )
    {
        double sum = 0.0;
        
        for (int i = 0; i < v.length; i++)
        {
            sum += v[i];
        }
        
        return sum;
    }
    

    public double getHistoryExponent()
    {
        return historyExponent;
    }


    public void setHistoryExponent(double historyExponent)
    {
        this.historyExponent = historyExponent;
    }


    public boolean isMultiply()
    {
        return multiply;
    }


    public void setMultiply(boolean multiply)
    {
        this.multiply = multiply;
    }
}
