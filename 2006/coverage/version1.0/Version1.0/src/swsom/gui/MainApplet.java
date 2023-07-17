
package swsom.gui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Type: MainApplet<br/>
 * Date: 23/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MainApplet extends JApplet
{
    protected MainPane panel;
    
    @Override
    public void init()
    {
        makeVisible();
    }    

    public void makeVisible()
    {
        Runnable run = new Runnable()
        {
            public void run()
            {
                prepareGui();
                setVisible(true);
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    protected void prepareGui()
    {
        panel = new MainPane();
        getContentPane().add(panel);
    }
}
