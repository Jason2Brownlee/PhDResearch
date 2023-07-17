
package jb.selfregulation.impl.proteinfolding.drawing;

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
import jb.selfregulation.impl.proteinfolding.problem.HPModelEval;
import jb.selfregulation.impl.proteinfolding.problem.InvalidModelException;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnit;
import jb.selfregulation.impl.proteinfolding.units.ProteinFoldingUnitFactory;

/**
 * Type: BestEncounteredPanel<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BestEncounteredPanel extends JPanel
    implements LatticeStatusListener, ListSelectionListener
{
    protected HPModelEval problem;
    protected ProteinFoldingUnitFactory unitFactory;
    
    protected SolutionDrawer solutionDrawer;
    protected JList bestList;
    protected DefaultListModel listModel;
    protected JTextArea bestResult;
    
    protected ProteinFoldingUnit lastAddedBest;
    

    public void latticeChangedEvent(Lattice aLattice)
    {
        GetBestCellVisitor v = new GetBestCellVisitor();
        aLattice.getPerformRoughVisit(v);
        ProteinFoldingUnit best = v.getBest();
        if(best != null)
        {
            boolean n = false;
            
            if(lastAddedBest==null || best.getScore() > lastAddedBest.getScore())
            {
                if(lastAddedBest == null)
                {
                   n = true;
                }
                
                // updates last best - have to duplicate                
                lastAddedBest = (ProteinFoldingUnit) unitFactory.generateNewUnit(best);
                lastAddedBest.setScore(best.getScore());
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
        problem = (HPModelEval) aState.problem;
        unitFactory = (ProteinFoldingUnitFactory) aState.unitFactory;
        // add to problem panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS)).add(this);
        prepareGUI();
    }
    
    
    
    
    public void valueChanged(ListSelectionEvent e)
    {
        ProteinFoldingUnit u = (ProteinFoldingUnit) bestList.getSelectedValue();
        updateBest(u);
    }
    
    public void updateBest(ProteinFoldingUnit u)
    {
        byte [] model = u.getModel();
        byte [][] map = null;
        try
        {
            map = problem.modelToMap(model);
        }
        catch (InvalidModelException e)
        {
            map = e.getModel();
        }
        
        float realScore = u.getScore() - problem.getDataset().length;
        
        solutionDrawer.updateMap(map, model);
        bestResult.setText("Path: " + problem.modelToString(model)
                +"\nScore: "+realScore + " ("+u.getScore()+")");
        

    }

    public void prepareGUI()
    {
        setName("Best Solution");
        
        solutionDrawer = new SolutionDrawer(problem);
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
