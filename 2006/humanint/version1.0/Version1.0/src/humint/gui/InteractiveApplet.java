
package humint.gui;

import javax.swing.JApplet;

/**
 * Type: InteractiveApplet<br/>
 * Date: 6/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class InteractiveApplet extends JApplet
{
    protected MasterPanel panel;    
    
    @Override
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
