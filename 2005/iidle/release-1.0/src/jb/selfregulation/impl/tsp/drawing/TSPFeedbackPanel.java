package jb.selfregulation.impl.tsp.drawing;

import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;

/**
 * Type: TSPFeedbackPanel<br/>
 * Date: 7/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPFeedbackPanel extends JPanel implements Configurable
{
    protected TSPProblem problem;
    protected int numToDisplay;
    protected volatile long waitTime;    
    protected TSPPanel [] panelList;
    
    
    public String getBase()
    {        
        return ".feedback";
    }
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase(); 
        numToDisplay = Integer.parseInt(prop.getProperty(b + ".total"));
        waitTime = Long.parseLong(prop.getProperty(b + ".time"));
    }
    public void setup(SystemState aState)
    {
        problem = (TSPProblem) aState.problem;
        // add self to user stimulation panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_FEEDBACK_PANELS)).add(this); 
        // prepare the gui
        prepareGui();
    }
    
    

    protected void prepareGui()
    {
        setName("Visual Feedback");
        prepareDisplay();
    }

    protected void prepareDisplay()
    {        
        int rows = 0;
        int cols = 0;

        if (numToDisplay == 1)
        {
            rows = 1;
            cols = 1;
        }
        else if(numToDisplay == 2)        
        {
            rows = 1;
            cols = 2;
        }
        else if(numToDisplay == 4)
        {
            rows = 2;
            cols = 2;
        }
        else if(numToDisplay == 9)
        {
            rows = 3;
            cols = 3;
        }
        else
        {
            throw new RuntimeException("Invalid number of tours to display: "+numToDisplay+", expect one of (1,2,4,9).");
        }

        GridLayout layout = new GridLayout(rows, cols);
        setLayout(layout);
        
        // prepare panel list
        panelList = new TSPPanel[numToDisplay];
        for (int i = 0; i < panelList.length; i++)
        {
            panelList[i] = new TSPPanel(problem.getCities());
            add(panelList[i]);
        }
    }

    
    public void runFeedbackCycle(Cell aCell)
    {
        // ensure that there is something to display
        LinkedList<Unit> units = aCell.getTail().getUnits();
        if(!units.isEmpty())
        {
            LinkedList<Unit> tmp = new LinkedList<Unit>();
            tmp.addAll(units);
            
            // process the tail
            while(!tmp.isEmpty())
            {
                // display a batch
                displayBatch(tmp);
                // remove the displayed units
                removeBatch(tmp);
            }
        }       
    }
    
    protected void removeBatch(LinkedList<Unit> list)
    {
        for (int i = 0; !list.isEmpty() && i < numToDisplay; i++)
        {
            list.removeFirst();
        }
        
        // clear the screen
        for (int i = 0; i < panelList.length; i++)
        {
            panelList[i].clearUnit();
        }
    }
    
    protected void displayBatch(LinkedList<Unit> list)
    {
        int totalDisplayed = 0;
        for (int i = 0; i<list.size() && i < numToDisplay; i++, totalDisplayed++)
        {
            TSPUnit unit = (TSPUnit) list.get(i);
            // display
            panelList[i].setUnit(unit);            
        }        
        
//        System.out.println("displayed: " + totalDisplayed + " / " + list.size());
        
        this.repaint();
        
        // wait for a time for feedback
        synchronized(this)
        {
            try
            {
                wait(waitTime);
            }
            catch (InterruptedException e)
            {}
        }
    }

    public long getWaitTime()
    {
        return waitTime;
    }

    public void setWaitTime(long a)
    {
        synchronized(this)
        {
            this.waitTime = a;
            this.notify(); // immediate effect
        }
    }
}
