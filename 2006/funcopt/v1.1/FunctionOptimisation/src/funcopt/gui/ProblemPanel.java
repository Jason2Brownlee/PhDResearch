
package funcopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Problem;

/**
 * Type: ProblemPanel<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ProblemPanel extends JPanel
{
    protected Problem problem;
    
    protected JTextField nameField;
    protected JTextField minimiseField;
    protected JTextField evaluationsField;
    protected JComboBox minmaxField;
    protected JComboBox optimaField;
    
    public ProblemPanel()
    {
        prepareGui();
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
        int size = 20;
        nameField = new JTextField("", size);
        nameField.setEditable(false);
        evaluationsField = new JTextField("", size);
        evaluationsField.setEditable(false);
        minimiseField = new JTextField("", size);        
        minimiseField.setEditable(false);
        minmaxField = new JComboBox();
        optimaField = new JComboBox();
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(nameLabel);
        labelPane.add(evaluationsLabel);
        labelPane.add(minimiseLabel);        
        labelPane.add(minmaxLabel);
        labelPane.add(optimaLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(nameField);
        fieldPane.add(evaluationsField);
        fieldPane.add(minimiseField);
        fieldPane.add(minmaxField);
        fieldPane.add(optimaField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Function Details"));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
    }
    
    public void setProblem(Problem p)
    {
        problem = p;
        
        nameField.setText(p.getName());
        minimiseField.setText((p.isMinimise()) ? "Minimise" : "Maximise");
        evaluationsField.setText("" + p.getMaxEvaluations());
        
        // bounds
        double [][] minmax = p.getMinmax();
        minmaxField.removeAllItems();
        minmaxField.addItem("x=" + minmax[0][0] + " - " + minmax[0][1]);
        minmaxField.addItem("y=" + minmax[1][0] + " - " + minmax[1][1]);
        
        // optima
        double [][] optima = p.getGlobalOptima();
        optimaField.removeAllItems();
        if(optima != null)
        {
            optimaField.setEnabled(true);
            for (int i = 0; i < optima.length; i++)
            {
                optimaField.addItem(i + ") x="+optima[i][0] + " - y=" + optima[i][1]);
            }
        }
        else
        {
            optimaField.setEnabled(false);
        }
    }
}
