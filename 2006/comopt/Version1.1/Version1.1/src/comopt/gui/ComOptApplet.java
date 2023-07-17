package comopt.gui;

import javax.swing.JApplet;

/**
 * Type: ComOptApplet<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ComOptApplet extends JApplet
{
    protected MasterPanel panel;

    public void init()
    {
        prepareGui();
    }

    protected void prepareGui()
    {
        panel = new MasterPanel();
        getContentPane().add(panel);
    }
}
