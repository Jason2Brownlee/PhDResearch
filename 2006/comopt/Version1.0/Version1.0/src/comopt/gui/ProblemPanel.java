
package comopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import comopt.Problem;

/**
 * Type: ProblemPanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProblemPanel extends JPanel implements ChangeListener
{
    public final static int MIN_EVALUATIONS = 2000;
    
    protected Problem problem;
    
    protected JTextField nameField;
    protected JTextField distanceField;
    protected JTextField optimalTourField;
    protected JTextField totalCititesField;
    
    protected JSlider evaluationsField;

    
    public ProblemPanel()
    {
        prepareGui();
    }
    
    protected void prepareGui()
    {
        // labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel evaluationsLabel = new JLabel("Evaluations:");
        JLabel distanceLabel = new JLabel("Distance Type:");
        JLabel optimaLabel = new JLabel("Optimal Tour:");
        JLabel totalCitiesLabel = new JLabel("Total Citites:");
        
        // fields
        int size = 25;
        nameField = new JTextField("", size);
        nameField.setEditable(false);
        distanceField = new JTextField("", size);
        distanceField.setEditable(false);
        optimalTourField = new JTextField("", size);
        optimalTourField.setEditable(false);
        totalCititesField = new JTextField("", size);
        totalCititesField.setEditable(false);
        
        evaluationsField = new JSlider(0, 50000);
        evaluationsField.setOrientation(JSlider.HORIZONTAL);
        evaluationsField.setMinorTickSpacing(2000);
        evaluationsField.setMajorTickSpacing(10000);
        evaluationsField.setValue(10000);
        evaluationsField.setSnapToTicks(true);
        evaluationsField.setPaintTicks(true);
        evaluationsField.setPaintLabels(true);
        evaluationsField.addChangeListener(this);       
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(nameLabel);
        labelPane.add(distanceLabel);        
        labelPane.add(optimaLabel);
        labelPane.add(totalCitiesLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(nameField);
        fieldPane.add(distanceField);
        fieldPane.add(optimalTourField);
        fieldPane.add(totalCititesField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
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
        distanceField.setText(p.getDistanceType().name());
        optimalTourField.setText(""+p.getSolutionTourLength());
        totalCititesField.setText(""+p.getTotalCities());
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
            if(v < MIN_EVALUATIONS)
            {
                evaluationsField.setValue(MIN_EVALUATIONS);
            }
        }        
    }
}
