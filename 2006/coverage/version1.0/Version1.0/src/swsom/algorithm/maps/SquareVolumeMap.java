
package swsom.algorithm.maps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Type: SquareVolumeMap<br/>
 * Date: 23/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SquareVolumeMap extends OneDimensionalLatticeMap
{
    protected double initialFrontRadius;
    
    public void prepareSquares()
    {        
        for (int i = 0; i < vectors.length; i++)
        {
            vectors[i].setSquare(true);
            double [] squares = vectors[i].getSquareRadii();
            for (int j = 0; j < squares.length; j++)
            {
                squares[j] = initialFrontRadius;
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "Square Volume Map";
    }
    
    protected JTextField squareField;
    
    @Override
    public void initialise(Random r, double [][] sample)
    {
        super.initialise(r, sample);
        
        // squares
        try
        {
            initialFrontRadius = Double.parseDouble(squareField.getText());
        }
        catch (Exception e)
        {
            initialFrontRadius = 20;
            squareField.setText("" + initialFrontRadius);
        }
        finally
        {
            if(initialFrontRadius<0)
            {
                initialFrontRadius = 20;
                squareField.setText("" + initialFrontRadius);
            }
        }
        
        prepareSquares();
    }   
    
   
    @Override
    public JPanel getConfigurationPane()
    {
        // defaults
        numVerticies = 100;
        kNearestNeighbours = 3;
        rewireProbability = 0.0;
        shortcutProbability = 0.0;        
        initialFrontRadius = 20;
        
        // labels
        JLabel verticiesLabel = new JLabel("Total verticies:");
        JLabel neighboursLabel = new JLabel("Neighbour connections:");
        JLabel rewireProbLabel = new JLabel("Rewire probability:");
        JLabel shortcutProbLabel = new JLabel("Shortcut probability:");
        JLabel squareRadiusLabel = new JLabel("Initial square radius:");
        
        // fields
        verticiesField = new JTextField(Integer.toString(numVerticies), 10);
        neighboursField = new JTextField(Integer.toString(kNearestNeighbours), 10);
        rewireProbField = new JTextField(Double.toString(rewireProbability), 10);
        shortcutProbField = new JTextField(Double.toString(shortcutProbability), 10);
        squareField = new JTextField(Double.toString(initialFrontRadius), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(verticiesLabel);
        labelPane.add(neighboursLabel);
        labelPane.add(rewireProbLabel);
        labelPane.add(shortcutProbLabel);
        labelPane.add(squareRadiusLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(verticiesField);
        fieldPane.add(neighboursField);
        fieldPane.add(rewireProbField);
        fieldPane.add(shortcutProbField);
        fieldPane.add(squareField);

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
    public int getNumParameters()
    {
        return 5;
    }
}
