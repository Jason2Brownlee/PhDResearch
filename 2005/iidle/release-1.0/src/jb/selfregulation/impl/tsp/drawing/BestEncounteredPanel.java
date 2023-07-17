
package jb.selfregulation.impl.tsp.drawing;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.problem.GetBestTSPTour;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;
import jb.selfregulation.impl.tsp.units.TSPUnitFactory;

/**
 * Type: BestEncounteredPanel<br/>
 * Date: 20/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BestEncounteredPanel extends JPanel
    implements LatticeStatusListener, ListSelectionListener
{
    protected TSPProblem problem;
    protected TSPUnitFactory unitFactory;
    
    protected TSPPanel solutionDrawer;
    protected JList bestList;
    protected DefaultListModel listModel;
    protected JTextArea bestResult;
    
    protected TSPUnit lastAddedBest;
    

    public void latticeChangedEvent(Lattice aLattice)
    {
        GetBestTSPTour v = new GetBestTSPTour();
        aLattice.getPerformRoughVisit(v);
        TSPUnit best = v.getBest();
        if(best != null)
        {
            boolean n = false;
            
            if(lastAddedBest==null || best.getTourLength() < lastAddedBest.getTourLength())
            {
                if(lastAddedBest == null)
                {
                   n = true;
                }
                
                // updates last best - have to duplicate                
                lastAddedBest = (TSPUnit) unitFactory.generateNewUnit(best);
                lastAddedBest.setTourLength(best.getTourLength());
                // add to list
                listModel.addElement(lastAddedBest);
            }
            
            if(n)
            {
                Runnable run = new Runnable()
                {
                    public void run()
                    {
                        updateBest(lastAddedBest);
                        setVisible(false);
                        setVisible(true);
                    }
                };
                SwingUtilities.invokeLater(run);

            }
        }
    }

    public String getBase()
    {
        return ".solutions";
    }

    public void loadConfig(String aBase, Properties prop)
    {}
    
    public void setup(SystemState aState)
    {
        problem = (TSPProblem) aState.problem;
        unitFactory = (TSPUnitFactory) aState.unitFactory;
        // add to problem panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS)).add(this);
        prepareGUI();
    }
    
    
    
    
    public void valueChanged(ListSelectionEvent e)
    {
        TSPUnit u = (TSPUnit) bestList.getSelectedValue();
        updateBest(u);
    }
    
    public void updateBest(TSPUnit u)
    {
        double score = u.getTourLength();        
        solutionDrawer.setPermutation(u.getData());
        solutionDrawer.repaint();
        bestResult.setText("Tour Length: "+ Math.round(score));
    }

    public void prepareGUI()
    {
        setName("Best Solution");
        
        solutionDrawer = new TSPPanel(problem.getCities());
        listModel = new DefaultListModel();
        bestList = new JList(listModel);
        bestList.addListSelectionListener(this);
        JScrollPane jsp1 = new JScrollPane(bestList);
        
        bestResult = new JTextArea();
        bestResult.setEditable(false);
        JScrollPane jsp2 = new JScrollPane(bestResult);
        
        setLayout(new BorderLayout());
        add(solutionDrawer, BorderLayout.CENTER);
        add(jsp1, BorderLayout.EAST);
        add(jsp2, BorderLayout.SOUTH);
    }    
}
