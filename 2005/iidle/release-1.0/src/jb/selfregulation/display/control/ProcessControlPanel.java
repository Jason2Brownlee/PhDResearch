
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jb.selfregulation.Lattice;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessUtils;
import jb.selfregulation.processes.work.ProcessDecay;
import jb.selfregulation.processes.work.ProcessExpansion;
import jb.selfregulation.processes.work.ProcessMovement;
import jb.selfregulation.processes.work.ProcessUnitInsertion;

/**
 * Type: SimulationControlPanel<br/>
 * Date: 14/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProcessControlPanel extends JPanel 
    implements Configurable, ChangeListener, ActionListener
{
    public final static String NAME = "Process Configuration";    
    
    protected SliderPanel totalLocalities;
    protected SliderPanel insertionAmplitude;
    protected SliderPanel stimulationAmplitude;
    protected SliderPanel decayIdealEnergy;
    protected SliderPanel decayAmount;
    protected SliderPanel simulationFrequency;
    protected SliderPanel movementAmplitude;
    protected SliderPanel movementProbability;    
    protected MovementControlPanel movementType;
    
    protected LinkedList<ProcessUnitInsertion> insertionProcess;
    protected LinkedList<ProcessDecay> decayProcess;
    protected LinkedList<ProcessMovement> movementProcess;
    protected LinkedList<ProcessExpansion> expansionProcess;
    protected LinkedList<ParallelProcesses> processes;
    protected Lattice lattice;
    
    public String getBase()
    {
        return ".processpanel";
    }

    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {   
        processes = aState.processes;
        insertionProcess = ProcessUtils.locateAllInsertProcess(aState.processes);
        decayProcess = ProcessUtils.locateAllDecayProcess(aState.processes); 
        movementProcess = ProcessUtils.locateAllMovementProcess(aState.processes); 
        expansionProcess = ProcessUtils.locateAllExpansionProcessExceputUserFeedback(aState.processes); 
        lattice = aState.lattice;
        
        // prepare the GUI
        prepareGUI();
    }

    public void stateChanged(ChangeEvent e)
    {
        Object src = e.getSource();
        
        if(src == insertionAmplitude.slider)
        {
            for(ProcessUnitInsertion p : insertionProcess)
            {
                p.setNumProcessIterations(insertionAmplitude.slider.getValue());
            }
                        
            insertionAmplitude.refreshCurrentValue();
        }
        else if(src == totalLocalities.slider)
        {
            int v = totalLocalities.slider.getValue();
            if(v < Lattice.MIN_CELLS)
            {
                v = Lattice.MIN_CELLS;
                totalLocalities.slider.setValue(v);                
            }
            int totalCells = lattice.getRoughTotalCells();
            // check for increase
            if(totalCells < v)
            {
                int diff = (v - totalCells);
                lattice.increaseCells(diff);
            }
            // check for decrease
            else if(totalCells > v)
            {
                int diff = (totalCells - v);
                lattice.decreaseCells(diff);
            }
            
            totalLocalities.refreshCurrentValue();
        }
        else if(src == stimulationAmplitude.slider)
        {
            for(ProcessExpansion p : expansionProcess)
            {
                p.setNumProcessIterations(stimulationAmplitude.slider.getValue());
            }
                        
            stimulationAmplitude.refreshCurrentValue();
        }        
        else if(src == decayIdealEnergy.slider)
        {
            for(ProcessDecay p : decayProcess)
            {
                p.setIdealEnergy(decayIdealEnergy.slider.getValue());    
            }
                        
            decayIdealEnergy.refreshCurrentValue();
        }
        else if(src == simulationFrequency.slider)
        {
            long v = simulationFrequency.slider.getValue();
            for(ParallelProcesses p : processes)
            {
                // never change network
                if(!p.getName().equalsIgnoreCase(ProcessUtils.NETWORK_PROCESS_NAME))
                {
                    p.setWaitTime(v);
                }
            }
            simulationFrequency.refreshCurrentValue();
        }
        else if(src == movementAmplitude.slider)
        {
            for(ProcessMovement p : movementProcess)
            {
                p.setNumProcessIterations(movementAmplitude.slider.getValue());
            }                        
            movementAmplitude.refreshCurrentValue();
        }
        else if(src == movementProbability.slider)
        {
            double v = movementProbability.slider.getValue() / 100.0;
            for(ProcessMovement p : movementProcess)
            {
                p.setProbability(v);
            }            
            movementProbability.refreshCurrentValue();
        }
        else if(src == decayAmount.slider)
        {
            double v = decayAmount.slider.getValue() / 100.0;
            for(ProcessDecay p : decayProcess)
            {
                p.setDecayAmount(v);
            }            
            decayAmount.refreshCurrentValue();
        }
        
    }
    
    protected void prepareGUI()
    {
        JPanel p = prepareConfigPanel();
        this.setLayout(new BorderLayout());
        this.add(p, BorderLayout.NORTH);
        this.setName(NAME);
    } 
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == movementType.checkbox)
        {
            if(movementType.checkbox.isSelected())
            {
                decayAmount.slider.setEnabled(false);
                decayIdealEnergy.slider.setEnabled(true); // use ideal energy
                for(ProcessDecay p : decayProcess)
                {
                    p.setAutomatic(true);
                }
            }
            else
            {
                decayIdealEnergy.slider.setEnabled(false);
                decayAmount.slider.setEnabled(true); // use static decay amount
                
                double v = decayAmount.slider.getValue() / 100.0;
                for(ProcessDecay p : decayProcess)
                {
                    p.setAutomatic(false);
                    p.setDecayAmount(v); // reset static decay amount
                }
            }
        }
    }
    
    
    protected JPanel prepareConfigPanel()
    {          
        SliderPanel.MODE m = SliderPanel.MODE.HORIZONTAL;
        int totalSliders = 9;
        
        // create sliders
        stimulationAmplitude = new SliderPanel(m, "Stimulation Amplitude", new JSlider(1, 20, (int)expansionProcess.getFirst().getNumProcessIterations()));
        insertionAmplitude = new SliderPanel(m, "Insertion Amplitude", new JSlider(0, 20, (int)insertionProcess.getFirst().getNumProcessIterations()));
        decayIdealEnergy = new SliderPanel(m, "Decay Ideal Energy", new JSlider(20, 200, decayProcess.getFirst().getIdealEnergy()));
        decayAmount = new SliderPanel(m, "Decay Amount", new JSlider(0, 100, (int)Math.round(decayProcess.getFirst().getDecayAmount()*100.0)));
        simulationFrequency = new SliderPanel(m, "Simulation Frequency", new JSlider(10, 1000, (int)processes.getFirst().getWaitTime()));
        movementAmplitude  = new SliderPanel(m, "Movement Amplitude", new JSlider(0, 20, (int)movementProcess.getFirst().getNumProcessIterations()));
        movementProbability = new SliderPanel(m, "Movement Probability", new JSlider(0, 100,(int) Math.round(movementProcess.getFirst().getProbability()*100.0)));
        movementType = new MovementControlPanel();
        totalLocalities = new SliderPanel(m, "Total Localities", new JSlider(0, 100, lattice.getInitialTotalCells()));
        
        // prepare decay
        if(decayProcess.getFirst().isAutomatic())
        {
            decayAmount.slider.setEnabled(false);
            movementType.checkbox.setSelected(true);
        }
        else
        {
            decayIdealEnergy.slider.setEnabled(false);
            movementType.checkbox.setSelected(false);
        }
        
        // configure sliders
        insertionAmplitude.prepareSlider(this, 1, 2);        
        decayIdealEnergy.prepareSlider(this, 20, 50);   
        simulationFrequency.prepareSlider(this, 10, 200);
        movementAmplitude.prepareSlider(this, 1, 2);
        movementProbability.prepareSlider(this, 1, 10);
        decayAmount.prepareSlider(this, 1, 10);
        movementType.checkbox.addActionListener(this);   
        stimulationAmplitude.prepareSlider(this, 1, 2);
        totalLocalities.prepareSlider(this, 1, 10);
        
        JPanel p = new JPanel(new GridLayout(totalSliders, 1));        
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), NAME));
        
        // processes
        p.add(simulationFrequency);
        p.add(stimulationAmplitude);
        p.add(totalLocalities);
        p.add(insertionAmplitude);
        p.add(movementType);
        p.add(decayIdealEnergy);
        p.add(decayAmount);
        p.add(movementAmplitude);
        p.add(movementProbability);
        
        return p;
    }    
    
    
    protected class MovementControlPanel extends JPanel
    {
        protected JCheckBox checkbox;
        
        public MovementControlPanel()
        {
            checkbox = new JCheckBox("(enabled is regulator homeostasis, disabled is conformer homeostasis)");
            checkbox.setForeground(Color.GRAY);
            JLabel label = new JLabel("Automatic Decay:");
            label.setForeground(Color.GRAY);
                       
            
            setLayout(new BorderLayout());
            add(label, BorderLayout.WEST);
            add(checkbox, BorderLayout.CENTER);
        }
    }
    
}
