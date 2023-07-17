
package swsom.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import swsom.algorithm.Algorithm;
import swsom.algorithm.IterationEventListener;
import swsom.algorithm.SOMMap;
import swsom.algorithm.algorithm.ConnectivityBasedSOM;
import swsom.algorithm.algorithm.GrowingAreaBasedSOM;
import swsom.algorithm.algorithm.SOMAlgorithm;
import swsom.algorithm.algorithm.SatelliteConnectivityBasedSOMAlgorithm;
import swsom.algorithm.maps.OneDimensionalLatticeMap;
import swsom.algorithm.maps.SatelliteVolumeMap;
import swsom.algorithm.maps.SquareVolumeMap;
import swsom.algorithm.maps.TwoDimensionalLatticeMap;
import swsom.algorithm.problem.CircleProblem;
import swsom.algorithm.problem.FourSquaresProblem;
import swsom.algorithm.problem.GenericShapeProblem;
import swsom.algorithm.problem.HProblem;
import swsom.algorithm.problem.SquareProblem;
import swsom.algorithm.problem.TriangleProblem;
import swsom.algorithm.stats.CoverageStatistics;
import swsom.algorithm.stats.GraphStatistics;
import swsom.algorithm.stats.PlacementStatistics;

/**
 * Type: MainPane<br/>
 * Date: 23/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MainPane extends JPanel
    implements ActionListener
{
    protected Random r = new Random();
    protected InternalWaitListener waitListener = new InternalWaitListener();
    protected int totalSamples = 2000;
    protected int histogramInterval = 40;
    protected int waitTime = 0;
    
    protected ProblemDisplay problemDisplay;
    protected JTextArea reportLog;
    
    protected JComboBox algorithmCombo;
    protected JButton algorithmConfig;
    protected JButton runAlgorithm;
    protected Algorithm [] algorithms = {new SOMAlgorithm(), new ConnectivityBasedSOM(), new SatelliteConnectivityBasedSOMAlgorithm(), new GrowingAreaBasedSOM()};

    protected JComboBox structureCombo;
    protected JButton structureConfig;    
    
    SOMMap squareMap = new SquareVolumeMap();
    SOMMap satelliteSomMap = new SatelliteVolumeMap();
    SOMMap oneDSomMap = new OneDimensionalLatticeMap();
    SOMMap twoDSomMap = new TwoDimensionalLatticeMap();
    
    protected SOMMap [] squareStructures = {squareMap};
    protected SOMMap [] satellitesomStructures = {satelliteSomMap};
    protected SOMMap [] conmnsomStructures = {oneDSomMap, twoDSomMap, squareMap};
    protected SOMMap [] somStructures = {twoDSomMap};

    protected JComboBox problemCombo;
    protected JButton problemConfig;
    protected GenericShapeProblem [] problems = {new SquareProblem(), new TriangleProblem(), new HProblem(), new CircleProblem(), new FourSquaresProblem()};
   
    public MainPane()
    {
        prepareGUI();
        
        // set defaults
        algorithmCombo.setSelectedIndex(0);
        
        // prepare listenes
        for(Algorithm a : algorithms)
        {
            a.addListener(problemDisplay);
            a.addListener(waitListener);
        }
    }    
    
    protected void prepareGUI()
    {
        JPanel algorithmPanel = prepareAlgorithmPanel();
        JPanel structurePanel = prepareStructurePanel();
        JPanel problemPanel = prepareProblemPanel();
        JPanel displayPanel = prepareDisplayPanel();
        
        JPanel top = new JPanel();
        top.add(algorithmPanel);
        top.add(structurePanel);
        top.add(problemPanel);        
        
        setLayout(new BorderLayout());
        add(displayPanel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
    }
    
    protected void updateStructureList(Algorithm a)
    {
        structureCombo.removeAllItems();        
        
        if(a instanceof GrowingAreaBasedSOM)
        {
            for (SOMMap s :squareStructures)
            {
                structureCombo.addItem(s);
            }
        }
        else if(a instanceof SatelliteConnectivityBasedSOMAlgorithm)
        {
            for (SOMMap s : satellitesomStructures)
            {
                structureCombo.addItem(s);
            }
        }
        else if(a instanceof ConnectivityBasedSOM)
        {            
            for (SOMMap s : conmnsomStructures)
            {
                structureCombo.addItem(s);
            }
        }
        else if(a instanceof SOMAlgorithm)
        {
            for (SOMMap s : somStructures)
            {
                structureCombo.addItem(s);
            }
        }
        else
        {
            throw new RuntimeException("Unknown algorithm.");
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        // algorithms
        if(src == algorithmCombo)
        {            
            Algorithm a = getSelectedAlgorithm();
            a.hideConfigurationFrame();
            updateStructureList(a); 
        }
        else if(src == algorithmConfig)
        {
            Algorithm a = getSelectedAlgorithm();
            a.showConfigurationFrame();
        }
        else if(src == runAlgorithm)
        {
            Algorithm a = getSelectedAlgorithm();
            SOMMap s = getSelectedStructure();
            GenericShapeProblem p = getSelectedProblem();
            a.hideConfigurationFrame();
            s.hideConfigurationFrame();
            runAlgorithm(a, s, p);
        }      
        
        // structures
        else if(src == structureCombo)
        {
            SOMMap s = getSelectedStructure();
            if(s!=null)
            {
                s.hideConfigurationFrame();
            }
        }
        else if(src == structureConfig)
        {
            SOMMap s = getSelectedStructure();
            s.showConfigurationFrame();
        }
        
        // problems
        else if(src == problemCombo)
        {
            GenericShapeProblem p = getSelectedProblem();
            problemDisplay.setProblem(p);
            problemDisplay.setSample(null);
            problemDisplay.setMap(null);
        }
        else if(src == problemConfig)
        {
            // TODO show problem config
        }
        
    }
    
    
    protected void runAlgorithm(final Algorithm a, final SOMMap s, final GenericShapeProblem p)
    {
        disableControls();
        
        // prepare sample and draw things
        final double [][] samples = p.generateSampleWithoutReplacement(r, totalSamples);
        problemDisplay.setSample(samples);
        problemDisplay.setMap(s);
//        double [][] histogram = p.calculateDistributionMap(histogramInterval, samples);
//        histogramDisplay.setHistogram(histogram);        
        
        // prepare the structure
        s.initialise(r, samples);
        // prepare the algorithm
        a.initialise();
        
        // run the algorithm
        Runnable run = new Runnable()
        {
            public void run()
            {
                a.run(s, p, r, samples);
                Runnable e = new Runnable()
                {
                    public void run(){enableControls();displayReport(a,s,p,samples);}
                };
                try{SwingUtilities.invokeLater(e);}catch(Exception e1){}
            }
        };
        new Thread(run).start();
    }
    
    protected void displayReport(Algorithm a, SOMMap s, GenericShapeProblem p, double [][] sample)
    {
        StringBuffer b = new StringBuffer(1024);
        
        b.append("- Problem Report -\n");
        b.append(p.getProblemReport()+"\n");
        b.append("- Structure Report -\n");
        b.append(GraphStatistics.prepareGraphReport(s)+"\n");
        b.append("- Placement Report -\n");
        b.append(PlacementStatistics.preparePlacementStatisticReport(p, s, sample, histogramInterval)+"\n");
        b.append("- Coverage Report -\n");
        b.append(CoverageStatistics.prepareCoverageReport(s, p, sample)+"\n");           
                
        reportLog.setText(b.toString());
    }
    

    
    protected Algorithm getSelectedAlgorithm()
    {
        Algorithm a = (Algorithm) algorithmCombo.getSelectedItem();        
        return a;
    }   
    protected SOMMap getSelectedStructure()
    {
        SOMMap a = (SOMMap) structureCombo.getSelectedItem();        
        return a;
    } 
    
    protected GenericShapeProblem getSelectedProblem()
    {
        GenericShapeProblem a = (GenericShapeProblem) problemCombo.getSelectedItem();  
        return a;
    } 
    
    protected JPanel prepareAlgorithmPanel()
    {                
        algorithmCombo = new JComboBox(algorithms);
        algorithmCombo.addActionListener(this);
        algorithmConfig = new JButton("Config"); 
        algorithmConfig.addActionListener(this);
        runAlgorithm = new JButton("Run"); 
        runAlgorithm.addActionListener(this);
        
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Algorithm"));
        p.add(algorithmCombo);
        p.add(algorithmConfig);
        p.add(runAlgorithm);        
        return p;
    }
    
    protected JPanel prepareStructurePanel()
    {
        structureCombo = new JComboBox(somStructures);
        structureCombo.addActionListener(this);
        structureConfig = new JButton("Config"); 
        structureConfig.addActionListener(this);

        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Structure"));
        p.add(structureCombo);
        p.add(structureConfig);
        return p;
    }    
    
    protected JPanel prepareProblemPanel()
    {
        problemCombo = new JComboBox(problems);
        problemCombo.addActionListener(this);
        problemConfig = new JButton("Config"); 
        problemConfig.addActionListener(this);

        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem"));
        p.add(problemCombo);
        p.add(problemConfig);
        return p;
    }
    
    protected JPanel prepareDisplayPanel()
    {        
        problemDisplay = new ProblemDisplay();        
        reportLog = new JTextArea(20, 5);
        reportLog.setEditable(false);
        reportLog.setFont(new Font("Courier", Font.PLAIN, 12)); // fixed width
        
        JPanel p = new JPanel(new BorderLayout());

        JPanel problemPanel = new JPanel(new BorderLayout());
        problemPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem Display"));
        problemPanel.add(problemDisplay);
        
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Run Report"));
        logPanel.add(new JScrollPane(reportLog));
        
        p.add(problemPanel, BorderLayout.WEST);
        p.add(logPanel, BorderLayout.CENTER);
                
        return p;
    }
    
    public void disableControls()
    {
        algorithmCombo.setEnabled(false);
        algorithmConfig.setEnabled(false);
        runAlgorithm.setEnabled(false);
        structureCombo.setEnabled(false);
        structureConfig.setEnabled(false);
        problemCombo.setEnabled(false);
        problemConfig.setEnabled(false);
    }
    public void enableControls()
    {
        algorithmCombo.setEnabled(true);
        algorithmConfig.setEnabled(true);
        runAlgorithm.setEnabled(true);
        structureCombo.setEnabled(true);
        structureConfig.setEnabled(true);
        problemCombo.setEnabled(true);
        problemConfig.setEnabled(true);
    }
    
    protected class InternalWaitListener implements IterationEventListener
    {
        public void iterationEvent(int iteration)
        {
            try
            {
                if((iteration%20) == 0)
                {
                    Thread.sleep(waitTime);
                }
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }        
    }
    
}
