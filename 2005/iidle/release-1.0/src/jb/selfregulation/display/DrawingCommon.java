
package jb.selfregulation.display;

import java.awt.Color;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;

/**
 * Type: DrawingCommon<br/>
 * Date: 15/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DrawingCommon
{
    
    public static Color determineCommonColor(
            float colour, 
            Unit aUnit,
            boolean showSelectionState)
    {
        Color c = Color.YELLOW;
        
        if(showSelectionState)
        {            
            if(aUnit.isSelected())
            {
                c = new Color(colour, colour, 1.0f); // blue
            }
            else if(aUnit.isEvaluated())
            {
                c = new Color(1.0f, colour, colour); // red
            }
            else
            {
                c = new Color(1.0f, 1.0f, colour); // yellow
            }
        }
        else
        {
            c = new Color(1.0f, colour, colour); // red
        }
        
            
        return c;
    }
    
    public static float determineShade(
            double aVal, 
            double aMin, 
            double aMax,
            boolean invertScale)
    {
        // do some safety things
        if(aVal < aMin)
        {
            aMin = aVal;
        }
        if(aVal > aMax)
        {
            aMax = aVal;
        }        
        
        float backToZero = 0.0f;
        // ensure the range is zero offset
        if(aMin < 0)
        {
            backToZero = (float) Math.abs(aMin);
        }        
        // determine shade
        float colour = ((float)aVal+backToZero) / ((float)aMax+backToZero);          
        // check if the shade should be inverted
        if(invertScale)
        {
            colour = (1.0f - colour);
        }
        
        // check that we are still in bounds, and if we are not - provide useful data.
        if(colour < 0.0f || colour > 1.0f)
        {
            throw new RuntimeException("Invalid shade: colour["+colour+"], backToZero["+backToZero+"], aVal["+aVal+"], min["+aMin+"], max["+aMax+"].");
        }
        
        return colour;
    }

    public static Color determinePartitionColor(float aShade, Cell aCell)
    {
        // get id
        Long lastFeedback = aCell.getLastFeedbackId();
        // prepare a suitable index
        int index = -1;
        if(lastFeedback != null)
        {
            index = (int) (lastFeedback - 1);
        }
        
        Color c = null;
        
        switch(index)
        {
            case 0:
            {
                c = new Color(1.0f, aShade, aShade); // red
                break;
            }
            case 1:
            {
                c = new Color(aShade, aShade, 1.0f); // blue
                break;
            }
            case 2:
            {
                c = new Color(aShade, 1.0f, aShade); // green
                break;
            }
            case 3:
            {
                c = new Color(1.0f, 1.0f, aShade); // yellow
                break;
            }
            default:
            {
                c = new Color(aShade, aShade, aShade); // black
                break;
            }
        }
        
        return c;
    }

}
