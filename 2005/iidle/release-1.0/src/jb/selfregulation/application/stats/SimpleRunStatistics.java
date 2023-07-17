
package jb.selfregulation.application.stats;

import java.util.Properties;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;

import org.apache.commons.lang.math.Range;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

/**
 * Type: RunStatistics<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimpleRunStatistics implements IRunStatistics
{       
    protected double [] bestScores;
    protected double [] evaluations;
    protected double [] totalUnits;
    
    protected int totalRuns;
    protected int internalRunNumber;
   
    
   
    
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
        bestScores = new double[totalRuns];
        evaluations = new double[totalRuns];
        totalUnits = new double[totalRuns];
    }
    
    public void addRunStatistic(
            double bestScore, 
            long numEvaluations,
            long numUnits)
    {        
        bestScores[internalRunNumber] = bestScore;
        evaluations[internalRunNumber] = numEvaluations;
        totalUnits[internalRunNumber] = numUnits;
        internalRunNumber++;
    }
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append("Best Score, Total Evaluations, Number of Units\n");
        for (int i = 0; i < bestScores.length; i++)
        {
            b.append(bestScores[i]);
            b.append(",");
            b.append(evaluations[i]);
            b.append(",");
            b.append(totalUnits[i]);
            b.append("\n");
        }
        return b.toString();
    }
    
    public String getSummary()
    {
        StringBuffer b = new StringBuffer();
        
        b.append(getDataSummary(bestScores));
        b.append(",");
        b.append(getDataSummary(evaluations));
        b.append(",");
        b.append(getDataSummary(totalUnits));
        
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
    
    public String getSummaryTitle()
    {
        return "Best Scores Mean, Standard Deviation, Min, Max, " +
               "Total Evaluations Mean, Standard Deviation, Min, Max, " + 
               "Num Units Mean, Standard Deviation, Min, Max";
    }

    public int getTotalRuns()
    {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns)
    {
        this.totalRuns = totalRuns;
    }   
    
    
}
