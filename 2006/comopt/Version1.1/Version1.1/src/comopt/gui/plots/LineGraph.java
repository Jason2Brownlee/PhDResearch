package comopt.gui.plots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import comopt.AlgorithmIterationNotification;
import comopt.Problem;
import comopt.Solution;

/**
 * Type: LineGraph<br/>
 * Date: 24/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LineGraph extends JPanel 
    implements ActionListener, AlgorithmIterationNotification
{      
	protected final JFreeChart chart;
	protected final XYSeriesCollection allTraces;	
	protected final XYSeries [] traces;
    protected int totalPoints;
    protected int count;

	public LineGraph()
	{        
	    // prepare traces
	    traces = prepareTraces();	    
		// add traces to graph		
		allTraces = new XYSeriesCollection();
		for (int i = 0; i < traces.length; i++)
        {
		    allTraces.addSeries(traces[i]);
        }

		chart = ChartFactory.createXYLineChart(getGraphTitle(), getXAxisLabel(), getYAxisLabel(), allTraces, PlotOrientation.VERTICAL, true, true, true);
		ChartPanel chartPanel = new ChartPanel(chart);		
		setName(getGraphTitle());
        
        JPanel controlPanel = prepareControlPanel();
        
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEtchedBorder());
		this.add(chartPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
	}
    
    
    public void iterationComplete(Problem p, LinkedList<Solution> currentPop, Solution best)
    {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double sum = 0.0;
        int c = 0;
        double mean = 0;
        
        for(Solution s : currentPop)
        {
            double v = s.getScore();
            if(!Double.isNaN(v))
            {
                c++;
                sum += v;
                if(v > max)
                {
                    max = v;                    
                }
                if(v < min)
                {
                    min = v;    
                }
            }
        }        
        mean = sum / c;
        
        // store stats
        traces[0].add(count, min);
        traces[1].add(count, mean);
        traces[2].add(count, max);
        traces[3].add(count, best.getScore());
        count++;
    }
    
    	
    protected String getXAxisLabel()
    {
        return "Iteration";
    }
    protected String getYAxisLabel()
    {
        return "Cost Function";
    }
    public String getGraphTitle()
    {
        return "Algorithm Results";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Min Eval"),  
                prepareTrace("Mean Eval"),
                prepareTrace("Max Eval"),
                prepareTrace("Best of run")
        };
    }
    
    protected boolean [] tracesEnabled;
    protected JCheckBox [] boxes;
    
    protected JPanel prepareControlPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Control Panel"));
        
        tracesEnabled = new boolean[traces.length];
        boxes = new JCheckBox[traces.length];
        
        for (int i = 0; i < traces.length; i++)
        {
            tracesEnabled[i] = true;
            boxes[i] = new JCheckBox(traces[i].getName(), true);
            boxes[i].addActionListener(this);
            p.add(boxes[i]);
        }
        
        return p;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        boolean reWork = false;        
        
        // locate the traces to add or remove
        for (int i = 0; i < traces.length; i++)
        {
            if(src == boxes[i])
            {
                boolean checked = boxes[i].isSelected();
                
                // check if a change is required
                if(checked != tracesEnabled[i])
                {
                    reWork = true;
                    tracesEnabled[i] = checked;
                }
            }
        }
        
        // rework as required
        if(reWork)
        {
            // remove all series
            allTraces.removeAllSeries();
            // add all selected series
            for (int i = 0; i < tracesEnabled.length; i++)
            {
                if(tracesEnabled[i])
                {
                    allTraces.addSeries(traces[i]);
                }
            }
        }
    }
    
    public void clear(int aTotalPoints)
    {
        totalPoints = aTotalPoints;
        count = 0;
        
        for (int i = 0; i < traces.length; i++)
        {   
            traces[i].clear();
            traces[i].setMaximumItemCount(totalPoints);
        }
    }
	
    protected XYSeries prepareTrace(String aName)
    {
        XYSeries t = new XYSeries(aName, false, true);
        t.setMaximumItemCount(totalPoints);
        return t;
    }
	
	protected void addPoint(double x, double y, XYSeries aTrace)
	{
	    aTrace.add(x,y);
	}
}