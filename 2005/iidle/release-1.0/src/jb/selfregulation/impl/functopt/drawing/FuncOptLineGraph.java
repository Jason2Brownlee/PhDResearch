
package jb.selfregulation.impl.functopt.drawing;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.functopt.problem.Function;

import org.jfree.data.xy.XYSeries;

/**
 * Type: FuncOptLineGraph<br/>
 * Date: 15/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FuncOptLineGraph extends LineGraph
{    
//    protected Lattice lattice;
    protected FuncOptLatticeEvaluator eval;
    protected Function function;
    
    protected long count;    
    protected double bestPossible;

    /**
     * @param aGraphName
     * @param aAxisLabel
     */
//    public FuncOptLineGraph(Lattice aLattice, Function aFunction)
//    {
//        lattice = aLattice;
//        function = aFunction;        
//        eval = new FuncOptLatticeEvaluator();
//        
//        bestPossible = function.getBestFitness();
//        chart.setTitle("Function Optimisation Statistics");
//    }
    
    
   
    public void setup(SystemState aState)
    {
        super.setup(aState);
//        lattice = aState.lattice;
        function = (Function) aState.problem;        
        eval = new FuncOptLatticeEvaluator();
      
        bestPossible = function.getBestFitness();
        chart.setTitle("Function Optimisation Statistics");
    }

   
    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Evaluation";
    }
    public String getGraphTitle()
    {
        return "FuncOpt";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Min Eval"),  
                prepareTrace("Mean Eval"),
                prepareTrace("Max Eval"),
                prepareTrace("Best Possible"),
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
        traces[0].add(count, eval.evalMin);
        traces[1].add(count, eval.evalMean);
        traces[2].add(count, eval.evalMax);
        traces[3].add(count, bestPossible);
        count++;
    }    
}
