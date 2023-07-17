
package funcopt.gui;

import javax.swing.JSlider;

/**
 * Type: HuygensProblemPanel<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensProblemPanel extends ProblemPanel
{

    @Override
    protected JSlider prepareEvaluationsSlider()
    {
        JSlider s = new JSlider(0, 1000);
        s.setOrientation(JSlider.HORIZONTAL);
        s.setMinorTickSpacing(50);
        s.setMajorTickSpacing(200);
        s.setValue(1000);
        s.setSnapToTicks(true);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        s.addChangeListener(this);
        
        return s;
    }
    
    @Override
    protected int getMinEvaluations()
    {
        return 50;
    }
    
    public void disableSlider()
    {
        evaluationsField.setEnabled(false);
    }
    public void enableSlider()
    {
        evaluationsField.setEnabled(true);
    }
}
