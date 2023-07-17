
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.selection.SelectionAll;
import jb.selfregulation.expansion.selection.SelectionGreedy;
import jb.selfregulation.expansion.selection.SelectionStrategy;
import jb.selfregulation.expansion.selection.SelectionTournament;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessWork;
import jb.selfregulation.processes.work.ProcessExpansion;

/**
 * Type: SelectionSubControlPanel<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SelectionSubControlPanel extends JPanel 
    implements ActionListener
{
    public final static String NAME = "Selection Configuration";
    protected String [] SELECTION_NAMES = {"Select All", "Elite Selection", "Tournament Selection"};    
    public final static int ALL=1, ELITE=2, TOURN=3;
        
    protected final ProcessExpansion expansionProcess;
    protected final SystemState state;
    protected int currentStrategy;
    
    protected JComboBox comboBox;
    protected JPanel chooserPanel;
    protected JPanel masterPanel;
    protected AllPanel allPanel;
    protected ElitePanel elitePanel;
    protected TournPanel tournPanel;
    
    public SelectionSubControlPanel(ProcessExpansion aProcess, SystemState aState)
    {
        expansionProcess = aProcess;       
        state = aState;

        // prepare the GUI
        prepareGUI();
        setupDefaultStrategy(expansionProcess.getSelection());
    }
    
//    public String getBase()
//    {        
//        return ".selectionsubpanel";
//    }
//    public void loadConfig(String aBase, Properties prop)
//    {}
//
//    public void setup(SystemState aState)
//    {    
//        state = aState;
//    }   
    
    protected void prepareGUI()
    {
        JPanel p = prepareControlPanel();
        this.setLayout(new BorderLayout());
        this.add(p);
        this.setName(NAME);
    } 
    
    protected JPanel prepareControlPanel()
    {
        masterPanel = new JPanel();
        masterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), NAME));
        masterPanel.setLayout(new BorderLayout());
        
        chooserPanel = getSelectorPanel();        
        allPanel = new AllPanel();
        elitePanel = new ElitePanel();
        tournPanel = new TournPanel();
        
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(allPanel, BorderLayout.CENTER);
        masterPanel.add(elitePanel, BorderLayout.CENTER);
        masterPanel.add(tournPanel, BorderLayout.CENTER);
        
        return masterPanel;
    }
    
    
    protected JPanel getSelectorPanel()
    {
        JPanel p = new JPanel();
        comboBox = new JComboBox(SELECTION_NAMES);
        comboBox.addActionListener(this);
        
        JLabel label = new JLabel("Selection Strategy: ");
        
        p.add(label);
        p.add(comboBox);
        
        return p;
    }    
    
    protected void setupDefaultStrategy(SelectionStrategy s)
    {
        if(s instanceof SelectionAll)
        {
            setupDefaultStrategy((SelectionAll)s);
        }
        else if(s instanceof SelectionGreedy)
        {
            setupDefaultStrategy((SelectionGreedy)s);
        }
        else if(s instanceof SelectionTournament)
        {
            setupDefaultStrategy((SelectionTournament)s);
        }
        // let the system reflect default config
        comboBox.setSelectedIndex(currentStrategy-1);
    }
    protected void setupDefaultStrategy(SelectionAll s)
    {
        masterPanel.add(allPanel, BorderLayout.CENTER);
        currentStrategy = ALL;
    }
    protected void setupDefaultStrategy(SelectionGreedy s)
    {
        masterPanel.add(elitePanel, BorderLayout.CENTER);
        elitePanel.setTotal(s.getNumToSelect());
        currentStrategy = ELITE;
    }
    protected void setupDefaultStrategy(SelectionTournament s)
    {
        masterPanel.add(tournPanel, BorderLayout.CENTER);
        tournPanel.setTotal(s.getNumToSelect());
        tournPanel.setTournamentSize(s.getTournamentSize());
        currentStrategy = TOURN;
    }
    
    protected void setupAll()
    {
        if(currentStrategy == ALL)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(allPanel, BorderLayout.CENTER);
        SelectionAll strategy = new SelectionAll();
        strategy.setup(state);
        expansionProcess.setSelectionStrategy(strategy);
        currentStrategy = ALL;
        allPanel.setVisible(true);
        allPanel.repaint();
    }
    protected void setupElite()
    {
        if(currentStrategy == ELITE)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(elitePanel, BorderLayout.CENTER);
        SelectionGreedy strategy = new SelectionGreedy();
        strategy.setNumToSelect(elitePanel.getTotal());
        strategy.setup(state);
        expansionProcess.setSelectionStrategy(strategy);
        currentStrategy = ELITE;
        elitePanel.setVisible(true);
        elitePanel.repaint();
    }
    protected void setupTournament()
    {
        if(currentStrategy == TOURN)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(tournPanel, BorderLayout.CENTER);
        SelectionTournament strategy = new SelectionTournament();        
        strategy.setTournamentSize(tournPanel.getTournamentSize());
        strategy.setNumToSelect(tournPanel.getTotal());
        strategy.setup(state); // for rand
        expansionProcess.setSelectionStrategy(strategy);
        currentStrategy = TOURN;  
        tournPanel.setVisible(true);
        tournPanel.repaint();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == comboBox)
        {
            int index = comboBox.getSelectedIndex();
            switch(index+1)
            {
                case ALL:
                {
                    setupAll();
                    break;
                }
                case ELITE:
                {
                    setupElite();
                    break;
                }
                case TOURN:
                {
                    setupTournament();
                    break;
                }
                default:
                {
                    throw new RuntimeException("Unknown selection " + index );
                }
            }
            
            // hack to get the panel switching working
            this.setVisible(false);
            this.setVisible(true);
        }        
    }
    
    /**
     * Type: AllPanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class AllPanel extends JPanel
    {
        public AllPanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "All Selection Configuration"));
        }
    }
    
    /**
     * Type: ElitePanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class ElitePanel extends JPanel
        implements ChangeListener
    {
        protected SliderPanel totalSelected;
        
        public ElitePanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Elite Selection Configuration"));
            totalSelected = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Total Selected", 2, 200, 20);
            totalSelected.prepareSlider(this, 1, 20);
            setLayout(new BorderLayout());
            add(totalSelected, BorderLayout.NORTH);
        }
        
        public void stateChanged(ChangeEvent e)
        {
            Object src = e.getSource();
            if(src == totalSelected.slider)
            {
                int v = totalSelected.slider.getValue();
                ((SelectionGreedy)expansionProcess.getSelection()).setNumToSelect(v);
                totalSelected.refreshCurrentValue();
            }
        }

        public int getTotal()
        {
            return totalSelected.slider.getValue();
        }
        public void setTotal(int t)
        {
            totalSelected.slider.setValue(t);
            totalSelected.refreshCurrentValue();
        }
    }
    
    /**
     * Type: TournPanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class TournPanel extends JPanel
        implements ChangeListener
    {
        protected SliderPanel totalSelected;
        protected SliderPanel boutSize;
        
        public TournPanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tournament Selection Configuration"));
            totalSelected = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Total Selected", 2, 200, 20);
            boutSize = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Bout Size", 1, 200, 20);
            totalSelected.prepareSlider(this, 1, 20);
            boutSize.prepareSlider(this, 1, 20);
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2, 1));
            p.add(totalSelected);
            p.add(boutSize);
            
            setLayout(new BorderLayout());
            add(p, BorderLayout.NORTH);
        }
        
        public void stateChanged(ChangeEvent e)
        {
            Object src = e.getSource();
            if(src == totalSelected.slider)
            {
                int v = totalSelected.slider.getValue();
                ((SelectionTournament)expansionProcess.getSelection()).setNumToSelect(v);                 
                totalSelected.refreshCurrentValue();
            }
            else if(src == boutSize.slider)
            {
                int v = boutSize.slider.getValue();
                ((SelectionTournament)expansionProcess.getSelection()).setNumToSelect(v);                
                boutSize.refreshCurrentValue();
            }
        }

        public int getTotal()
        {
            return totalSelected.slider.getValue();
        }
        public void setTotal(int t)
        {
            totalSelected.slider.setValue(t);
            totalSelected.refreshCurrentValue();
        }
        public int getTournamentSize()
        {
            return boutSize.slider.getValue();
        }
        public void setTournamentSize(int t)
        {
            boutSize.slider.setValue(t);
            boutSize.refreshCurrentValue();
        }
    }
}
