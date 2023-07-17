package swsom.algorithm.algorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.Edge;
import swsom.algorithm.ExemplarVector;
import swsom.algorithm.MathUtils;

/**
 * Type: SatelliteConnectivityBasedSOMAlgorithm <br>
 * Date: 24/02/2005 <br>
 * <br>
 * 
 * Description:
 * 
 * @author Jason Brownlee
 */
public class SatelliteConnectivityBasedSOMAlgorithm extends ConnectivityBasedSOM
{
    protected double satelliteUpdateRate;

    protected JTextField satelliteLearningRate;
    
    @Override
    protected void updateVector(
            double[][] problemBoundary, 
            ExemplarVector aCodebookVector, 
            double[] aDataInstance, 
            double aLearningRate)
    {
        ExemplarVector[] satellites = aCodebookVector.getSatellites();

        double[] original = new double[aCodebookVector.getData().length];
        System.arraycopy(aCodebookVector.getData(), 0, original, 0, aCodebookVector.getData().length);

        // update the centroid
        updateVector(aCodebookVector.getData(), aDataInstance, aLearningRate, problemBoundary);

        // adjust all satellites with the exemplar
        for (int i = 0; i < satellites.length; i++)
        {
            updateVector(satellites[i].getData(), original, aDataInstance, aLearningRate, problemBoundary);
        }                
        
        // adjust the satellites
//        System.arraycopy(aCodebookVector.getData(), 0, original, 0, aCodebookVector.getData().length);
        ExemplarVector bestMatchingSattelite = getBestMatchingSatellite(satellites, aDataInstance);
        expandSatelliteFrontier(bestMatchingSattelite, original, aDataInstance, satelliteUpdateRate);
        

        // adjust the bests neighbours
//        LinkedList<ExemplarVector> list = getNonSelfVertices(bestMatchingSattelite);
//        for (ExemplarVector v : list)
//        {
//            expandSatelliteFrontier(v, original, aDataInstance, satelliteUpdateRate*0.5);
//        }
    }
    
    
    protected ExemplarVector getBestMatchingSatellite(
            ExemplarVector [] satellites, 
            double [] aSample)
    {
        double best = MathUtils.distanceEuclidean(satellites[0].getData(), aSample);
        int bestIndex = 0;
        for (int i = 1; i < satellites.length; i++)
        {
            double d = MathUtils.distanceEuclidean(satellites[i].getData(), aSample);
            if (d < best)
            {
                best = d;
                bestIndex = i;
            }
        }
        
        return satellites[bestIndex];
    }
    
    protected void expandSatelliteFrontier(
            ExemplarVector aSatellite, 
            double [] aExemplarData,
            double [] aSample,
            double aLearningRate)
    {
        // select an axis to work on 
        double [] bmuData = aSatellite.getData();
        double diffX = bmuData[0] - aSample[0];
        double diffY = bmuData[1] - aSample[1];
        
        // check if all work is on the X axis
        if(Math.abs(diffX) > Math.abs(diffY))
        {   
            // orientate to centroid
            double diffC = aExemplarData[0] - bmuData[0];
            
            // check for case where sample is to the right of satellite
            // and satellite is to the right of the node
            if(diffX<0 && diffC<0)
            {
                bmuData[0] += (aLearningRate * Math.abs(diffX));
            }
            // check for case where sample is to the left of the satellite
            // and the satellite is to the left of the node
            if(diffX > 0 && diffC > 0)
            {
                bmuData[0] -= (aLearningRate * Math.abs(diffX));
            }
        }
        // check if all work is on the Y axis
        else if(Math.abs(diffX) < Math.abs(diffY))
        {
            // orientate to centroid
            double diffC = aExemplarData[1] - bmuData[1];
            
            // check for case where sample is above the satellite
            // and satellite is above the node
            if(diffY<0 && diffC<0)
            {
                bmuData[1] += (aLearningRate * Math.abs(diffY));
            }
            // check for case where sample is to the left of the satellite
            // and the satellite is to the left of the node
            if(diffY > 0 && diffC > 0)
            {
                bmuData[1] -= (aLearningRate * Math.abs(diffY));
            }
        }
    }
    
    

    protected LinkedList<ExemplarVector> getNonSelfVertices(ExemplarVector self)
    {
        LinkedList<ExemplarVector> list = new LinkedList<ExemplarVector>();

        Edge[] edges = self.getEdges();
        for (int i = 0; i < edges.length; i++)
        {
            if (edges[i].getVertex1() == self)
            {
                if (!list.contains(edges[i].getVertex2()))
                {
                    list.add((ExemplarVector) edges[i].getVertex2());
                }
            }
            else
            {
                if (!list.contains(edges[i].getVertex1()))
                {
                    list.add((ExemplarVector) edges[i].getVertex1());
                }
            }
        }

        return list;
    }

    protected void updateVector(double[] aExemplar, double[] aSample, double aLearningRate, double[][] boundary)
    {
        // update all attributes
        for (int i = 0; i < aExemplar.length; i++)
        {
            // calculate the delta (weighted difference) and update codebook
            // vector
            aExemplar[i] += (aLearningRate * (aSample[i] - aExemplar[i]));
        }
        // ensure point still exists within the domain
        wrapToBoundary(boundary, aExemplar);
    }

    protected void updateVector(double[] aExemplar, double[] aOriginalExemplar, double[] aSample, double aLearningRate, double[][] boundary)
    {
        // update all attributes
        for (int i = 0; i < aExemplar.length; i++)
        {
            // calculate the delta (weighted difference) and update codebook
            // vector
            aExemplar[i] += (aLearningRate * (aSample[i] - aOriginalExemplar[i]));
        }
        // ensure point still exists within the domain
        wrapToBoundary(boundary, aExemplar);
    }
    
    @ Override
    public String toString()
    {
        return "Satellite-base Volume SOM";
    }
    
    
    
    
    @Override
    public void initialise()
    {
        super.initialise();
        
        // learningRateField
        try
        {
            satelliteUpdateRate = Double.parseDouble(satelliteLearningRate.getText());
        }
        catch (Exception e)
        {
            satelliteUpdateRate = 0.3;
            satelliteLearningRate.setText("" + satelliteUpdateRate);
        }
        finally
        {
            if(satelliteUpdateRate<=0)
            {
                satelliteUpdateRate = 0.3;
                satelliteLearningRate.setText("" + satelliteUpdateRate);
            }
        }
    }
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        initialLearningRate = 0.3;
        initialNeighbourhoodSize = 10;
        totalIterations = DEFAULT_ITERATIONS;
        satelliteUpdateRate = 0.3;
        
        // labels
        JLabel learningRateLabel = new JLabel("Learning rate:");
        JLabel neighbourhoodSizeLabel = new JLabel("Neighbourhood size:");
        JLabel iterationsLabel = new JLabel("Algorithm iterations:");
        JLabel satelliteUpdateLabel = new JLabel("Satellite learning rate:");
        
        // fields
        learningRateField = new JTextField(Double.toString(initialLearningRate), 10);
        neighbourhoodSizeField = new JTextField(Double.toString(initialNeighbourhoodSize), 10);
        iterationsField = new JTextField(Integer.toString(totalIterations), 10);
        satelliteLearningRate = new JTextField(Double.toString(satelliteUpdateRate), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(learningRateLabel);
        labelPane.add(neighbourhoodSizeLabel);
        labelPane.add(iterationsLabel);
        labelPane.add(satelliteUpdateLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(learningRateField);
        fieldPane.add(neighbourhoodSizeField);
        fieldPane.add(iterationsField);
        fieldPane.add(satelliteLearningRate);

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
        return 4;
    }
}
