
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
import jb.selfregulation.expansion.proliforation.ProgenyACO;
import jb.selfregulation.expansion.proliforation.ProgenyGA;
import jb.selfregulation.expansion.proliforation.ProgenyMutate;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.processes.ParallelProcesses;
import jb.selfregulation.processes.ProcessWork;
import jb.selfregulation.processes.work.ProcessExpansion;

/**
 * Type: ProliferationSubControlPanel<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProliferationSubControlPanel extends JPanel 
    implements ActionListener
{    
    public final static String NAME = "Proliferation Configuration";
    protected String [] SELECTION_NAMES = {"Genetic Algorithm", "Discrete History Ant Systems", "Random Mutation"};    
    public final static int GA=0, DHAS=1, MUT=2;
        
    protected final ProcessExpansion expansionProcess;
    protected final SystemState state;
    protected final String [] classes;    
    
    protected int currentStrategy = -1;
    
    protected JComboBox comboBox;
    protected JPanel chooserPanel;
    protected JPanel masterPanel;
    protected GAPanel gaPanel;
    protected DHASPanel dhasPanel;
    protected MutationPanel mutationPanel;
    
    
    public ProliferationSubControlPanel(
            ProcessExpansion aProcess, 
            SystemState aState,
            String [] aClasses)
    {
        expansionProcess = aProcess;
        state = aState;
        classes = aClasses;
        
        prepareGUI();
        setupDefaultStrategy(expansionProcess.getProgeny());  
    }    
    
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
        gaPanel = new GAPanel();
        dhasPanel = new DHASPanel();
        mutationPanel = new MutationPanel();
        
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(gaPanel, BorderLayout.CENTER);
        masterPanel.add(dhasPanel, BorderLayout.CENTER);
        masterPanel.add(mutationPanel, BorderLayout.CENTER);
        
        return masterPanel;
    }
    
    
    protected JPanel getSelectorPanel()
    {
        JPanel p = new JPanel();
        comboBox = new JComboBox(SELECTION_NAMES);
        comboBox.addActionListener(this);        
        JLabel label = new JLabel("Proliferation Strategy: ");        
        p.add(label);
        p.add(comboBox);        
        return p;
    }    
    
    protected void setupDefaultStrategy(ProgenyStrategy s)
    {
        if(s instanceof ProgenyGA)
        {
            setupDefaultStrategy((ProgenyGA)s);
        }
        else if(s instanceof ProgenyACO)
        {
            setupDefaultStrategy((ProgenyACO)s);
        }
        else if(s instanceof ProgenyMutate)
        {
            setupDefaultStrategy((ProgenyMutate)s);
        }
        // let the system reflect default config
        comboBox.setSelectedIndex(currentStrategy);
    }
    protected void setupDefaultStrategy(ProgenyGA s)
    {
        masterPanel.add(gaPanel, BorderLayout.CENTER);
        gaPanel.setTotalProgeny(s.getTotalProgeny());
        gaPanel.setCrossover((int)Math.round(s.getCrossoverPercentage()*100.0));
        gaPanel.setMutation((int)Math.round(s.getMutationPercentage()*100.0));
        currentStrategy = GA;
    }
    protected void setupDefaultStrategy(ProgenyACO s)
    {
        masterPanel.add(dhasPanel, BorderLayout.CENTER);
        dhasPanel.setTotalProgeny(s.getTotalProgeny());
        dhasPanel.setHistoryExponent((int)Math.round((s.getHistoryExponent()*200.0)/2));
        currentStrategy = DHAS;
    }
    protected void setupDefaultStrategy(ProgenyMutate s)
    {
        masterPanel.add(mutationPanel, BorderLayout.CENTER);
        mutationPanel.setTotalProgeny(s.getTotalProgeny());
        mutationPanel.setMutation((int)Math.round(s.getMutation()*100.0));
        currentStrategy = MUT;
    }
    
    protected void setupGA()
    {
        if(currentStrategy == GA)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(gaPanel, BorderLayout.CENTER);
        ProgenyGA strategy = null;
        try
        {
            strategy = (ProgenyGA) Class.forName(classes[GA]).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create class ["+classes[GA]+"].", e);
        }
        strategy.setTotalProgeny(gaPanel.getTotalProgeny());
        strategy.setCrossoverPercentage(gaPanel.getCrossover()/100.0);
        strategy.setMutationPercentage(gaPanel.getMutation()/100.0);
        strategy.setup(state);
        expansionProcess.setProgenyStrategy(strategy);
        currentStrategy = GA;
        gaPanel.setVisible(true);
        gaPanel.repaint();
    }
    protected void setupDHAS()
    {
        if(currentStrategy == DHAS)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(dhasPanel, BorderLayout.CENTER);
        ProgenyACO strategy = null;
        try
        {
            strategy = (ProgenyACO) Class.forName(classes[DHAS]).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create class ["+classes[DHAS]+"].", e);
        }
        strategy.setTotalProgeny(dhasPanel.getTotalProgeny());
        strategy.setHistoryExponent((dhasPanel.getHistoryExponent()/200.0)*2.0);
        strategy.setMultiply(true); // HACK HACK HACK
        strategy.setup(state);
        expansionProcess.setProgenyStrategy(strategy);
        currentStrategy = DHAS;
        dhasPanel.setVisible(true);
        dhasPanel.repaint();
    }
    protected void setupMutation()
    {
        if(currentStrategy == MUT)
        {
            return;
        }
        
        masterPanel.removeAll();
        masterPanel.add(chooserPanel, BorderLayout.NORTH);
        masterPanel.add(mutationPanel, BorderLayout.CENTER);
        ProgenyMutate strategy = null;        
        try
        {
            strategy = (ProgenyMutate) Class.forName(classes[MUT]).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create class ["+classes[MUT]+"].", e);
        }
        strategy.setTotalProgeny(gaPanel.getTotalProgeny());
        strategy.setMutation(gaPanel.getMutation()/100.0);
        strategy.setup(state);
        expansionProcess.setProgenyStrategy(strategy);
        currentStrategy = MUT;  
        mutationPanel.setVisible(true);
        mutationPanel.repaint();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == comboBox)
        {
            int index = comboBox.getSelectedIndex();
            switch(index)
            {
                case GA:
                {
                    setupGA();
                    break;
                }
                case DHAS:
                {
                    setupDHAS();
                    break;
                }
                case MUT:
                {
                    setupMutation();
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
     * Type: GAPanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class GAPanel extends JPanel
        implements ChangeListener
    {
        protected SliderPanel totalProgeny;
        protected SliderPanel crossover;
        protected SliderPanel mutation;
        
        public GAPanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Genetic Algorithm Proliferation"));
            
            totalProgeny = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Total Progeny", 1, 200, 20);
            crossover = new SliderPanel(SliderPanel.MODE.HORIZONTAL,    "Crossover %", 0, 100, 95);
            mutation = new SliderPanel(SliderPanel.MODE.HORIZONTAL,    "Mutation %", 0, 100, 5);
            
            totalProgeny.prepareSlider(this, 1, 20);
            crossover.prepareSlider(this, 1, 10);
            mutation.prepareSlider(this, 1, 10);
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(3, 1));
            p.add(totalProgeny);
            p.add(crossover);
            add(mutation);
            
            setLayout(new BorderLayout());
            add(p, BorderLayout.NORTH);
        }
        
        public void stateChanged(ChangeEvent e)
        {
            Object src = e.getSource();
            if(src == totalProgeny.slider)
            {
                int v = totalProgeny.slider.getValue();
                ((ProgenyGA)expansionProcess.getProgeny()).setTotalProgeny(v);
                totalProgeny.refreshCurrentValue();
            }
            else if(src == crossover.slider)
            {
                int v = crossover.slider.getValue();
                double d = v/100.0;
                ((ProgenyGA)expansionProcess.getProgeny()).setCrossoverPercentage(d);
                
                crossover.refreshCurrentValue();
            }
            else if(src == mutation.slider)
            {
                int v = mutation.slider.getValue();
                double d = v/100.0;
                ((ProgenyGA)expansionProcess.getProgeny()).setMutationPercentage(d);
                
                
                mutation.refreshCurrentValue();
            }
        }

        public int getTotalProgeny()
        {
            return totalProgeny.slider.getValue();
        }
        public void setTotalProgeny(int t)
        {
            totalProgeny.slider.setValue(t);
            totalProgeny.refreshCurrentValue();
        }
        public int getCrossover()
        {
            return crossover.slider.getValue();
        }
        public void setCrossover(int t)
        {
            crossover.slider.setValue(t);
            crossover.refreshCurrentValue();
        }

        public int getMutation()
        {
            return mutation.slider.getValue();
        }
        public void setMutation(int t)
        {
            mutation.slider.setValue(t);
            mutation.refreshCurrentValue();
        }        
    }
    
    /**
     * Type: DHASPanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class DHASPanel extends JPanel
        implements ChangeListener
    {
        protected SliderPanel totalProgeny;
        protected SliderPanel historyExponent;
        
        public DHASPanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "DHAS Proliferation Configuration"));
            
            totalProgeny = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Total Progeny", 1, 200, 20);
            historyExponent = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "History Exponent", 1, 200, 20);
            
            totalProgeny.prepareSlider(this, 1, 20);
            historyExponent.prepareSlider(this, 1, 20);
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2, 1));
            p.add(totalProgeny);
            p.add(historyExponent);
            
            setLayout(new BorderLayout());
            add(p, BorderLayout.NORTH);
        }
        
        public void stateChanged(ChangeEvent e)
        {
            Object src = e.getSource();
            if(src == totalProgeny.slider)
            {
                int v = totalProgeny.slider.getValue();
                ((ProgenyACO)expansionProcess.getProgeny()).setTotalProgeny(v);
                
                totalProgeny.refreshCurrentValue();
            }
            else if(src == historyExponent.slider)
            {
                int v = historyExponent.slider.getValue();
                double d = (v/200.0)*2.0;
                ((ProgenyACO)expansionProcess.getProgeny()).setHistoryExponent(v);
                
                historyExponent.refreshCurrentValue();
            }
        }
        public int getTotalProgeny()
        {
            return totalProgeny.slider.getValue();
        }
        public void setTotalProgeny(int t)
        {
            totalProgeny.slider.setValue(t);
            totalProgeny.refreshCurrentValue();
        }
        public int getHistoryExponent()
        {
            return historyExponent.slider.getValue();
        }
        public void setHistoryExponent(int t)
        {
            historyExponent.slider.setValue(t);
            historyExponent.refreshCurrentValue();
        }
    }
    
    /**
     * Type: MutationPanel<br/>
     * Date: 15/02/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class MutationPanel extends JPanel
        implements ChangeListener
    {
        protected SliderPanel totalProgeny;
        protected SliderPanel mutation;
        
        public MutationPanel()
        {
            prepareGUI();
        }
        
        protected void prepareGUI()
        {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Mutation Proliferation Configuration"));
            
            totalProgeny = new SliderPanel(SliderPanel.MODE.HORIZONTAL, "Total Progeny", 1, 200, 20);
            mutation = new SliderPanel(SliderPanel.MODE.HORIZONTAL,    "Mutation %", 0, 100, 5);
            
            totalProgeny.prepareSlider(this, 1, 20);
            mutation.prepareSlider(this, 1, 10);
            
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2, 1));
            p.add(totalProgeny);
            p.add(mutation);
            
            setLayout(new BorderLayout());
            add(p, BorderLayout.NORTH);
        }
        
        public void stateChanged(ChangeEvent e)
        {
            Object src = e.getSource();
            if(src == totalProgeny.slider)
            {
                int v = totalProgeny.slider.getValue();
                ((ProgenyMutate)expansionProcess.getProgeny()).setTotalProgeny(v);
                
                
                totalProgeny.refreshCurrentValue();
            }
            else if(src == mutation.slider)
            {
                int v = mutation.slider.getValue();
                double d = v/100.0;
                ((ProgenyMutate)expansionProcess.getProgeny()).setMutation(d);
                
                
                mutation.refreshCurrentValue();
            }
        }

        public int getTotalProgeny()
        {
            return totalProgeny.slider.getValue();
        }
        public void setTotalProgeny(int t)
        {
            totalProgeny.slider.setValue(t);
            totalProgeny.refreshCurrentValue();
        }
        public int getMutation()
        {
            return mutation.slider.getValue();
        }
        public void setMutation(int t)
        {
            mutation.slider.setValue(t);
            mutation.refreshCurrentValue();
        }     
    }
}
