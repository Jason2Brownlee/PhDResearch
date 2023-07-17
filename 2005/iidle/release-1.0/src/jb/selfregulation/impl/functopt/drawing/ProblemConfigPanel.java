
package jb.selfregulation.impl.functopt.drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.control.SliderPanel;
import jb.selfregulation.impl.functopt.expansion.stimulation.StimulationLocalFunction;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.processes.work.ProcessExpansion;

/**
 * Type: ProblemConfigPanel<br/>
 * Date: 15/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProblemConfigPanel extends JPanel 
    implements LatticeStatusListener, ChangeListener
{
    protected final static int PLOT_RESOLUTION = 64;
    
    protected LinkedList<Function> problems;
    protected LinkedList<InterpolatedFunctionPlot> plotList;
    
    protected SliderPanel jitterUnit;
    protected SliderPanel cycleLengthUnit;
    
    
    
    protected boolean globalMode;
    protected int totalLocalFunctions;
    protected int [] localStimulationIds;
    
    public String getBase()
    {
        return ".control";
    }

   

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        globalMode = Boolean.parseBoolean(prop.getProperty(b + ".global"));
        if(!globalMode)
        {
            totalLocalFunctions = Integer.parseInt(prop.getProperty(b + ".total"));
            localStimulationIds = new int[totalLocalFunctions];
            for (int i = 0; i < localStimulationIds.length; i++)
            {
                localStimulationIds[i] = Integer.parseInt(prop.getProperty(b + "." + i));
            }
        }
    }


    public void setup(SystemState aState)
    {
        plotList = new LinkedList<InterpolatedFunctionPlot>();
        problems = new LinkedList<Function>(); 
        
        // the global problem
        problems.add((Function)aState.problem);
        
        if(!globalMode)
        {
            HashMap<Long, ProcessExpansion> map = ((HashMap<Long, ProcessExpansion>)aState.getUserDatum(SystemState.KEY_PROCESS_STIMULATION));
            
            // get all other problems
            for (int i = 0; i < localStimulationIds.length; i++)
            {
                ProcessExpansion exp = map.get(new Long(localStimulationIds[i]));
                StimulationLocalFunction local = (StimulationLocalFunction) exp.getStimulation();
                problems.add(local.getFunction());
            }
        }
        // add to problem panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS)).add(this);
        // prepare the GUI
        prepareGui();        
    }
    
    
    
    
    
    protected void prepareGui()
    {
        setName("FuncOpt Config");
        
        jitterUnit = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Evaluation Jitter (%)", 0, 100, (int)(problems.getFirst().getJitterPercentage()*100.0));
        cycleLengthUnit = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Dynamic Cycle Length", 0, 2000, (int)problems.getFirst().getCycleLength());
               
        jitterUnit.prepareSlider(this, 1, 10);
        cycleLengthUnit.prepareSlider(this, 20, 100);
        
        if(!problems.getFirst().supportsJitter())
        {
            jitterUnit.slider.setEnabled(false);
        }
        if(!problems.getFirst().supportsDynamic())
        {
            cycleLengthUnit.slider.setEnabled(false);
        }
        
        // add plots side by side
        JPanel plots = new JPanel(new GridLayout(1, problems.size()));
        for(Function f : problems)
        {
            // prepare plot
            InterpolatedFunctionPlot plot = new InterpolatedFunctionPlot(f, PLOT_RESOLUTION);
            plot.setMinimumSize(new Dimension(250,250));
            plot.setPreferredSize(plot.getMinimumSize());
            plotList.add(plot);        
            // prepare plot panel
            JPanel p = new JPanel();      
            p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem"));
            // add the thing
            p.add(plot);
            plots.add(p);
        }   
        
        // add sliders one above the other
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem Configuration"));
        panel.add(jitterUnit);
        panel.add(cycleLengthUnit);
        
        setLayout(new BorderLayout());
        add(plots, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }


    public void latticeChangedEvent(Lattice aLattice)
    {   
        // redraw all plots
        for(InterpolatedFunctionPlot p : plotList)
        {
            // redraw image only on a change
            p.latticeChangedEvent(aLattice);
        }
    }


    public void stateChanged(ChangeEvent e)
    {
        Object src = e.getSource();
        
        if(src == jitterUnit.slider)
        {
            double value = (jitterUnit.slider.getValue() / 100.0);            
            setJitter(value);
            jitterUnit.refreshCurrentValue(value);
//            jitterAdjusted = true;
        }
        else if(src == cycleLengthUnit.slider)
        {
            long value = cycleLengthUnit.slider.getValue();
            setCycleLength(value);
            cycleLengthUnit.refreshCurrentValue(value);
        }     
        
        // redraw everyone that this change effects
        latticeChangedEvent(null);
    }
    
    
    public void setJitter(double d)
    {
        for(Function f : problems)
        {
            f.setJitterPercentage(d);
        }
    }
    
    public void setCycleLength(long d)
    {
        for(Function f : problems)
        {
            f.setCycleLength(d);
        }
    }



}
