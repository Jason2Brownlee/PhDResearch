
package jb.selfregulation.impl.tsp.drawing;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.problem.TSPTourEvaluator;

import org.jfree.data.xy.XYSeries;

/**
 * Type: TSPTourLineGraph<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPTourLineGraph extends LineGraph
{    
    protected final TSPTourEvaluator eval;
    protected double bestScore;
    protected long count;
    
    
    /**
     * @param aGraphName
     * @param aAxisLabel
     */
    public TSPTourLineGraph()
    {
        eval = new TSPTourEvaluator();
        chart.setTitle("TSP Tour Statistics");        
    }
   
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        bestScore = ((TSPProblem)aState.problem).getSolutionTourLength();
        
        // HACK
        eval.setProblem((TSPProblem)aState.problem);
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
                prepareTrace("Best Possible")
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
        traces[3].add(count, bestScore);
        
        count++;
    }
    
}
