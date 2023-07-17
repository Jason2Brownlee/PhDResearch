
package jb.selfregulation;

import java.io.Serializable;
import java.util.HashSet;

import jb.selfregulation.expansion.selection.UnitSelector;
import jb.selfregulation.expansion.stimulation.UnitStimulator;

/**
 * Type: Unit<br/>
 * Date: 19/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Unit implements Serializable
{
    public final static double MAX_ENERGY = 1.0;
    public final static double MIN_ENERGY = 0.0;
    
    // network
    protected boolean isVisitor;
    
    protected double energy;
    protected boolean evaluated;
    protected boolean selected;
    
    protected final HashSet<Long> evaluatedList;
    
    
    public Unit()
    {
        evaluatedList = new HashSet<Long>();
    }
    
    public void stimulate(UnitStimulator aUnitStimulator)
    {
        // stimulate
        Long s = aUnitStimulator.stimulate(this);
        // store an indicator of who evaluated
        evaluatedList.add(s);
        // mark as evaluated
        evaluated = true;
    }    
    public void select(UnitSelector aUnitSelector)
    {
        // select
        aUnitSelector.select(this);
        // mark as selected
        selected = true;
    }    
    
    
    public boolean decay(double aDecayAmount)
    {
        energy -= aDecayAmount;
        return isDead();
    }    
    public boolean isDead()
    {
        return energy <= MIN_ENERGY;
    }  
    public double getEnergy()
    {
        return energy;
    }
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }      
    
    public boolean isVisitor()
    {
        return isVisitor;
    }
    public void setVisitor(boolean isVisitor)
    {
        this.isVisitor = isVisitor;
    }

    public boolean isEvaluated()
    {
        return evaluated;
    }

    public boolean isSelected()
    {
        return selected;
    }
    
    
}
