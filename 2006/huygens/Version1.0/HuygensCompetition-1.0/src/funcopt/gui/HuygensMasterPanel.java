
package funcopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.gui.plots.LineGraph;
import funcopt.problem.BlindSamplePlot;
import funcopt.problem.HuygensInternalBenchmarkProblem;
import funcopt.problem.HuygensProblem;
import funcopt.problem.HuygensServerBenchmarkProblem;

/**
 * Type: HuygensMasterPanel<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensMasterPanel extends MasterPanel
{
    protected JFrame parent;
    protected JButton problemConfigButton;
    protected BlindSamplePlot blindPlot;
    protected BenchmarkPanel benchmarkPanel;
    
    public HuygensMasterPanel(JFrame f)
    {
        parent = f;
    }
    
    @Override
    protected JComponent prepareCentrePanel()
    {
        //plot = new InterpolatedFunctionPlot();
        //threeDPlot = new ThreeDimensionalSurfacePlot();
        blindPlot = new BlindSamplePlot();
        lineGraph = new LineGraph();
        problemPanel = new HuygensProblemPanel(); // new 
        benchmarkPanel = new BenchmarkPanel();
        
        // prepare south east
        JTabbedPane tp = new JTabbedPane();
        //tp.add("2D Function Plot", plot);
        tp.add("Blind Plot", blindPlot);
        tp.add("Algorithm Performance", lineGraph);
        tp.add("Benchmark Results", benchmarkPanel);
               
        // preapre east
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(problemPanel, BorderLayout.NORTH);
        //p2.add(threeDPlot, BorderLayout.CENTER);      
        p2.add(new JPanel(), BorderLayout.CENTER);
        
        // prepare the lot
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(tp, BorderLayout.CENTER);
        p1.add(p2, BorderLayout.EAST);
        
        return p1;
    }
    
    @Override
    protected void prepareProblemListeners()
    {
        for (int i = 0; i < problemList.length; i++)
        {
            problemList[i].initialise();
            problemList[i].addListener(blindPlot);
        }
    }
    
    @Override
    protected void clear()
    {
        blindPlot.clearPoints();
        lineGraph.clear(1000); // TODO hack
        benchmarkPanel.clearSamples();
    }
    
    @Override
    protected void startAlgorithm()
    {
        Algorithm a = getSelectionAlgorithm();
        a.hideConfigurationFrame();
        Problem p = getSelectionProblem();
        int totalEvals = problemPanel.getTotalEvaluations();
        p.setMaximumEvaluations(totalEvals);
        clear();   
        
        // check that we really want to do this
        if(p instanceof HuygensServerBenchmarkProblem)
        {
            int v = JOptionPane.showConfirmDialog(this, "Are you sure you want to do a server benchmark?\n" +
                    "This requires you goto the competition site to prepare \n" +
                    "the server for resuts. Once you have done this, click yes to continue.",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION);
            
            if(v!=JOptionPane.OK_OPTION)
            {
                return;
            }  
        }
        
        try
        {
            ((HuygensProblem)p).prepareBeforeRun();
            
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        
        aList.setEnabled(false);
        pList.setEnabled(false);
        startButton.setEnabled(false);
        clearButton.setEnabled(false);
        configButton.setEnabled(false);
        problemConfigButton.setEnabled(false);
        
        if(p instanceof HuygensInternalBenchmarkProblem)
        {
            runInternalBenchmark((HuygensInternalBenchmarkProblem)p, a);
        }
        else if(p instanceof HuygensServerBenchmarkProblem)
        {
            runServerBenchmark((HuygensServerBenchmarkProblem)p, a);
        }
        else
        {
            a.execute(p, this);
        }
    }
       


    protected void runServerBenchmark(final HuygensServerBenchmarkProblem p, final Algorithm a)
    {
        int iterations = 1;
        int totalMoons = p.getBenchmarkTotalMoons();
        runBenchmark(p, a, totalMoons, iterations);
    }    
    
    public final static int [] INTERNAL_BENCHMARK_SEEDS = {1,2,3,4,5,6,7,8,9,10};
    
    protected void runInternalBenchmark(final HuygensInternalBenchmarkProblem p, final Algorithm a)
    {
        int iterations = p.getIterations();
        int totalMoons = INTERNAL_BENCHMARK_SEEDS.length;
        runBenchmark(p, a, totalMoons, iterations);
    }    
    
    protected void runBenchmark(
            final HuygensProblem p, final Algorithm a, 
            final int totalMoons, final int iterations)
    {
        int totalEvaluations = p.getBenchmarkProbesPerMoon();
        p.setMaximumEvaluations(totalEvaluations);                
      
        Runnable r = new Runnable()
        {
            public void run()
            {
                benchmarkPanel.prepareSamples(totalMoons, iterations);
                for (int i = 0; i < totalMoons; i++)
                {
                    if(p instanceof HuygensInternalBenchmarkProblem)
                    {
                        ((HuygensInternalBenchmarkProblem)p).setSeed(INTERNAL_BENCHMARK_SEEDS[i]);
                    }
                    
                    for (int j = 0; j < iterations; j++)
                    {
                        runBenchmarkIteration(p, a, i, j);
                    }
                }
                // finished
                benchmarkPanel.finished();
                searchComplete(null);
            }
        };          

        // run the thread
        new Thread(r).start();
    }
    protected void runBenchmarkIteration(HuygensProblem p, Algorithm a, int moon, int iteration)    
    {
        blindPlot.clearPoints();
        lineGraph.clear(p.getBenchmarkTotalMoons());
        p.resetEvaluations(); // reset
        a.clearBestEver();
        a.initialise(p);
        Solution best = a.executeAlgorithm(p);
        benchmarkPanel.update(best.getScore(), moon, iteration);
        double [] coord = best.getCoordinate();
        log.append("> "+p+", ["+a+"], "+best.getScore()+", x["+coord[0]+"], y["+coord[1]+"]\n");
    }
    
    
    
    
    @Override
    public void searchComplete(final Solution best)
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                Problem p = getSelectionProblem();
                if(p instanceof HuygensProblem)
                {
                    ((HuygensProblem)p).cleanupAfterRun();
                }
                
                aList.setEnabled(true);
                pList.setEnabled(true);
                startButton.setEnabled(true);
                clearButton.setEnabled(true);
                configButton.setEnabled(true);
                problemConfigButton.setEnabled(true);
                
                String algorithm = getSelectionAlgorithm().getName();
                String problem = p.getName();
                if(best != null)
                {
                    double [] coord = best.getCoordinate();
                    log.append("> "+problem+", ["+algorithm+"], "+best.getScore()+", x["+coord[0]+"], y["+coord[1]+"]\n");
                }
            }
        };
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch (Exception e)
        {}
    }
    
    @Override
    protected void selectProblem()
    {
        Problem p = getSelectionProblem();
        problemPanel.setProblem(p);
        blindPlot.setProblem(p);
        //threeDPlot.setProblem(p);
        
        if(p instanceof HuygensInternalBenchmarkProblem ||
           p instanceof HuygensServerBenchmarkProblem)
        {
            ((HuygensProblemPanel)problemPanel).disableSlider();
        }
        else
        {
            ((HuygensProblemPanel)problemPanel).enableSlider();
        }
    }
    
    @Override
    protected JPanel getProblemPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Function"));
        
        loadProblemList();
        problemConfigButton = new JButton("Config");
        problemConfigButton.addActionListener(this);
        pList = new JComboBox(problemList);        
        pList.addActionListener(this);
        p.add(new JScrollPane(pList));
        p.add(problemConfigButton);
        
        return p;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == problemConfigButton)
        {
            HuygensProblem p = (HuygensProblem) getSelectionProblem();
            p.showConfigurationFrame();
        }
        else if(src == startButton)
        {
            HuygensProblem p = (HuygensProblem) getSelectionProblem();
            p.hideConfigurationFrame();
        }
        
        // always
        super.actionPerformed(e);        
    }
}
