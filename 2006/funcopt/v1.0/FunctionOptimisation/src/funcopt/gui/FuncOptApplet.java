package funcopt.gui;

import javax.swing.JApplet;

/**
 * Type: FuncOptApplet<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FuncOptApplet extends JApplet
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
