
package jb.selfregulation.impl.proteinfolding.drawing;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;

import org.jfree.data.xy.XYSeries;

/**
 * Type: ProteinFoldingQualityLineGraph<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProteinFoldingQualityLineGraph extends LineGraph
{    
    protected ProteinFoldingLatticeEvaluator eval;
    protected long count;           
   
    public void setup(SystemState aState)
    {
        super.setup(aState);
        eval = new ProteinFoldingLatticeEvaluator();
        chart.setTitle("Protein Folding Quality Stats");
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
        return "ProteinFolding";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Min Eval"),  
                prepareTrace("Mean Eval"),
                prepareTrace("Max Eval")
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
        count++;
    }    
}
