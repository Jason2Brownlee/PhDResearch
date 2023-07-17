
package swsom.algorithm.algorithm;

import java.util.HashSet;
import java.util.LinkedList;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.SOMMap;
import swsom.algorithm.VectorConnection;

/**
 * Type: AreaBasedSOM<br/>
 * Date: 15/03/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GrowingAreaSOMWithConnections extends ConnectivityBasedSOM
{
   
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
            updateVector(problemBoundary, bmu, data, learningRate, true);
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
                    updateVector(problemBoundary, v, data, learningRate, (v==bmu));    
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
    
    @Override
    protected void updateVector(
	        double [][] problemBoundary,
	        ExemplarVector aCodebookVector, 
	        double [] aDataInstance, 
	        double aVertexLearningRate)
    {
        // safety
        throw new RuntimeException("This function is not supported!");
    }
    
    protected void updateVector(
	        double [][] problemBoundary,
	        ExemplarVector aCodebookVector, 
	        double [] aDataInstance,
	        double learningRate,
	        boolean isBMU)
	{
        // get the data from within the exemplar
        double [] exemplar = aCodebookVector.getData();
        double [] square = aCodebookVector.getSquareRadii();
        
	    // update the exemplar as per normal
        updateVector(exemplar, aDataInstance, learningRate, problemBoundary);
        
//        if(!isBMU)
//        {
//            return;
//        }
        
        // updates squares
        updateSquares(aDataInstance, exemplar, square, isBMU);
	}
    
    protected void updateSquares(
            double [] aDataInstance,
            double [] exemplar,
            double [] square,
            boolean isBMU)
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
        double rate = 0.0;
        if(isBMU)
        {
            rate = 1.0;
        }
        else
        {
            rate = 0.5;
        }
        
        
        // check for a positive - positive means left or down
        // it means the point is to the left on the x axis, or down on the y axis of the node
        if(axisDistance[index] > 0) 
        {            
            updateLeftAndDownFronts(index, aDataInstance, exemplar, square, rate, isBMU);
        }
        // the value is negative - means right or up
        else if(axisDistance[index] < 0) 
        {          
            updateRightAndUpFronts(index, aDataInstance, exemplar, square, rate, isBMU);            
        } 
    }
    
    
    protected void updateLeftAndDownFronts(
            int selectedIndex,
            double [] aDataInstance,
            double [] aExemplar,
            double [] aSquares,
            double aRate,
            boolean isBMU)
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
        
        // check for a retraction - bmu can never retract, only others can
        if(diff < 0 && isBMU)
        {
            return;
        }        
        // check for expantion - only bmu can expand
        else 
            if(diff > 0 && !isBMU)
        {
            return;
        }

        // we are adjusting the negative axies left and down
        // the difference between the front is thus:
        // positive means push it out, negative means pull it in
        
        aSquares[squareIndex] += (diff * aRate);
    }
    
    protected void updateRightAndUpFronts(
            int selectedIndex,
            double [] aDataInstance,
            double [] aExemplar,
            double [] aSquares,
            double aRate, 
            boolean isBMU)
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
        
        // check for a retraction
        if(isBMU && diff > 0)
        {
            return;
        }
        else 
            if(!isBMU && diff < 0)
        {
            return;
        }
        
        // we are adjusting the positive axies right and up
        // the effect on the front is thus: 
        // positive pull it back, negative push it out
        // thus its the reverse of above
        
        aSquares[squareIndex] -= (diff * aRate); 
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
	
	
}
