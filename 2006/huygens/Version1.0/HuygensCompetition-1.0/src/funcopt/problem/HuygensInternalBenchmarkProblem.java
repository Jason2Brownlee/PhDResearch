
package funcopt.problem;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Type: HuygensInternalBenchmarkProblem<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensInternalBenchmarkProblem extends HuygensProblem
{
    public final static int DEFAULT_ITERATIONS = 3;
    
   
    protected int iterations;
    
    protected JTextField iterationsField;
    
   

    @Override
    public String getName()
    {
        return "Internal Benchmark";
    }
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = 1;
        boulders = 20;
        email = DEFAULT_EMAIL;
        iterations = DEFAULT_ITERATIONS;
        
        // labels
        JLabel iterationsLabel = new JLabel("Iterations per moon:");
        JLabel emailLabel = new JLabel("Email address:");
        
        // fields
        iterationsField = new JTextField(Integer.toString(iterations), 10);
        emailField = new JTextField(email, 15);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(iterationsLabel);
        labelPane.add(emailLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(iterationsField);
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
        return 2;
    }
    
    @Override
    public void prepareBeforeRun()
        throws Exception
    {             
        try
        {
            iterations = Integer.parseInt(iterationsField.getText());
        }
        catch(Exception e)
        {
            iterations = DEFAULT_ITERATIONS;
            iterationsField.setText(Integer.toString(iterations));
        }      
        finally
        {
            if(iterations<=0)
            {
                iterations = DEFAULT_ITERATIONS;
                iterationsField.setText(Integer.toString(iterations));
            }
        }
        
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
    
    public void setSeed(int aSeed)
    {
        seed = aSeed;
    }

    public int getIterations()
    {
        return iterations;
    }    
}
