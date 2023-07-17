
package comopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import comopt.Algorithm;
import comopt.FileUtils;
import comopt.Problem;
import comopt.SearchCompletionNotify;
import comopt.Solution;
import comopt.gui.plots.AdjacencyMatrixPanel;
import comopt.gui.plots.LineGraph;
import comopt.gui.plots.TourDisplay;

/**
 * Type: MasterPanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MasterPanel extends JPanel
    implements ActionListener, SearchCompletionNotify
{
    public final static String ALGORITHM_LIST_FILE = "algorithm.properties";
    public final static String PROBLEM_LIST_FILE = "problems.properties";
    
    protected TourDisplay optimalResult;
    protected TourDisplay currentOptimal;
    protected AdjacencyMatrixPanel adjacencyMatrix;
    protected ProblemPanel problemPanel;
    protected LineGraph lineGraph;
    
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
        adjacencyMatrix = new AdjacencyMatrixPanel();
        optimalResult = new TourDisplay();
        lineGraph = new LineGraph();
        problemPanel = new ProblemPanel();        
        currentOptimal = new TourDisplay();
        
        // prepare south east
        JTabbedPane tp = new JTabbedPane();
        tp.add("Iteration Summary", lineGraph);
        tp.add("Current Best Tour", currentOptimal);
        tp.add("Adjacency Matrix", adjacencyMatrix);
               
        // preapre east
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(problemPanel, BorderLayout.NORTH);
        p2.add(optimalResult, BorderLayout.CENTER);      
        
        // prepare the lot
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(tp, BorderLayout.CENTER);
        p1.add(p2, BorderLayout.EAST);
        
        return p1;
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
        
        loadAlgorithmList();
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
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "TSP Problem"));
        
        loadProblemList();
        pList = new JComboBox(problemList);        
        pList.addActionListener(this);
        p.add(new JScrollPane(pList));
        
        Dimension d = new Dimension(200, p.getHeight());
        //p.setMinimumSize(d);
        p.setPreferredSize(d);
        
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
            Problem p = getSelectionProblem();
            int totalEvals = problemPanel.getTotalEvaluations();
            p.setMaximumEvaluations(totalEvals);
            adjacencyMatrix.clearPoints();
            lineGraph.clear(1000); // TODO hack
            a.execute(p, this);
        }
        else if(src == clearButton)
        {
            adjacencyMatrix.clearPoints();
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
            currentOptimal.setProblem(p);            
            adjacencyMatrix.setProblem(p);
            optimalResult.setProblem(p);
            optimalResult.setPermutation(p);
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
                
                adjacencyMatrix.repaint();
                
                String algorithm = getSelectionAlgorithm().getName();
                String problem = getSelectionProblem().getName();
                log.append("> "+problem+", ["+algorithm+"], "+best.getScore()+"\n");
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
        return (Problem) pList.getSelectedItem();
    }
    protected Algorithm getSelectionAlgorithm()    
    {
        return (Algorithm) aList.getSelectedItem();
    }
    
    
    
       
    protected void loadAlgorithmList()
    {
        String data = FileUtils.loadFile(ALGORITHM_LIST_FILE);
        if(data == null)
        {
            throw new RuntimeException("Unable to load algorithm list");
        }
        LinkedList<Algorithm> tmpList = new LinkedList<Algorithm>();
        String [] lines = data.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            line = line.trim();
            if(line.length()>0 && !line.startsWith("//") && !line.startsWith("#"))
            {
                try
                {
                    Algorithm a = (Algorithm) (Class.forName(line)).newInstance();
                    tmpList.add(a);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Unable to load algorithm class from properties file: " + line);
                }
            }
        }
        if(tmpList.isEmpty())
        {
            throw new RuntimeException("Unable to load any algorithms from algorithms list.");
        }
        algorithmList = tmpList.toArray(new Algorithm[tmpList.size()]);
        Arrays.sort(algorithmList);
        for (int i = 0; i < algorithmList.length; i++)
        {
            algorithmList[i].addListener(lineGraph);
            algorithmList[i].addListener(currentOptimal);
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
                    String problemFile = line.trim();
                    String solutionFile = problemFile.substring(0, problemFile.indexOf('.')) + ".opt.tour";
                    Problem p = new Problem(problemFile, solutionFile);
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
        for (int i = 0; i < problemList.length; i++)
        {
            problemList[i].initialise();
            problemList[i].addListener(adjacencyMatrix);
            
        }
        Arrays.sort(problemList);
    }
}
