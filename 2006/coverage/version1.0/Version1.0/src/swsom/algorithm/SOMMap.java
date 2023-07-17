
package swsom.algorithm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Type: SOMMap<br>
 * Date: 24/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public abstract class SOMMap implements FeatureMap
{
    protected ConfigurationFrame configFrame;
    protected boolean hasFrame;
    
    public SOMMap()
    {
        prepareConfigFrame();
    }
    
    
    public abstract ExemplarVector getBestMatchingUnit(double [] v);
    
    public abstract int getNumParameters();
    public abstract JPanel getConfigurationPane();
    public abstract void initialise(Random r, double [][] sample);

    
    public void prepareConfigFrame()
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
            super("Configuration - " + SOMMap.this.toString());     
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
}
