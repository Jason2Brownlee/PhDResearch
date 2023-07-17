
package comopt.gui.plots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import comopt.AlgorithmIterationNotification;
import comopt.Problem;
import comopt.Solution;


/**
 * Type: TSPPanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TourDisplay extends JPanel implements AlgorithmIterationNotification
{
    protected TourPanel tourPanel;
    
    public TourDisplay()
    {
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        tourPanel = new TourPanel();
        setLayout(new BorderLayout());
        add(tourPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Optimal Tour"));
    }
    
    public void setProblem(Problem p)
    {
        tourPanel.setProblem(p);
        tourPanel.setTour(null);
        tourPanel.repaint();
    }
    public void setPermutation(Problem p)
    {
        tourPanel.setTour(p.getSolutionCityList());
        tourPanel.repaint();
    }
    
    public void setPermutation(Solution s)
    {
        tourPanel.setTour(s.getPermutation());
        tourPanel.repaint();
    }

    public void iterationComplete(Problem p, LinkedList<Solution> currentPop, Solution currentBest)
    {
//        Collections.sort(currentPop);
//        Solution best = currentPop.getFirst();
//        setPermutation(best);
        
            setPermutation(currentBest);
    }
    
    
}
