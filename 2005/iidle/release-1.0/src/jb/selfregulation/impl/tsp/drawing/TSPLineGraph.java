
package jb.selfregulation.impl.tsp.drawing;

import jb.selfregulation.Lattice;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.tsp.problem.TSPLatticeEvaluator;

import org.jfree.data.xy.XYSeries;

/**
 * Type: TSPLineGraph<br/>
 * Date: 5/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPLineGraph extends LineGraph
{    
    protected final TSPLatticeEvaluator eval;
    protected long count;
    
    
    /**
     * @param aGraphName
     * @param aAxisLabel
     */
    public TSPLineGraph()
    {
        eval = new TSPLatticeEvaluator();
        chart.setTitle("TSP Statistics");
    }
   
    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Stimulation";
    }
    public String getGraphTitle()
    {
        return "TSP";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Min Tour"),  
                prepareTrace("Mean Tour"),
                prepareTrace("Max Tour"),
                
                prepareTrace("Min Int"),  
                prepareTrace("Mean Int"),
                prepareTrace("Max Int"),
                
                prepareTrace("Min NN"),  
                prepareTrace("Mean NN"),
                prepareTrace("Max NN")
        };
    }
   
    protected void updatePlotInternal(Lattice aLattice)
    {        
        // clear old stats
        eval.reset();
        // collect stats
        aLattice.getPerformRoughVisit(eval);
        // complete collection
        eval.finished();
        
        // store stats
        traces[0].add(count, eval.tourMin);
        traces[1].add(count, eval.tourMean);
        traces[2].add(count, eval.tourMax);
        
        traces[3].add(count, eval.crossesMin);
        traces[4].add(count, eval.crossesMean);
        traces[5].add(count, eval.crossesMax);
        
        traces[6].add(count, eval.nnConnectionsMin);
        traces[7].add(count, eval.nnConnectionsMean);
        traces[8].add(count, eval.nnConnectionsMax);
        
        count++;
    }
    
}
