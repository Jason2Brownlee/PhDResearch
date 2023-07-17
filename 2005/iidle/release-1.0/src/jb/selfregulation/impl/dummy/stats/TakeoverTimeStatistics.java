
package jb.selfregulation.impl.dummy.stats;

import java.util.Properties;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

import jb.selfregulation.application.SystemState;
import jb.selfregulation.application.stats.IRunStatistics;


/**
 * Type: TakeoverTimeStatistics<br/>
 * Date: 19/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TakeoverTimeStatistics implements IRunStatistics
{
    protected double [] takeoverTime;
    protected int totalRuns;
    
    protected int takeOverCount;
    protected int failureCount;

    public int getTotalRuns()
    {
        return totalRuns;
    }

    public String getBase()
    {
        return ".runs";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        totalRuns = Integer.parseInt(prop.getProperty(aBase + getBase() + ".total"));
    }

    public void setup(SystemState aState)
    {
        takeoverTime = new double[totalRuns];
    }
    
    public void updateStatistic(int aVal)
    {        
//        System.out.println(aVal);
        
        if(aVal == -1)
        {
            failureCount++;
        }
        else
        {
            takeoverTime[takeOverCount++] = aVal;
        }
        
    }

    public String toString()
    {
        return getSummary();
    }    
    
    public String getSummary()
    {
        StringBuffer b = new StringBuffer();
        
        
        b.append("Iterations, Failures\n");
        
        b.append(new Mean().evaluate(takeoverTime, 0, takeOverCount));
        b.append(", ");
        b.append(failureCount);
        b.append("\n");
        
        b.append(new StandardDeviation().evaluate(takeoverTime));
        b.append(", 0\n");
                      
        return b.toString();
    }
    
    public String getDataSummary(double [] d)
    {
        StringBuffer b = new StringBuffer();
        b.append(new Mean().evaluate(d));
        b.append(",");
        b.append(new StandardDeviation().evaluate(d));
        b.append(",");
        b.append(new Min().evaluate(d));
        b.append(",");
        b.append(new Max().evaluate(d));
        return b.toString();
    }
}
