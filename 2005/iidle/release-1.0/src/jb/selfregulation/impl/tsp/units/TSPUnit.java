
package jb.selfregulation.impl.tsp.units;

import jb.selfregulation.Unit;

/**
 * Type: TSPUnit<br/>
 * Date: 7/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPUnit extends Unit
{
    public final static double STIMULATION_STEP = 1.0;    
    public final static double UPPER_LIMIT = +10;
    public final static double LOWER_LIMIT = -10;
    
    protected int [] data;
    
    protected double userStimulation;
    protected double tourLength;
    protected int totalCrosses;
    protected int totalNNConnections;
    
    protected boolean hasTourLength;
    protected boolean hasTotalCrosses;
    protected boolean hasNNConnections;
    protected boolean hasUserStimulation;

    public TSPUnit(int [] aData)
    {
        data = aData;
    }
    
    public String toString()
    {
        return "Permutation ["+Math.round(tourLength)+"]";
    }
    
    
    public void decreaseUserStimulation()
    {
        if(userStimulation > LOWER_LIMIT)
        {
            userStimulation -= STIMULATION_STEP;
            
            if(userStimulation == 0)
            {
                userStimulation  -= STIMULATION_STEP;
            }
        }
        hasUserStimulation = true;
    }
    public void increaseUserStimulation()
    {
        if(userStimulation < UPPER_LIMIT)
        {
            userStimulation += STIMULATION_STEP;
            
            if(userStimulation == 0)
            {
                userStimulation  += STIMULATION_STEP;
            }
        }        
        hasUserStimulation = true;
    }
    
    

    public int[] getData()
    {
        return data;
    }

    public void setData(int[] data)
    {
        this.data = data;
    }

    public boolean isHasTotalCrosses()
    {
        return hasTotalCrosses;
    }

    public void setHasTotalCrosses(boolean hasTotalCrosses)
    {
        this.hasTotalCrosses = hasTotalCrosses;
    }

    public boolean isHasTourLength()
    {
        return hasTourLength;
    }

    public void setHasTourLength(boolean hasTourLength)
    {
        this.hasTourLength = hasTourLength;
    }

    public int getTotalCrosses()
    {
        return totalCrosses;
    }

    public void setTotalCrosses(int totalCrosses)
    {
        this.totalCrosses = totalCrosses;
    }

    public double getTourLength()
    {
        return tourLength;
    }

    public void setTourLength(double tourLength)
    {
        this.tourLength = tourLength;
    }

    public boolean isHasNNConnections()
    {
        return hasNNConnections;
    }

    public void setHasNNConnections(boolean hasNNConnections)
    {
        this.hasNNConnections = hasNNConnections;
    }

    public int getTotalNNConnections()
    {
        return totalNNConnections;
    }

    public void setTotalNNConnections(int totalNNConnections)
    {
        this.totalNNConnections = totalNNConnections;
    }


    public boolean isHasUserStimulation()
    {
        return hasUserStimulation;
    }


    public double getUserStimulation()
    {
        return userStimulation;
    }    
    

    
}
