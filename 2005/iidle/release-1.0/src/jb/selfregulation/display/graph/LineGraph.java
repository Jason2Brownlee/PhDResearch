package jb.selfregulation.display.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * Type: LineGraph<br/>
 * Date: 21/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class LineGraph extends JPanel 
    implements ActionListener, LatticeStatusListener, Loggable
{    
    public final static int MAX_POINTS = 100;
    
    public final static long LOCK_MAX_WAIT = 20;
    
    protected final Logger logger;
	protected final JFreeChart chart;
	protected final XYSeriesCollection allTraces;	
	protected final XYSeries [] traces;

	public LineGraph()
	{
        logger = Logger.getLogger(LOG_CONFIG);
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
    
    public Logger getLogger()
    {
        return logger;
    }
    
    
    
    public void latticeChangedEvent(final Lattice aLattice)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                        // do the work in the caller thread
                        updatePlotInternal(aLattice);
                    }
                }
        );
    }
    public String getBase()
    {
        return ".graph";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        
    }
    public void setup(SystemState aState)
    {
        // add to common panels
        ((LinkedList<LineGraph>)aState.getUserDatum(SystemState.KEY_GRAPH_PANELS)).add(this);
    }
    
    
    
    
    
    protected abstract String getYAxisLabel();
    
    public abstract String getGraphTitle();
	
	protected abstract String getXAxisLabel();
	
	protected abstract XYSeries [] prepareTraces();	
		
	protected abstract void updatePlotInternal(Lattice aLattice);
	
    
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
	
    protected XYSeries prepareTrace(String aName)
    {
        XYSeries t = new XYSeries(aName, false, true);
        t.setMaximumItemCount(MAX_POINTS);
        return t;
    }
	
	protected void addPoint(double x, double y, XYSeries aTrace)
	{
	    aTrace.add(x,y);
	}
}