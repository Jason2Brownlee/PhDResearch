
package comopt.gui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Type: ComOptFrame<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ComOptFrame extends JFrame
{
    protected MasterPanel panel;
    
    public ComOptFrame()
    {
        super("Combinatorial Optimisation: TSP");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        prepareGui();  
//        setResizable(false);
    }
    
    public void centerScreen()
    {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        setVisible(true);
        requestFocus();
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
        panel = new MasterPanel();
        getContentPane().add(panel);
    }
    
    public static void main(String[] args)
    {
        ComOptFrame f = new ComOptFrame();
        f.makeVisible();
    }
}
