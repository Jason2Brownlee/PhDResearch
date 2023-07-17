
package swsom.algorithm.algorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.Edge;
import swsom.algorithm.ExemplarVector;
import swsom.algorithm.SOMMap;
import swsom.algorithm.VectorConnection;

/**
 * Type: ConnectivityBasedSOM<br>
 * Date: 24/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class ConnectivityBasedSOM extends SOMAlgorithm
{    
    
    @Override
    protected double calculateCurrentNeighbourhoodRadius(            
            double aCurrentIteration,
            double aTotalIterations)
    {
        double currentRadius = initialNeighbourhoodSize * (aTotalIterations - aCurrentIteration) / aTotalIterations;
		return currentRadius;
    }    
    
    @Override
    protected void updateModel(
            double [][] problemBoundary,
            ExemplarVector bmu, 
            double [] data, 
            double networkRadius, 
            double learningRate,
            SOMMap aMap)
    {        
        HashSet<VectorConnection> alreadyProcessed = new HashSet<VectorConnection>();
        
        int totalHops = (int) Math.floor(networkRadius);
        if(totalHops == 0)
        {
            // just adjust the BMU
            updateVector(problemBoundary, bmu, data, learningRate);
        }
        else
        {
            LinkedList<ExemplarVector> nextLevel = new LinkedList<ExemplarVector>();
            nextLevel.add(bmu);
            
            // process members at each hop level
            for (int i = 0; i < totalHops; i++)
            {
                // process all elements at the current hop level
                for (ExemplarVector v : nextLevel)
                {
                    updateVector(problemBoundary, v, data, learningRate);
                }
                // clear the current set
                LinkedList<ExemplarVector> oldlist = new LinkedList<ExemplarVector>(nextLevel);
                nextLevel.clear();
                // prepare the next level                
                for (ExemplarVector v : oldlist)
                {
                    nextLevel.addAll(getNonSelfVertices(v, alreadyProcessed));
                }                
            }
        }
    }    
    
    protected LinkedList<ExemplarVector> getNonSelfVertices(
            ExemplarVector self,
            HashSet<VectorConnection> alreadyProcessed)
    {
        LinkedList<ExemplarVector> nextLevel = new LinkedList<ExemplarVector>();
        Edge [] edges = self.getEdges();
        
        for (int i = 0; i < edges.length; i++)
        {
            // check if not already processed
            if(alreadyProcessed.contains(edges[i]))
            {
                // already processed
                continue;
            }
            
            // add to the set
            alreadyProcessed.add((VectorConnection)edges[i]);
            
            // retrieve the non-self nodes 
            if(edges[i].getVertex1() == self)
            {
                nextLevel.add((ExemplarVector)edges[i].getVertex2());
            }
            else
            {
                nextLevel.add((ExemplarVector)edges[i].getVertex1());
            }
        }
        
        return nextLevel;
    }
    
    @ Override
    public String toString()
    {
        return "Connectivity-based SOM";
    }
    
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        initialLearningRate = 0.3;
        initialNeighbourhoodSize = 10;
        totalIterations = DEFAULT_ITERATIONS;
        
        // labels
        JLabel learningRateLabel = new JLabel("Learning rate:");
        JLabel neighbourhoodSizeLabel = new JLabel("Neighbourhood size:");
        JLabel iterationsLabel = new JLabel("Algorithm iterations:");
        
        // fields
        learningRateField = new JTextField(Double.toString(initialLearningRate), 10);
        neighbourhoodSizeField = new JTextField(Double.toString(initialNeighbourhoodSize), 10);
        iterationsField = new JTextField(Integer.toString(totalIterations), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(learningRateLabel);
        labelPane.add(neighbourhoodSizeLabel);
        labelPane.add(iterationsLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(learningRateField);
        fieldPane.add(neighbourhoodSizeField);
        fieldPane.add(iterationsField);

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
        return 3;
    }
}
