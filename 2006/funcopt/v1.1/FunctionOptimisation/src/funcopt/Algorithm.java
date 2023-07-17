
package funcopt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Type: Algorithm<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class Algorithm implements Comparable<Algorithm>
{
    protected volatile Solution bestEver;    
    protected LinkedList<AlgorithmIterationNotification> listeners;
    protected ConfigurationFrame configFrame;
    protected boolean hasFrame;
    
    public Algorithm()
    {
        listeners = new LinkedList<AlgorithmIterationNotification>();
        prepareConfigFrame();
    }
    
    protected void prepareConfigFrame()
    {
        JPanel configPane = getConfigurationPane();
        if(configPane != null)
        {
            configFrame = new ConfigurationFrame(configPane);
            hasFrame = true;
        }        
    }
    
    protected class ConfigurationFrame extends JFrame
    {
        protected JPanel internalConfig;
        
        public ConfigurationFrame(JPanel aInternalConfig)
        {
            super("Configuration - " + Algorithm.this.getName());     
            internalConfig = aInternalConfig;
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            prepareGui();  
            Dimension d = internalConfig.getPreferredSize();
            setSize(new Dimension(d.width+50, 80 + (20 * getNumParameters())));
        }
        
        public void centerScreen()
        {
            Dimension dim = getToolkit().getScreenSize();
            Rectangle abounds = getBounds();
            setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
            setVisible(true);
            requestFocus();
        }
        
        public void makeHidden()
        {
            Runnable run = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                }
            };
            SwingUtilities.invokeLater(run);
        }

        public void makeVisible()
        {
            Runnable run = new Runnable()
            {
                public void run()
                {
                    centerScreen();
                }
            };
            SwingUtilities.invokeLater(run);
        }
        
        protected void prepareGui()
        {
            internalConfig.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Configuration"));
            add(internalConfig);
        }
    }
    
    
    public void showConfigurationFrame()    
    {
        if(hasFrame)
        {
            configFrame.makeVisible();
        }
    }
    public void hideConfigurationFrame()    
    {
        if(hasFrame)
        {
            configFrame.makeHidden();
        }
    }
    
    
    
    protected void notifyListeners(double be, double currentBest, double currentWorst, double currentMean)
    {
        for(AlgorithmIterationNotification n : listeners)
        {
            n.iterationComplete(be, currentBest, currentWorst, currentMean);
        }
    }
    
    
    public void execute(final Problem aProblem, final SearchCompletionNotify n)
    {        
        initialise(aProblem);
        aProblem.resetEvaluations();
        bestEver = null;
        
        Runnable r = new Runnable()
        {
            public void run()
            {
                bestEver = executeAlgorithm(aProblem);
                n.searchComplete(bestEver);
            }
        };
        new Thread(r).start();
    }
    
    /**
     * must return the best solution found
     * @param aProblem
     * @return
     */
    protected abstract Solution executeAlgorithm(Problem aProblem);
    public abstract String getName();
    protected abstract JPanel getConfigurationPane();
    protected abstract int getNumParameters();
    public abstract void initialise(Problem p);

    public LinkedList<AlgorithmIterationNotification> getListeners()
    {
        return listeners;
    }
    
    public void addListener(AlgorithmIterationNotification l)
    {
        listeners.add(l);
    }  
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param o
     * @return
     */
    public int compareTo(Algorithm o)
    {
        return getName().compareTo(o.getName());
    }
}
