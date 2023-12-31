/*
 * Created on 30/12/2004
 *
 */
package weka.classifiers.immune.airs.algorithm;

import java.io.Serializable;
import java.util.LinkedList;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Normalize;

/**
 * Type: AISModelClassifier
 * File: AISModelClassifier.java
 * Date: 30/12/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public abstract class AISModelClassifier implements Serializable 
{
	protected final int kNumNeighbours; 
	
	protected final Normalize normaliser;
	
	protected final CellPool model;
	
	protected final AffinityFunction affinityFunction;
	
	

	public AISModelClassifier(
					int aKNumNeighbours,
					Normalize aNormalise,
					CellPool aCellPool,
					AffinityFunction aAffinityFunction)
	{
		normaliser = aNormalise;
		model = aCellPool;
		affinityFunction = aAffinityFunction;
		
		// adjust knn 
		int totalElements = model.getCells().size();
		if(aKNumNeighbours > totalElements)
		{			
			aKNumNeighbours = totalElements;
		}
		kNumNeighbours = aKNumNeighbours;
	}
	
	
	
	public String getModelSummary(Instances aInstances)
	{
	    StringBuffer buffer = new StringBuffer(1024);
	   
	    // data reduction percentage
	    buffer.append(" - Classifier Statistics - \n");
	    double dataReduction = 100.0 * (1.0 - ((double)model.size() / (double)aInstances.numInstances()));
	    buffer.append("Data Reduction Percentage:..." + Utils.format.format(dataReduction)+"%\n");
	    buffer.append("\n");
	    
	    // determine the breakdown of cells
	    int numClasses = aInstances.numClasses();
	    int [] counts = new int[numClasses];
	    
	    for(Cell c : model.getCells())
	    {
	        counts[(int)c.getClassification()]++;
	    }	    
	    buffer.append(" - Classifier Memory Cells - \n");
	    buffer.append("Total: " + model.getCells().size()+"\n");
	    for(int i=0; i<counts.length; i++)
	    {
	        int val = counts[i];
	        buffer.append(aInstances.classAttribute().value(i)+": "+ val+"\n");
	    }
	    
	    return buffer.toString();
	}
	
	protected int [] determineClassCountForkNN(
					Instance aInstance,
					LinkedList<Cell> affinitySortedCells)
	{
		int numClasses = aInstance.classAttribute().numValues();
		int [] classCount = new int[numClasses];
		
		for (int i = 0; i < kNumNeighbours; i++)
		{
			int classIndex = (int) affinitySortedCells.get(i).getClassification();
			classCount[classIndex]++;
		}
		
		return classCount;
	}
	
	public int classifyInstance(Instance aInstance)
	{
		// normalise vector
		normaliser.input(aInstance);
		aInstance = normaliser.output();
			
		// classify
		return classify(aInstance);
	}	
	
	protected abstract int classify(Instance aInstance);
	
	
	
	
	public AffinityFunction getAffinityFunction()
	{
		return affinityFunction;
	}
	public int getKNumNeighbours()
	{
		return kNumNeighbours;
	}
	public CellPool getModel()
	{
		return model;
	}
}
