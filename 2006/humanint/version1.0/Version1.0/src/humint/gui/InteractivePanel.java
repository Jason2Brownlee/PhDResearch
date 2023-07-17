
package humint.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Type: InteractivePanel<br/>
 * Date: 5/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class InteractivePanel extends ProblemPanel
{
    public InteractivePanel()
    {
        this.addMouseListener(new InternalMouseListener());
    }    
    
    protected class InternalMouseListener extends MouseAdapter
    {        
        @Override
        public synchronized void mouseClicked(MouseEvent evt)
        {
            if(solution != null)
            {
                solution.setScore(1);
                repaint();
            }
        }        
    }    
}
