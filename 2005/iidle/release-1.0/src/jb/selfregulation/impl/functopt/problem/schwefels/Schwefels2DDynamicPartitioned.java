
package jb.selfregulation.impl.functopt.problem.schwefels;

import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.impl.functopt.drawing.InterpolatedFunctionPlot;


/**
 * Type: Schwefels2DDynamicPartitioned<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Schwefels2DDynamicPartitioned extends Schwefels2DDynamic
{
    protected boolean evalX;
    protected boolean evalY;
       
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + getBase();
        evalX = Boolean.parseBoolean(prop.getProperty(b + ".evalx"));
        evalY = Boolean.parseBoolean(prop.getProperty(b + ".evaly"));
    }
    
    
    public double evaluate(double[] v)
    {        
        totalEvaluations++;
        // normal evaluation
        double x = evaluate(0, v[0]);
        double y = -evaluate(1, v[1]);
        
        // calculate dynamic value
        double nx = (alpha * x + (1.0-alpha) * y);
        double ny = (alpha * y + (1.0-alpha) * x);
        
        double sum = 0.0;
        if(evalX)
        {            
            sum += nx;
        }
        if(evalY)
        {
            sum += ny;
        }
        return sum;
    }
}
