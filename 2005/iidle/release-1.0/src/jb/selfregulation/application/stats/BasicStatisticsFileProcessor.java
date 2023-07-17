
package jb.selfregulation.application.stats;

import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

import jb.selfregulation.FileUtil;


/**
 * Type: BasicStatisticsFileProcessor<br/>
 * Date: 30/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BasicStatisticsFileProcessor
{
    /**
     * @param args
     */
//    public static void main(String[] args)
//    {
//        try
//        {
//            
//            
//            StringBuffer b = new StringBuffer();
//            b.append("Measure, Mean, Stev, Min, Max\n");
//            
//            b.append("Score, " + getDataSummary(scores)); b.append("\n");            
//            b.append("Evaluations, " + getDataSummary(evals)); b.append("\n");
//            b.append("Units, " + getDataSummary(units)); b.append("\n");
//            
//            System.out.println(b.toString());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    
    
    
    public static double [][] getStatistics(String aFilename)
        throws Exception
    {
        String data = FileUtil.loadFile(aFilename);
        String [] lines = data.trim().split("\n");
        
        double [] scores = new double[lines.length-1];
        double [] evals = new double[lines.length-1];
        double [] units = new double[lines.length-1];
        
        for (int i = 1; i < lines.length; i++)
        {
            String [] parts = lines[i].trim().split(",");
            scores[i-1] = Double.parseDouble(parts[0]);
            evals[i-1] = Double.parseDouble(parts[1]);
            units[i-1] = Double.parseDouble(parts[2]);
        }
        
        return new double [][]
        {
                getDataSummary(scores),
                getDataSummary(evals),
                getDataSummary(units)
        };
    }

    public static double [] getDataSummary(double [] d)
    {
        return new double[]
        {
                new Mean().evaluate(d),
                new StandardDeviation().evaluate(d),
                new Min().evaluate(d),
                new Max().evaluate(d)
        };
    }
    
    public static String getDataSummaryString(double [] d)
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
