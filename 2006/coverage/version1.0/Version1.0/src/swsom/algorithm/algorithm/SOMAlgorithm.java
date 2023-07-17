
package swsom.algorithm.algorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.Algorithm;
import swsom.algorithm.ExemplarVector;
import swsom.algorithm.IterationEventListener;
import swsom.algorithm.NeighbourhoodFunction;
import swsom.algorithm.Problem;
import swsom.algorithm.SOMMap;
import swsom.algorithm.maps.TwoDimensionalLatticeMap;
import swsom.algorithm.neighbourhood.BubbleNeighbourhoodFunction;
import swsom.algorithm.neighbourhood.GaussianNeighbourhoodFunction;

/**
 * Type: SOMAlgorithm<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class SOMAlgorithm extends Algorithm 
    implements ActionListener
{
    public final static int DEFAULT_ITERATIONS = 25000;
    
    protected NeighbourhoodFunction [] neighbourhoodFunctions;
        
    protected double initialLearningRate;    
    protected double initialNeighbourhoodSize;    
    protected NeighbourhoodFunction neighbourhoodFunction;    
    protected int totalIterations;
    
    protected JTextField learningRateField;
    protected JTextField neighbourhoodSizeField;
    protected JTextField iterationsField;
    protected JComboBox neighbourhoodCombo;
    
    
    public SOMAlgorithm()
    {}
    

    @Override
    public void run(
            SOMMap aMap,
            Problem aProblem,
            Random rand,
            double [][] sample)    
    {  
        // set a neighbourhood size if required
        if(initialNeighbourhoodSize <= 0)
        {
            throw new RuntimeException("Invalid neighbourhood size, must be > 0");
        }
        // run algorithm
        double [][] problemBoundary = aProblem.getDomainBounds();
        for (long i = 0; i < totalIterations; i++)
        {
            // get a data point
            double [] data = getNextSample(sample, rand);            
            // get best matching unit
            ExemplarVector bmu = aMap.getBestMatchingUnit(data);
            // determine neighbourhood size
            double radius = calculateCurrentNeighbourhoodRadius(i, totalIterations);            
            // determine learning rate
            double learningRate = calculateCurrentLearningRate(i, totalIterations);
            // update the model
            updateModel(problemBoundary, bmu, data, radius, learningRate, aMap);
            // update listeners
            updateListeners(i);
        }
    }
    
    protected double [] getNextSample(double [][] sampleSet, Random rand)
    {
        int index = rand.nextInt(sampleSet.length); // uniform
        return sampleSet[index];
    }
    
    protected void updateListeners(long currentIteration)
    {     
        for(IterationEventListener l : listeners)
        {
            l.iterationEvent((int)currentIteration);
        }
    }
    
    
    protected void updateModel(
            double [][] problemBoundary,
            ExemplarVector bmu, 
            double [] data, 
            double networkRadius, 
            double learningRate,
            SOMMap aMap)
    {
        ExemplarVector [] vectors = aMap.getVectors();
        
        // calculate structure distance
        for (int i = 0; i < vectors.length; i++)
        {
            double distance = ((TwoDimensionalLatticeMap)aMap).calculateNeighbourhoodDistance(bmu, vectors[i]);
            // check if within update radius
            if(neighbourhoodFunction.isDistanceInRadius(distance, networkRadius))
            {
                // calculate adjusted learning rate
                double adjustedLearningRate = neighbourhoodFunction.calculateNeighbourhoodAdjustedLearningRate(learningRate, distance, networkRadius);
                // adjust vector in response to current state
                updateVector(problemBoundary, vectors[i], data, adjustedLearningRate);
            }
        }
    }
    
	protected void updateVector(
	        double [][] problemBoundary,
	        ExemplarVector aCodebookVector, 
	        double [] aDataInstance, 
	        double aLearningRate)
	{
		// get attributes for bmu
		double [] data = aCodebookVector.getData();

		// update all attributes
		for(int i=0; i<data.length; i++)
		{			
			// calculate the delta (weighted difference) and update codebook vector
		    data[i] += (aLearningRate * (aDataInstance[i] - data[i]));			
		}
		
		// ensure point still exists within the domain
		wrapToBoundary(problemBoundary, data);
	}
	
	protected void wrapToBoundary(
	        double [][] problemBoundary,
	        double [] data
	        )
	{
	    // ensure vector is still in the world
		for (int i = 0; i < data.length; i++)
        {
            if(data[i] < problemBoundary[i][0])
            {
                data[i] = problemBoundary[i][0];
            }
            if(data[i] > problemBoundary[i][1])
            {
                data[i] = problemBoundary[i][1];
            }
        }
	}
    
    protected double calculateCurrentNeighbourhoodRadius(            
            double aCurrentIteration,
            double aTotalIterations)
    {
		double currentRadius = 1.0 + (initialNeighbourhoodSize - 1.0) * (aTotalIterations - aCurrentIteration) / aTotalIterations;
		return currentRadius;
    }
    
    protected double calculateCurrentLearningRate(            
            double aCurrentIteration,
            double aTotalIterations)
	{
		double currentRate = (initialLearningRate * (aTotalIterations - aCurrentIteration) / aTotalIterations);
		return currentRate;
	}
    

    @Override
    public void initialise()
    {
        // learningRateField
        try
        {
            initialLearningRate = Double.parseDouble(learningRateField.getText());
        }
        catch (Exception e)
        {
            initialLearningRate = 0.3;
            learningRateField.setText("" + initialLearningRate);
        }
        finally
        {
            if(initialLearningRate<=0)
            {
                initialLearningRate = 0.3;
                learningRateField.setText("" + initialLearningRate);
            }
        }
        // neighbourhood size
        try
        {
            initialNeighbourhoodSize = Double.parseDouble(neighbourhoodSizeField.getText());
        }
        catch (Exception e)
        {
            initialNeighbourhoodSize = 10;
            neighbourhoodSizeField.setText("" + initialNeighbourhoodSize);
        }
        finally
        {
            if(initialNeighbourhoodSize<=0)
            {
                initialNeighbourhoodSize = 10;
                neighbourhoodSizeField.setText("" + initialNeighbourhoodSize);
            }
        }
        // iterations
        try
        {
            totalIterations = Integer.parseInt(iterationsField.getText());
        }
        catch (Exception e)
        {
            totalIterations = DEFAULT_ITERATIONS;
            iterationsField.setText("" + totalIterations);
        }
        finally
        {
            if(totalIterations<=0)
            {
                totalIterations = DEFAULT_ITERATIONS;
                iterationsField.setText("" + totalIterations);
            }
        }
    }
    


    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == neighbourhoodCombo)
        {
            int s = neighbourhoodCombo.getSelectedIndex();
            neighbourhoodFunction = neighbourhoodFunctions[s];
        }
    }

    
    @Override
    protected JPanel getConfigurationPane()
    {
        neighbourhoodFunctions = new NeighbourhoodFunction[]{new BubbleNeighbourhoodFunction(), new GaussianNeighbourhoodFunction()};
        
        // defaults
        initialLearningRate = 0.3;
        initialNeighbourhoodSize = 10;
        neighbourhoodFunction = neighbourhoodFunctions[0];
        totalIterations = DEFAULT_ITERATIONS;
        
        // labels
        JLabel learningRateLabel = new JLabel("Learning rate:");
        JLabel neighbourhoodSizeLabel = new JLabel("Neighbourhood size:");
        JLabel iterationsLabel = new JLabel("Algorithm iterations:");
        JLabel neighbourhoodFuncLabel = new JLabel("Neighbourhood function:");
        
        // fields
        learningRateField = new JTextField(Double.toString(initialLearningRate), 10);
        neighbourhoodSizeField = new JTextField(Double.toString(initialNeighbourhoodSize), 10);
        iterationsField = new JTextField(Integer.toString(totalIterations), 10);
        neighbourhoodCombo = new JComboBox(neighbourhoodFunctions);
        neighbourhoodCombo.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(learningRateLabel);
        labelPane.add(neighbourhoodSizeLabel);
        labelPane.add(iterationsLabel);
        labelPane.add(neighbourhoodFuncLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(learningRateField);
        fieldPane.add(neighbourhoodSizeField);
        fieldPane.add(iterationsField);
        fieldPane.add(neighbourhoodCombo);

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
    
    @Override
    public String toString()
    {
        return "Self Organising Map";
    }
}
