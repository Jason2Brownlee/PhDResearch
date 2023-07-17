
package funcopt.problem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import funcopt.Problem;
import funcopt.Solution;

/**
 * Type: HuygensProblem<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HuygensProblem extends Problem
{
    protected final static int DEFAULT_BOULDERS = 20;
    protected final static int DEFAULT_SEED = 1;
    protected final static String DEFAULT_EMAIL = "";
    
    protected HuygensClient client;
    
    protected int boulders;
    protected int seed;
    protected String email;
    
    protected JTextField seedField;
    protected JTextField bouldersField;
    protected JTextField emailField;
    
    protected ConfigurationFrame configFrame;
    protected boolean hasFrame;
    
    public HuygensProblem()
    {
        prepareConfigFrame();
    }
    
    @Override
    public boolean isToroidal()
    {
        return true;
    }
    
    @Override
    public void initialise()
    {
        super.initialise();
        dimensions = 2;
        maxEvaluations = 1000;
        client = new HuygensClient();
        client.loadProperties();
    }    
    
    @Override
    public void cost(LinkedList<Solution> ss)
    {
        int remaining = (maxEvaluations-evaluationCount);
        int total = Math.min(remaining, ss.size());     
        total = Math.max(total, 0); // remaining could be negative, this is a fix     
        LinkedList<double[]> coords = new LinkedList<double[]>();
        // ensure we only evaluate within the limits
        for (int i = 0; i < ss.size(); i++)
        {
            if(i < total)
            {
                double [] c = ss.get(i).getCoordinate();
                checkSafety(c);
                coords.add(c);
            }
            else
            {
                ss.get(i).evaluated(Double.NaN);
            }
        }
        if(total > 0)
        {
            // get the scores
            double [] scores = clientEvaluate(coords);
            // set scores
            for (int i = 0; i < scores.length; i++)
            {
                ss.get(i).evaluated(scores[i]);
                notifyListeners(ss.get(i).getCoordinate(), scores[i]);
            }
        }
        // update total evaluations
        evaluationCount += ss.size();
    }
    
    protected double clientEvaluate(double [] v)
    {
        return client.evaluate(boulders, seed, v[0], v[1]);
    }
    protected double [] clientEvaluate(LinkedList<double[]> coords)
    {
        return client.batchProcessPoints(boulders, seed, coords);
    }
    

    @Override
    protected double calculateCost(double[] v)
    {
        return clientEvaluate(v);
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{0,1},{0,1}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return null;
    }

    @Override
    protected boolean isMinimiseProblem()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Huygens Any Moon";
    }
    
    
    protected void prepareConfigFrame()
    {
        JPanel configPane = getConfigurationPane();
        if(configPane != null)
        {
            configFrame = new ConfigurationFrame(configPane);
            hasFrame = true;
        }        
    }
   
    protected class ConfigurationFrame extends JFrame
    {
        protected JPanel internalConfig;
        
        public ConfigurationFrame(JPanel aInternalConfig)
        {
            super("Configuration - " + HuygensProblem.this.getName());     
            internalConfig = aInternalConfig;
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            prepareGui();  
            Dimension d = internalConfig.getPreferredSize();
            setSize(new Dimension(d.width+50, 80 + (20 * getNumParameters())));
        }
        
        public void centerScreen()
        {
            Dimension dim = getToolkit().getScreenSize();
            Rectangle abounds = getBounds();
            setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
            setVisible(true);
            requestFocus();
        }
        
        public void makeHidden()
        {
            Runnable run = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                }
            };
            SwingUtilities.invokeLater(run);
        }

        public void makeVisible()
        {
            Runnable run = new Runnable()
            {
                public void run()
                {
                    centerScreen();
                }
            };
            SwingUtilities.invokeLater(run);
        }
        
        protected void prepareGui()
        {
            internalConfig.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Configuration"));
            add(internalConfig);
        }
    }
    
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = DEFAULT_SEED;
        boulders = DEFAULT_BOULDERS;
        email = DEFAULT_EMAIL;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel bouldersLabel = new JLabel("Number of boulders:");
        JLabel emailLabel = new JLabel("Email address:");
        
        // fields
        seedField = new JTextField(Integer.toString(seed), 10);
        bouldersField = new JTextField(Integer.toString(boulders), 10);
        bouldersField.setEditable(false);
        emailField = new JTextField(email, 15);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(bouldersLabel);
        labelPane.add(emailLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(bouldersField);
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
    
    protected int getNumParameters()
    {
        return 3;
    }
    
    public void showConfigurationFrame()    
    {
        if(hasFrame)
        {
            configFrame.makeVisible();
        }
    }
    public void hideConfigurationFrame()    
    {
        if(hasFrame)
        {
            configFrame.makeHidden();
        }
    }
    
    public void cleanupAfterRun()
    {
        client.shutdown();
    }
    
    public void prepareBeforeRun()
        throws Exception
    {             
        try
        {
            seed = Integer.parseInt(seedField.getText());
        }
        catch(Exception e)
        {
            seed = DEFAULT_SEED;
            seedField.setText(Integer.toString(seed));
        }
        try
        {
            boulders = Integer.parseInt(bouldersField.getText());
        }
        catch(Exception e)
        {
            boulders = DEFAULT_BOULDERS;
            bouldersField.setText(Integer.toString(boulders));
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
    
    public int getBenchmarkTotalMoons()
    {
        return client.getServerBenchmarkTotalMoons();
    }
    public int getBenchmarkProbesPerMoon()
    {
        return client.getServerBenchmarkProbesPerMoon();
    }
}
