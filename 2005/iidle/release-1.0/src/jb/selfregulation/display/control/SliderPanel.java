
package jb.selfregulation.display.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;


/**
 * Type: SliderPanel<br/>
 * Date: 14/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SliderPanel extends JPanel
{
    public static enum MODE {HORIZONTAL, VERTICAL};
    
    protected final static NumberFormat format = new DecimalFormat();

    protected final JLabel currentValue;
    public final JSlider slider;
    protected JLabel label;
    protected MODE mode;
    
    public SliderPanel(MODE aMode, String aTitle, JSlider aSlider)
    {
        mode = aMode;
        label = new JLabel(aTitle);
        slider = aSlider;
        currentValue = new JLabel();            
        prepareGui();
        refreshCurrentValue(slider.getValue());
       
    }
    
    public SliderPanel(MODE aMode, String aTitle, int min, int max, int aValue)
    {
        mode = aMode;
        label = new JLabel(aTitle);
        slider = new JSlider(JSlider.HORIZONTAL, min, max, aValue);
        currentValue = new JLabel();            
        prepareGui();
        refreshCurrentValue(slider.getValue());
    }
    
    protected void prepareGui()
    {
        currentValue.setForeground(Color.GRAY);
        currentValue.setHorizontalAlignment(JLabel.CENTER);  
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.GRAY);            
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setLayout(new BorderLayout());
        
        if(mode == MODE.VERTICAL)
        {
            slider.setOrientation(JSlider.VERTICAL);
            add(label, BorderLayout.NORTH);
            add(slider, BorderLayout.CENTER);
            add(currentValue, BorderLayout.SOUTH);
        }
        else
        {
            slider.setOrientation(JSlider.HORIZONTAL);
            add(label, BorderLayout.WEST);
            add(slider, BorderLayout.CENTER);
            add(currentValue, BorderLayout.EAST);
        }
    }
    
    public void prepareSlider(ChangeListener listener)
    {
        prepareSlider(listener, 1, 10);
    }
    public void prepareSlider(ChangeListener listener, int minTick, int maxTick)
    {
        slider.setMinorTickSpacing(minTick);
        slider.setMajorTickSpacing(maxTick);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(listener);
        slider.setPaintTrack(true);
    }
    
    public void refreshCurrentValue()
    {
        refreshCurrentValue(slider.getValue());
    }
    
    public void refreshCurrentValue(int aValue)
    {
        currentValue.setText("( "+format.format(aValue)+" )");
    }
    public void refreshCurrentValue(double aValue)
    {
        currentValue.setText("( "+format.format(aValue)+" )");
    }
    public void refreshCurrentValue(long aValue)
    {
        currentValue.setText("( "+format.format(aValue)+" )");
    }
}
