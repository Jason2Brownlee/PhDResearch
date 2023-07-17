
package humint.gui;

import humint.FileUtils;
import humint.Solution;
import humint.algorithm.MutationAlgorithm;
import humint.problem.Problem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Type: MasterPanel<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MasterPanel extends JPanel
    implements ActionListener, ChangeListener
{
    public final static Integer [] POP_SIZE_OPTIONS = {4, 9, 16};
    public final static String PROBLEM_LIST_FILE = "problems.properties";  
    
    protected Problem [] problemList;
    protected InteractivePanel [] interactivePanels;
    
    protected LinkedList<Solution> population;
    protected MutationAlgorithm algorithm;
    protected InternalMouseListener stepListener;
    protected AlgorithmStepper stepper;    
    
    protected JComboBox problemCombo; 
    protected JSlider mutationSlider;
    protected JComboBox popSizeCombo; 
    protected int popSize;
    protected JPanel displayPanel;
    
    
    public MasterPanel()
    {
        prepareGui();
    }
    
    protected void prepareGui()
    {
        JPanel feedbackPanel = prepareFeedbackPanel();
        JPanel controlPanel = prepareControlPanel(); 
        
        setLayout(new BorderLayout());
        add(feedbackPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        
        popSizeCombo.setSelectedIndex(1); 
        problemCombo.setSelectedIndex(0);        
    }
    
    
    
    protected JPanel prepareFeedbackPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Feedback Panel"));
        
        stepListener = new InternalMouseListener();
        displayPanel = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(displayPanel);
        
        return p;
    }
    
    
    
    protected void prepareDisplay()
    {
        popSize = (Integer) popSizeCombo.getSelectedItem();
        int n = (int) Math.sqrt(popSize);
        
        displayPanel.removeAll();  
        displayPanel.setLayout(new GridLayout(n,n));
                 
        interactivePanels = new InteractivePanel[popSize];        
        for (int i = 0; i < interactivePanels.length; i++)
        {
            interactivePanels[i] = new InteractivePanel();
            interactivePanels[i].addMouseListener(stepListener);
            displayPanel.add(interactivePanels[i]);
        }
        
        displayPanel.setVisible(false);
        displayPanel.setVisible(true);
        displayPanel.repaint();
        
        // fill the population 
        population.clear();
        setProblem((Problem)problemCombo.getSelectedItem());
    }
    
    
    protected JPanel prepareControlPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Control Panel"));
        
        preapreAlgorithm();
        
        loadProblemList();
        problemCombo = new JComboBox(problemList);
        problemCombo.addActionListener(this);                
        JPanel p1 = new JPanel();        
        p1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem"));
        p1.add(problemCombo);    
        
        popSizeCombo = new JComboBox(POP_SIZE_OPTIONS);
        popSizeCombo.addActionListener(this);
        JPanel p3 = new JPanel();
        p3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Display Options"));
        p3.add(popSizeCombo);
        
        mutationSlider = new JSlider(0, 100);
        mutationSlider.setOrientation(JSlider.HORIZONTAL);
        mutationSlider.setMinorTickSpacing(1);
        mutationSlider.setMajorTickSpacing(10);
        mutationSlider.setValue((int)Math.round(algorithm.getStdev()*100.0));
        mutationSlider.setSnapToTicks(true);
        mutationSlider.setPaintTicks(true);        
        mutationSlider.addChangeListener(this);
        mutationSlider.setPaintLabels(true); 
        
        JPanel p2 = new JPanel(new BorderLayout());        
        p2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Mutation"));
        p2.add(mutationSlider);
        
        p.setLayout(new GridLayout(1, 0));
        p.add(p1);
        p.add(p3);
        p.add(p2);
        
        return p;
    }
    
    
    protected void setProblem(Problem p)
    {
        population.clear(); // population is now void
        for (int i = 0; i < interactivePanels.length; i++)
        {
            interactivePanels[i].setProblem(p);
        }
        // fill the population 
        stepper.wakeup();
    }
    
    protected void preapreAlgorithm()
    {
        population = new LinkedList<Solution>();
        algorithm = new MutationAlgorithm();
        algorithm.setStdev(0.1);
        stepper = new AlgorithmStepper();
        startStepperThread();
    }
    

    
    protected void stepAlgorithm()
    {        
        Problem p = (Problem) problemCombo.getSelectedItem();
        
        if(population.isEmpty())
        {            
            // random initial pop
            while(population.size() < popSize)
            {
                population.add(algorithm.randomSolution(p));
            }
        }        
        else
        {
            // selection
            Collections.shuffle(population); // in case all the same
            Collections.sort(population);
            Solution best = population.getLast(); // asc - so get most stim 
            // mutation
            population.clear();
            population.add(best);
            best.setScore(0);
            while(population.size() < popSize)
            {
                population.add(algorithm.execute(best, p));
            }            
        }  
        
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    outlayNewSolutions();
                }
            });
        }
        catch (Exception e)
        {}
    }
    
    protected void outlayNewSolutions()
    {
        for (int i = 0; i < interactivePanels.length; i++)
        {
            interactivePanels[i].setSolution(population.get(i));
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == problemCombo)
        {
            Problem p = (Problem) problemCombo.getSelectedItem();
            setProblem(p);
        }
        else if(src == popSizeCombo)
        {
            prepareDisplay();
        }
    }    
    

    protected void loadProblemList()
    {
        String data = FileUtils.loadFile(PROBLEM_LIST_FILE);
        if(data == null)
        {
            throw new RuntimeException("Unable to load problem list");
        }
        LinkedList<Problem> tmpList = new LinkedList<Problem>();
        String [] lines = data.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            line = line.trim();
            if(line.length()>0 && !line.startsWith("//") && !line.startsWith("#"))
            {
                try
                {
                    Problem p = (Problem) (Class.forName(line)).newInstance();
                    tmpList.add(p);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Unable to load problem class from properties file: " + line);
                }
            }
        }
        if(tmpList.isEmpty())
        {
            throw new RuntimeException("Unable to load any problems from problem list.");
        }
        problemList = tmpList.toArray(new Problem[tmpList.size()]);
        Arrays.sort(problemList);        
    }
    
    
    protected class InternalMouseListener extends MouseAdapter
    {        
        @Override
        public void mouseClicked(MouseEvent evt)
        {
            stepper.wakeup();
        }
    }
    

    protected class AlgorithmStepper implements Runnable
    {
        protected volatile boolean requestMade; 
        
        public void run()
        {
            do
            {
               synchronized(this)
               {                   
                   try
                   {
                       this.wait();
                   }
                   catch (InterruptedException e)
                   {}
                   if(requestMade)
                   {
                       stepAlgorithm();
                       requestMade = false;
                   }
               }               
            }
            while(true);
        }
        
        public void wakeup()
        {
            synchronized(this)
            {
                requestMade = true;
                this.notify();
            }
        }
    }
    
    public void startStepperThread()
    {
        new Thread(stepper).start();
    }

    public void stateChanged(ChangeEvent e)
    {
        Object src = e.getSource(); 
        if(src == mutationSlider)
        {
            double v = mutationSlider.getValue();
            algorithm.setStdev(v / 100.0);
        }
    }
    
    
}
