
package jb.selfregulation.display.lattice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;

/**
 * Type: LatticeDisplayPanel<br/>
 * Date: 20/05/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class LatticeDisplayPanel extends JPanel 
    implements LatticeStatusListener, ActionListener
{
    protected LatticeDisplay latticeDisplay;
    
    protected JRadioButton energyButton;    
    protected JRadioButton partitionButton;
    protected JRadioButton networkButton;
    
    protected JCheckBox drawSelectedState;
    protected JCheckBox drawScaleInverted;    

    protected JRadioButton sortEnergy;
    protected JRadioButton sortSelection;
    
    
    public LatticeDisplayPanel()
    {}
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        latticeDisplay.repaint();
    }
    public String getBase()
    {
        return "latticepanel";
    }
    public void loadConfig(String aBase, Properties prop)
    {}
    public void setup(SystemState aState)
    {
        latticeDisplay = new LatticeDisplay(aState.lattice);
        setName("Lattice");
        prepareGui();
        // add to common panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_COMMON_PANELS)).add(this);
    }

    protected void prepareGui()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Lattice Plot Control"));
        p.setLayout(new GridLayout(3, 1));
        
        p.add(getDrawingModePanel());
        p.add(getSortingModePanel());
        p.add(getControlsPanel());        
        
        setLayout(new BorderLayout());
        add(latticeDisplay, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
    }
    
    protected JPanel getControlsPanel()
    {
        JPanel p = new JPanel();
        
        drawSelectedState = new JCheckBox("Draw Selected State", latticeDisplay.isShowSelectionState());
        drawSelectedState.addActionListener(this);        
        
        drawScaleInverted = new JCheckBox("Draw Scale Inverted", latticeDisplay.isInvertScale());
        drawScaleInverted.addActionListener(this);        
        
        p.add(drawSelectedState);
        p.add(drawScaleInverted);
        
        return p;
    }
    
    protected JPanel getSortingModePanel()
    {
        JPanel p = new JPanel();
                
        latticeDisplay.setSortMode(LatticeDisplay.SORT_MODE.SELECTION_STATE);
        
        sortEnergy = new JRadioButton("Sort Energy", false);
        sortSelection = new JRadioButton("Sort Selection State", true);
        
        ButtonGroup group = new ButtonGroup();
        group.add(sortEnergy);
        group.add(sortSelection);
        
        sortEnergy.addActionListener(this);
        sortSelection.addActionListener(this);
        
        p.add(sortEnergy);
        p.add(sortSelection);
        
        return p;
    }
    
    protected JPanel getDrawingModePanel()
    {
        JPanel p = new JPanel();
        
        latticeDisplay.setDrawMode(LatticeDisplay.DRAW_MODE.ENERGY);
        
        energyButton = new JRadioButton("Draw Energy Scale", true);        
        partitionButton = new JRadioButton("Draw Feedback", false);
        networkButton = new JRadioButton("Draw Network", false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(energyButton);
        group.add(partitionButton);
        group.add(networkButton);
        
        energyButton.addActionListener(this);
        partitionButton.addActionListener(this);
        networkButton.addActionListener(this);
        
        p.add(energyButton);
        p.add(partitionButton);
        p.add(networkButton);
        
        return p;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == energyButton)
        {
            latticeDisplay.setDrawMode(LatticeDisplay.DRAW_MODE.ENERGY);
        }
        else if(src == networkButton)
        {
            latticeDisplay.setDrawMode(LatticeDisplay.DRAW_MODE.NETWORK);
        }
        else if(src == partitionButton)
        {
            latticeDisplay.setDrawMode(LatticeDisplay.DRAW_MODE.FEEDBACK);
        }        
        else if(src == drawSelectedState)
        {
            latticeDisplay.setShowSelectionState(drawSelectedState.isSelected());
        }
        else if(src == drawScaleInverted)
        {
            latticeDisplay.setInvertScale(drawScaleInverted.isSelected());
        }
        else if(src == sortEnergy)
        {
            latticeDisplay.setSortMode(LatticeDisplay.SORT_MODE.ENERGY);
        }
        else if(src == sortSelection)
        {
            latticeDisplay.setSortMode(LatticeDisplay.SORT_MODE.SELECTION_STATE);
        }
        
        // update the gui
        latticeDisplay.repaint();
    }
}
