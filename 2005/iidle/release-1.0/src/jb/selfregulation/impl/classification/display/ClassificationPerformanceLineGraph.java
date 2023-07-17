
package jb.selfregulation.impl.classification.display;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.graph.LineGraph;
import jb.selfregulation.impl.classification.expansion.progeny.LVQProgenyStrategy;
import jb.selfregulation.processes.work.ProcessExpansion;

import org.jfree.data.xy.XYSeries;

/**
 * Type: ClassificationPerformanceLineGraph<br/>
 * Date: 3/10/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ClassificationPerformanceLineGraph extends LineGraph
{    
//    protected final TSPTourEvaluator eval;
//    protected double bestScore;
    protected long count;
    
    
    protected LVQProgenyStrategy lvqStrategy;
    protected long stimulationId;
    protected ProcessExpansion feedbackProcess;  
    
    
    public ClassificationPerformanceLineGraph()
    {
//        eval = new TSPTourEvaluator();
        chart.setTitle("Classification Performance");        
    }
    
    
    public String getBase()
    {
        return super.getBase() + ".classification";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);        
        String b = aBase + super.getBase() + ".classification";        
        stimulationId = Long.parseLong(prop.getProperty(b + ".id"));
    }
   
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        
        feedbackProcess = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION)).get(stimulationId);
        if(feedbackProcess == null)
        {
            logger.severe("Unable to locate expansion process with id " + stimulationId);
        }
        // get the proliforation strategy
        lvqStrategy = (LVQProgenyStrategy) feedbackProcess.getProgeny();
         
        // reset the name
        String name = lvqStrategy.getClass().getName();
        name = name.substring(name.lastIndexOf('.')+1);
        chart.setTitle("Performance - " + name);
    }
    
    
    
    protected String getXAxisLabel()
    {
        return "Time";
    }
    protected String getYAxisLabel()
    {
        return "Num Patterns";
    }
    public String getGraphTitle()
    {
        return "Classification";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Total Correct"),  
                prepareTrace("Total Incorrect"),
                prepareTrace("Total Exposures"),
        };
    }
    
    
    public void latticeChangedEvent(final Lattice aLattice)
    {
        // capture data now, graph later
        synchronized(queue)
        {   
            queue.add(new Datum(lvqStrategy.getCorrectCount(), lvqStrategy.getIncorrectCount(), count++));        
        }
        lvqStrategy.clearAccuracyCounts();
        super.latticeChangedEvent(aLattice);
    }
    
   
    protected class Datum
    {
        protected long correct;
        protected long incorrect;
        protected long count;
        
        public Datum(long corr, long incorr, long cnt)
        {
            correct = corr;
            incorrect = incorr;
            count = cnt;
        }
    }
    
    protected LinkedList<Datum> queue = new LinkedList<Datum>();
    
    @Override
    protected void updatePlotInternal(Lattice aLattice)
    {                       
//        long correct = lvqStrategy.getCorrectCount();
//        long incorrect = lvqStrategy.getIncorrectCount();        
//        long total = correct + incorrect;
//        // clear counts
//        lvqStrategy.clearAccuracyCounts();
        
        Datum d = null;
        synchronized(queue)
        {
            d = queue.removeFirst();
        }
        
        // store stats
        traces[0].add(d.count, d.correct);
        traces[1].add(d.count, d.incorrect);
        traces[2].add(d.count, d.correct + d.incorrect);
        
//        count++;
    }
    
}
