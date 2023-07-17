
package funcopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.SearchCompletionNotify;
import funcopt.Solution;
import funcopt.algorithms.ClonalSelectionAlgorithm;
import funcopt.algorithms.CrowdingGeneticAlgorithm;
import funcopt.algorithms.DifferentialEvolution;
import funcopt.algorithms.EvolutionStrategies;
import funcopt.algorithms.FitnessSharingGeneticAlgorithm;
import funcopt.algorithms.GeneralizedExtremalOptimization;
import funcopt.algorithms.ParallelHillclimbingAlgorithm;
import funcopt.algorithms.ParticleSwarmOptimization;
import funcopt.algorithms.RandomSearch;
import funcopt.algorithms.RealValueGeneticAlgorithm;
import funcopt.algorithms.SimpleGeneticAlgorithm;
import funcopt.algorithms.SimulatedAnnealing;
import funcopt.algorithms.UniformSearch;
import funcopt.gui.plots.InterpolatedFunctionPlot;
import funcopt.gui.plots.ThreeDimensionalSurfacePlot;
import funcopt.problems.AckleysPathFunction10;
import funcopt.problems.AxisParalleHyper_EllipsoidFunction;
import funcopt.problems.BraninssRcosFunction;
import funcopt.problems.Bumps;
import funcopt.problems.CPF1;
import funcopt.problems.CPF2;
import funcopt.problems.DeJongF1;
import funcopt.problems.DeJongF2;
import funcopt.problems.DeJongF3;
import funcopt.problems.DeJongF4;
import funcopt.problems.EasomsFunction;
import funcopt.problems.Euclidean;
import funcopt.problems.Exp;
import funcopt.problems.GoldsteinPricesFunction;
import funcopt.problems.GriewangksFunction8;
import funcopt.problems.HimmelblausFunction;
import funcopt.problems.LangermannFunction;
import funcopt.problems.MichalewiczsFunction;
import funcopt.problems.MultiplePeakFunction;
import funcopt.problems.Peaks;
import funcopt.problems.RastriginFunction;
import funcopt.problems.Ripples;
import funcopt.problems.SchwefelsFunction;
import funcopt.problems.ShekelsFoxholes;
import funcopt.problems.SixHumpCamelBackFunction;
import funcopt.problems.SquashedFrog;
import funcopt.problems.SumOfDifferentPowerFunction;
import funcopt.problems.ThreePotHoles;

/**
 * Type: MasterPanel<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MasterPanel extends JPanel
    implements ActionListener, SearchCompletionNotify
{
    protected ThreeDimensionalSurfacePlot threeDPlot;
    protected InterpolatedFunctionPlot plot;
    protected ProblemPanel problemPanel;
    
    protected JComboBox aList;
    protected JComboBox pList;
    
    protected JButton configButton;
    protected JButton startButton;
    protected JButton clearButton;
    
    protected JTextArea log;
    
    protected Algorithm [] algorithmList; 
    protected Problem [] problemList;
    
    
    public MasterPanel()
    {
        prepareGui();
    }
    
    protected void prepareGui()
    {
        setLayout(new BorderLayout());
        add(prepareCentrePanel(), BorderLayout.CENTER);
        add(prepareControlPanel(), BorderLayout.NORTH);
        add(prepareLogPanel(), BorderLayout.SOUTH);
        
        Random r = new Random();
        aList.setSelectedIndex(r.nextInt(algorithmList.length));
        pList.setSelectedIndex(r.nextInt(problemList.length));
    }
    
    protected JComponent prepareCentrePanel()
    {
        plot = new InterpolatedFunctionPlot();
        threeDPlot = new ThreeDimensionalSurfacePlot();
        problemPanel = new ProblemPanel();
                       
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(plot, BorderLayout.CENTER);
        
        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(2, 1));
        p2.add(problemPanel);
        p2.add(threeDPlot);
        
        p1.add(p2, BorderLayout.EAST);
        
        return p1;
        
//        JTabbedPane pane = new JTabbedPane();
//        
//        plot = new InterpolatedFunctionPlot(); 
//        threeDPlot = new ThreeDimensionalSurfacePlot();
//        problemPanel = new ProblemPanel();
//        
//        JPanel p = new JPanel();
//        p.setLayout(new BorderLayout());
//        p.add(plot, BorderLayout.CENTER);
//        p.add(problemPanel, BorderLayout.EAST);
//        
//        pane.add(p, "2D Plot");
//        pane.add(threeDPlot, "3D Plot");
//        
//        return pane;
    }
    
    protected JPanel prepareLogPanel()
    {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Run Log"));
        
        log = new JTextArea(5, 70);
        log.setEditable(false);
        
        p.add(new JScrollPane(log));
        
        return p;
    }
    
    protected JPanel prepareControlPanel()
    {
        JPanel p = new JPanel();
              
        JPanel alg = getAlgorithmPanel();
        JPanel prob = getProblemPanel();
        
        p.setLayout(new BorderLayout());
        p.add(alg, BorderLayout.CENTER);
        p.add(prob, BorderLayout.EAST);
        
        return p;
    }
    
    protected JPanel getAlgorithmPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Algorithm"));
        
        prepareAlgorithmList();
        aList = new JComboBox(algorithmList);
        aList.addActionListener(this);
        
        configButton = new JButton("Configure");
        configButton.addActionListener(this);
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
                
        p.add(new JScrollPane(aList));
        p.add(configButton);
        p.add(startButton);
        p.add(clearButton);
        
        return p;
    }
    protected JPanel getProblemPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Function"));
        
        prepareProblemList();
        pList = new JComboBox(problemList);        
        pList.addActionListener(this);
        p.add(new JScrollPane(pList));
        
        return p;
    }
    
    
    
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == startButton)
        {
            aList.setEnabled(false);
            pList.setEnabled(false);
            startButton.setEnabled(false);
            clearButton.setEnabled(false);
            configButton.setEnabled(false);
            
            Algorithm a = getSelectionAlgorithm();
            a.hideConfigurationFrame();
            plot.clearPoints();            
            a.execute(getSelectionProblem(), this);
        }
        else if(src == clearButton)
        {
            plot.clearPoints();
        }
        else if(src == configButton)
        {
            Algorithm a = getSelectionAlgorithm();
            a.showConfigurationFrame();
        }
        else if(src == aList)
        {
            // TODO
        }
        else if(src == pList)
        {
            Problem p = getSelectionProblem();
            problemPanel.setProblem(p);
            plot.setProblem(p);
            threeDPlot.setProblem(p);
        }            
    }
    
    
    
    
    public void searchComplete(final Solution best)
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                aList.setEnabled(true);
                pList.setEnabled(true);
                startButton.setEnabled(true);
                clearButton.setEnabled(true);
                configButton.setEnabled(true);
                
                String algorithm = getSelectionAlgorithm().getName();
                String problem = getSelectionProblem().getName();
                double [] coord = best.getCoordinate();
                log.append("> "+problem+", ["+algorithm+"], "+best.getScore()+", x["+coord[0]+"], y["+coord[1]+"]\n");
            }
        };
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch (Exception e)
        {}
    }

    protected Problem getSelectionProblem()    
    {
        int index = pList.getSelectedIndex();
        Problem p = problemList[index];
        return p;
    }
    protected Algorithm getSelectionAlgorithm()    
    {
        int index = aList.getSelectedIndex();
        Algorithm a = algorithmList[index];
        return a;
    }
    

   

    protected void prepareAlgorithmList()
    {
        algorithmList = new Algorithm[]
        {
                new RandomSearch(),
                new SimpleGeneticAlgorithm(),
                new DifferentialEvolution(),       
                new RealValueGeneticAlgorithm(),
                new EvolutionStrategies(),
                new ParticleSwarmOptimization(),
                new UniformSearch(),
                new SimulatedAnnealing(),
                new CrowdingGeneticAlgorithm(),
                new FitnessSharingGeneticAlgorithm(), 
                new ParallelHillclimbingAlgorithm(),
                new ClonalSelectionAlgorithm(),
                new GeneralizedExtremalOptimization()
        };
        
        Arrays.sort(algorithmList);
    }
    
    protected void prepareProblemList()
    {
        
//        
// removed boring functions 
//        
        problemList = new Problem[]
        {
                new SchwefelsFunction(),
                new SquashedFrog(),
                new ThreePotHoles(),
                new RastriginFunction(),
                new MultiplePeakFunction(),
                new HimmelblausFunction(),
                new GriewangksFunction8(),
                new DeJongF1(),
                new DeJongF2(),
                new DeJongF3(),
                new DeJongF4(),          
                new CPF1(),
                new CPF2(),
                new AckleysPathFunction10(),
                new SixHumpCamelBackFunction(),
                new ShekelsFoxholes(),
                new LangermannFunction(),
                new AxisParalleHyper_EllipsoidFunction(),               
//                new RotatedHyper_EllipsoidFunction(),
//                new MovedAxisParalleHyper_EllipsoidFunction(),
                new SumOfDifferentPowerFunction(), 
                new MichalewiczsFunction(),
                new BraninssRcosFunction(),
                new EasomsFunction(),
                new GoldsteinPricesFunction(),
                new Bumps(),
                new Euclidean(),
                new Exp(),
                new Ripples(),
                new Peaks(),
                
        };
        
        Arrays.sort(problemList);
        
        for (int i = 0; i < problemList.length; i++)
        {
            problemList[i].initialise();
            problemList[i].addListener(plot);
        }
    }
    
}
