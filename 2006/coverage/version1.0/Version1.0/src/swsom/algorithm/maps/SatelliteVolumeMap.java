
package swsom.algorithm.maps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.VectorConnection;

/**
 * Type: SatelliteVolumeMap<br/>
 * Date: 23/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SatelliteVolumeMap extends OneDimensionalLatticeMap
{
    protected int totalSatellites;   
    
    public void prepareSatellites()
    {        
        // need atleast three to create a concave region
        if(totalSatellites > 2)
        {
            for (int i = 0; i < vectors.length; i++)
            {
                ExemplarVector self = vectors[i];
                // create satellites
                ExemplarVector [] satellites = new ExemplarVector[totalSatellites];
                for (int j = 0; j < satellites.length; j++)
                {                   
                    // at parent origin
                    satellites[j] = new ExemplarVector(self);
                }
                // store satellites
                self.setSatellites(satellites);
                // connect satellites
                for (int j = 1; j < satellites.length; j++)
                {
                    VectorConnection edge = new VectorConnection(satellites[j-1], satellites[j]);
                    satellites[j-1].addConnection(edge);
                    satellites[j].addConnection(edge);
                }
                // last two
                VectorConnection edge = new VectorConnection(satellites[0], satellites[satellites.length-1]);
                satellites[0].addConnection(edge);
                satellites[satellites.length-1].addConnection(edge); 
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "Satellite Map";
    }
    
    protected JTextField satellitesField;
    
    @Override
    public void initialise(Random r, double [][] sample)
    {
        super.initialise(r, sample);
        
        // numVertitotalSatellitescies
        try
        {
            totalSatellites = Integer.parseInt(satellitesField.getText());
        }
        catch (Exception e)
        {
            totalSatellites = 4;
            satellitesField.setText("" + totalSatellites);
        }
        finally
        {
            if(totalSatellites<=0)
            {
                totalSatellites = 4;
                satellitesField.setText("" + totalSatellites);
            }
        }
        
        prepareSatellites();
    }   
    
   
    @Override
    public JPanel getConfigurationPane()
    {
        // defaults
        numVerticies = 100;
        kNearestNeighbours = 3;
        rewireProbability = 0.0;
        shortcutProbability = 0.0;        
        totalSatellites = 4;
        
        // labels
        JLabel verticiesLabel = new JLabel("Total verticies:");
        JLabel neighboursLabel = new JLabel("Neighbour connections:");
        JLabel rewireProbLabel = new JLabel("Rewire probability:");
        JLabel shortcutProbLabel = new JLabel("Shortcut probability:");
        JLabel satellitesProbLabel = new JLabel("Total Satellites:");
        
        // fields
        verticiesField = new JTextField(Integer.toString(numVerticies), 10);
        neighboursField = new JTextField(Integer.toString(kNearestNeighbours), 10);
        rewireProbField = new JTextField(Double.toString(rewireProbability), 10);
        shortcutProbField = new JTextField(Double.toString(shortcutProbability), 10);
        satellitesField = new JTextField(Integer.toString(totalSatellites), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(verticiesLabel);
        labelPane.add(neighboursLabel);
        labelPane.add(rewireProbLabel);
        labelPane.add(shortcutProbLabel);
        labelPane.add(satellitesProbLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(verticiesField);
        fieldPane.add(neighboursField);
        fieldPane.add(rewireProbField);
        fieldPane.add(shortcutProbField);
        fieldPane.add(satellitesField);

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
