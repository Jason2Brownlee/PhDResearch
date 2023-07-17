
package funcopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import funcopt.Problem;

/**
 * Type: ProblemPanel<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProblemPanel extends JPanel implements ChangeListener
{
    public final static int MIN_EVALUATIONS = 1000;
    
    protected Problem problem;
    
    protected JTextField nameField;
    protected JTextField minimiseField;
    protected JSlider evaluationsField;
    protected JComboBox minmaxField;
    protected JComboBox optimaField;
    
    public ProblemPanel()
    {
        prepareGui();
    }
    
    protected JSlider prepareEvaluationsSlider()
    {
        JSlider s = new JSlider(0, 50000);
        s.setOrientation(JSlider.HORIZONTAL);
        s.setMinorTickSpacing(1000);
        s.setMajorTickSpacing(10000);
        s.setValue(10000);
        s.setSnapToTicks(true);
        s.setPaintTicks(true);        
        s.addChangeListener(this);
        s.setPaintLabels(true);        
        return s;
    }
    
    protected void prepareGui()
    {
        // labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel evaluationsLabel = new JLabel("Evaluations:");
        JLabel minimiseLabel = new JLabel("Extrema:");
        JLabel minmaxLabel = new JLabel("Bounds:");
        JLabel optimaLabel = new JLabel("Optima:");
        
        // fields
        int size = 25;
        nameField = new JTextField("", size);
        nameField.setEditable(false);
        evaluationsField = prepareEvaluationsSlider();
        
        minimiseField = new JTextField("", size);        
        minimiseField.setEditable(false);
        minmaxField = new JComboBox();
        optimaField = new JComboBox();
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(nameLabel);
        //labelPane.add(evaluationsLabel);
        labelPane.add(minimiseLabel);        
        labelPane.add(minmaxLabel);
        labelPane.add(optimaLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(nameField);
        //fieldPane.add(evaluationsField);
        fieldPane.add(minimiseField);
        fieldPane.add(minmaxField);
        fieldPane.add(optimaField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        
//        JPanel l1 = new JPanel(new BorderLayout());
//        l1.add(evaluationsLabel);
//        JPanel f1 = new JPanel(new BorderLayout());
//        f1.add(evaluationsField);
        JPanel c1 = new JPanel(new BorderLayout());
        c1.add(evaluationsLabel, BorderLayout.WEST);
        c1.add(evaluationsField, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Function Details"));
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        add(c1, BorderLayout.SOUTH);
    }
    
    public void setProblem(Problem p)
    {
        problem = p;
        
        nameField.setText(p.getName());
        minimiseField.setText((p.isMinimise()) ? "Minimise" : "Maximise");
        //evaluationsField.setText("" + p.getMaxEvaluations());
        
        // bounds
        double [][] minmax = p.getMinmax();
        minmaxField.removeAllItems();
        minmaxField.addItem("x=[" + minmax[0][0] + "] to [" + minmax[0][1]+"]");
        minmaxField.addItem("y=[" + minmax[1][0] + "] to [" + minmax[1][1]+"]");
        
        // optima
        double [][] optima = p.getGlobalOptima();
        optimaField.removeAllItems();
        if(optima != null)
        {
            optimaField.setEnabled(true);
            for (int i = 0; i < optima.length; i++)
            {
                double v = p.unCountedCost(optima[i]);
                optimaField.addItem("x=["+optima[i][0] + "], y=[" + optima[i][1]+"] ("+v+")");
            }
        }
        else
        {
            optimaField.setEnabled(false);
        }
    }
    
    public int getTotalEvaluations()
    {
        return evaluationsField.getValue();
    }

    
    public void stateChanged(ChangeEvent e)
    {
        Object src = e.getSource();
        if(src == evaluationsField)
        {
            int v = evaluationsField.getValue();
            if(v < getMinEvaluations())
            {
                evaluationsField.setValue(getMinEvaluations());
            }
        }
        
    }
    
    protected int getMinEvaluations()
    {
        return MIN_EVALUATIONS;
    }
}
