
package funcopt.problem;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Type: HuygensServerBenchmarkProblem<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensServerBenchmarkProblem extends HuygensProblem
{
    @Override
    public String getName()
    {
        return "Server Benchmark";
    }
    
    @Override
    protected double clientEvaluate(double [] v)
    {
        return client.evaluate(v[0], v[1]);
    }
    @Override
    protected double [] clientEvaluate(LinkedList<double[]> coords)
    {
        return client.batchProcessPoints(coords);
    }
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = -1;
        boulders = -1;
        email = DEFAULT_EMAIL;
        
        // labels
        JLabel emailLabel = new JLabel("Email address:");
        
        // fields
        emailField = new JTextField(email, 15);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(emailLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(emailField);

        // Put the panels in another panel, labels on left,
        // text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }
    
    @Override
    protected int getNumParameters()
    {
        return 1;
    }
    
    @Override
    public void prepareBeforeRun()
        throws Exception
    {  
        email = emailField.getText();
        if(email==null || (email=email.trim()).length()<1)
        {
            throw new RuntimeException("Invalid email address.");
        }
        
        try
        {
            // prepare the client
            client.startup(email);
        }
        catch (RuntimeException e)
        {
            throw new RuntimeException("Unable to prepare HTTP client: " + e.getMessage(),e);
        }        
    }
}
