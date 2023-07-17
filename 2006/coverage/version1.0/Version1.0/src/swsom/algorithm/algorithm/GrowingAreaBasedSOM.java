
package swsom.algorithm.algorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.ExemplarVector;

/**
 * Type: GrowingAreaBasedSOM<br/>
 * Date: 23/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GrowingAreaBasedSOM extends ConnectivityBasedSOM
{
    protected boolean adjustFrontReduction = true;
    protected boolean adjustFrontExpantion = true;
    
    protected double squarePushLearningRate;
    protected double squarePullLearningRate;
      

    @Override
    protected void updateVector(
	        double [][] problemBoundary,
	        ExemplarVector aCodebookVector, 
	        double [] aDataInstance, 
	        double aLearningRate)
	{
        // get the data from within the exemplar
        double [] exemplar = aCodebookVector.getData();
        double [] square = aCodebookVector.getSquareRadii();        
	    // update the exemplar as per normal
        updateVector(exemplar, aDataInstance, aLearningRate, problemBoundary);        
        // updates squares
        updateSquares(aDataInstance, exemplar, square);
	}
    
    protected void updateSquares(
            double [] aDataInstance,
            double [] exemplar,
            double [] square)
    {
        // calculate a distance on each axis, then 
        // locate the distance with the largest magnatude
        // where positive is left & up, negative is right, down
        double [] axisDistance = new double[aDataInstance.length];
        double largest = Double.NEGATIVE_INFINITY;
        int index = -1;
        for (int i = 0; i < axisDistance.length; i++)
        {
            axisDistance[i] = (exemplar[i] - aDataInstance[i]);
            if(Math.abs(axisDistance[i]) > largest)
            {
                largest = Math.abs(axisDistance[i]);
                index = i;
            }
        }
        
        if(index == -1)
        {
            return;
        }        
        
        //
        // array order: (0)-left, (1)+right, (2)-down, (3)+up
        //
        
        // check for a positive - positive means left or down
        // it means the point is to the left on the x axis, or down on the y axis of the node
        if(axisDistance[index] > 0) 
        {            
            updateLeftAndDownFronts(index, aDataInstance, exemplar, square);
        }
        // the value is negative - means right or up
        else if(axisDistance[index] < 0) 
        {          
            updateRightAndUpFronts(index, aDataInstance, exemplar, square);            
        } 
    }
    
    
    protected void updateLeftAndDownFronts(
            int selectedIndex,
            double [] aDataInstance,
            double [] aExemplar,
            double [] aSquares)
    {
        int squareIndex = -1;
        
        // determine which square index
        if(selectedIndex == 0)
        {
            // remain the same
            squareIndex = 0; // left
        }
        else
        {
            squareIndex = 2; // down
        }        
        
        // the front has a position which again is relative to the sample,
        // the idea is to move the front closer to the point.
        
        // locate the position of the front
        double position = aExemplar[selectedIndex] - aSquares[squareIndex];
        // determine difference in position on axis for the front
        double diff = (position - aDataInstance[selectedIndex]);
        
        double adjustmentRate = 0.0;
        
        // check for a reduction adjustment
        if(diff < 0)
        {
            if(!adjustFrontReduction)
            {
                return;
            }
            adjustmentRate = squarePullLearningRate;
        }        
        else if(diff > 0)
        {
            if(!adjustFrontExpantion)
            {
                return;
            }
            adjustmentRate = squarePushLearningRate;
        }

        // we are adjusting the negative axies left and down
        // the difference between the front is thus:
        // positive means push it out, negative means pull it in
        
        aSquares[squareIndex] += (diff * adjustmentRate);
    }
    
    protected void updateRightAndUpFronts(
            int selectedIndex,
            double [] aDataInstance,
            double [] aExemplar,
            double [] aSquares)
    {
        int squareIndex = -1;
        // determine square index
        if(selectedIndex == 0)
        {
            squareIndex = 1; // right
        }
        else
        {
            squareIndex = 3; // up
        }
        
        // locate the position on the front
        double position = aExemplar[selectedIndex] + aSquares[squareIndex];
        // determine difference in position on axis for the front
        double diff = (position - aDataInstance[selectedIndex]);
        
        double adjustmentRate = 0.0;
        
        // check for a retraction
        if(diff > 0)
        {
            if(!adjustFrontReduction)
            {
                return;
            }
            adjustmentRate = squarePullLearningRate;
        }
        else if(diff < 0)
        {
            if(!adjustFrontExpantion)
            {
                return;
            }
            adjustmentRate = squarePushLearningRate;
        }
        
        // we are adjusting the positive axies right and up
        // the effect on the front is thus: 
        // positive pull it back, negative push it out
        // thus its the reverse of above
        
        aSquares[squareIndex] -= (diff * adjustmentRate); 
    }
    
    
    
    /**
     * Update the exemplar in response to a samplea and a learning rate
     * 
     * @param aExemplars
     * @param aSample
     * @param aLearningRate
     * @param aBoundary
     */
	protected void updateVector(
	        double [] aExemplars, 
	        double [] aSample,
	        double aLearningRate,	        
	        double [][] aBoundary)
	{
	    // update all attributes	    
		for(int i=0; i<aSample.length; i++)
		{			
			// calculate the delta (weighted difference) and update codebook vector
		    aExemplars[i] += (aLearningRate * (aSample[i] - aExemplars[i]));			
		}		
		// ensure point still exists within the domain
		wrapToBoundary(aBoundary, aExemplars);
	}    
    
    @ Override
    public String toString()
    {
        return "Square-base Volume SOM";
    }
    

    
    @Override
    public void initialise()
    {
        super.initialise();
        
        // squarePushLearningRate
        try
        {
            squarePushLearningRate = Double.parseDouble(pushRateField.getText());
        }
        catch (Exception e)
        {
            squarePushLearningRate = 0.3;
            pushRateField.setText("" + squarePushLearningRate);
        }
        finally
        {
            if(squarePushLearningRate<=0)
            {
                squarePushLearningRate = 0.3;
                pushRateField.setText("" + squarePushLearningRate);
            }
        }
        // squarePullLearningRate
        try
        {
            squarePullLearningRate = Double.parseDouble(pullRateField.getText());
        }
        catch (Exception e)
        {
            squarePullLearningRate = 0.1;
            pullRateField.setText("" + squarePullLearningRate);
        }
        finally
        {
            if(squarePullLearningRate<=0)
            {
                squarePullLearningRate = 0.1;
                pullRateField.setText("" + squarePullLearningRate);
            }
        }
    }
    
    protected JTextField pushRateField;
    protected JTextField pullRateField;
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        initialLearningRate = 0.3;
        initialNeighbourhoodSize = 10;
        totalIterations = DEFAULT_ITERATIONS;
        squarePushLearningRate = 0.3;
        squarePullLearningRate = 0.1;
        
        // labels
        JLabel learningRateLabel = new JLabel("Learning rate:");
        JLabel neighbourhoodSizeLabel = new JLabel("Neighbourhood size:");
        JLabel iterationsLabel = new JLabel("Algorithm iterations:");
        JLabel pushLabel = new JLabel("Square push rate:");
        JLabel pullLabel = new JLabel("Square pull rate:");
        
        // fields
        learningRateField = new JTextField(Double.toString(initialLearningRate), 10);
        neighbourhoodSizeField = new JTextField(Double.toString(initialNeighbourhoodSize), 10);
        iterationsField = new JTextField(Integer.toString(totalIterations), 10);
        pushRateField = new JTextField(Double.toString(squarePushLearningRate), 10);
        pullRateField = new JTextField(Double.toString(squarePullLearningRate), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(learningRateLabel);
        labelPane.add(neighbourhoodSizeLabel);
        labelPane.add(iterationsLabel);
        labelPane.add(pushLabel);
        labelPane.add(pullLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(learningRateField);
        fieldPane.add(neighbourhoodSizeField);
        fieldPane.add(iterationsField);
        fieldPane.add(pushRateField);
        fieldPane.add(pullRateField);

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
