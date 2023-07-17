/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2008  Jason Brownlee

OAT is free software; you can redistribute it and/or modify it under the terms
of the GNU Lesser General Public License as published by the Free Software 
Foundation; either version 3 of the License, or (at your option) any 
later version.

OAT is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for 
more details.

You should have received a copy of the GNU Lesser General Public License 
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Jason Brownlee
Project Lead
*/
package com.oat.domains.cells.degenerate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigen;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: Utilities for working with degenerate clonal selection algorithms
 *  
 * Date: 04/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DegenerateUtils
{
	/**
	 * Assess the given repertoire against the given antigen
	 * @param <C>
	 * @param antigen
	 * @param repertoire
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstAntigen(DegenerateAntigen antigen, Collection<C> repertoire)
	{
		// assess the repertoire
		for(C cell : repertoire)
		{
			assessCellAgainstAntigen(antigen, cell);
		}
	}	
	
	/**
	 * Assess the given cell against the given antigen
	 * @param <C>
	 * @param antigen
	 * @param cell
	 */
	public static <C extends DiscreteCell> void assessCellAgainstAntigen(DegenerateAntigen antigen, C cell)
	{
		double score = antigen.costDiscreteCell(cell);
		cell.evaluated(score);
	}
	
	
	/**
	 * Creates a random collection of cells
	 * 
	 * @param rand
	 * @param numCells
	 * @param numBitsPerCell
	 * @return
	 */
	public static LinkedList<SubCell> getRandomSubCellRepertoire(Random rand, int numCells, int numBitsPerCell)
	{
		LinkedList<SubCell> repertoire = new LinkedList<SubCell>();		
		for (int i = 0; i < numCells; i++)
		{
			boolean [] data = RandomUtils.randomBitString(rand, numBitsPerCell);
			repertoire.add(new SubCell(data));
		}
		return repertoire;
	}
	
	/**
	 * Creates a random collection of cells
	 * 
	 * @param rand
	 * @param numCells
	 * @param numBitsPerCell
	 * @return
	 */
	public static LinkedList<DegenerateCell> getRandomDegenerateCellRepertoire(Random rand, int numCells, int numBitsPerCell)
	{
		LinkedList<DegenerateCell> repertoire = new LinkedList<DegenerateCell>();		
		for (int i = 0; i < numCells; i++)
		{
			boolean [] data = RandomUtils.randomBitString(rand, numBitsPerCell);
			boolean [] mask = RandomUtils.randomBitString(rand, numBitsPerCell);
			repertoire.add(new DegenerateCell(data, mask));
		}
		return repertoire;
	}
	
	/**
	 * Create a set of mutated clones for the provided Cell
	 * @param bmu
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<SubCell> cloningAndMutationSubCell(SubCell bmu, int numClones, Random rand)
	{
		LinkedList<SubCell> newPop = new LinkedList<SubCell>();
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			// mutate
			double mutationRate = 1.0 / cloneData.length;
			EvolutionUtils.binaryMutate(cloneData, rand, mutationRate);
			SubCell clone = new SubCell(cloneData);
			clone.setComponent(bmu.getComponent());
			newPop.add(clone);
		}
		
		return newPop;
	}
	
	/**
	 * Create a set of mutated clones for the provided Cell,
	 * Mutation treats the data and the mask as a single string (mutation is 1/L)
	 * 
	 * @param bmu
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<DegenerateCell> cloningAndMutationDegenerateCell(DegenerateCell bmu, int numClones, Random rand)
	{
		LinkedList<DegenerateCell> newPop = new LinkedList<DegenerateCell>();
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			boolean [] cloneMask = ArrayUtils.copyArray(bmu.getMask());
			// mutate, treat data and mask as one long string (1/L)
			double mutationRate = 1.0 / (cloneData.length*2);
			EvolutionUtils.binaryMutate(cloneData, rand, mutationRate);
			EvolutionUtils.binaryMutate(cloneMask, rand, mutationRate);
			// create 
			DegenerateCell clone = new DegenerateCell(cloneData, cloneMask);
			newPop.add(clone);
		}
		
		return newPop;
	}
	
	
	/**
	 * Creates a set of clones for each Cell in the selected set
	 * @param selectedSet
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<SubCell> cloningAndMutationSubCell(LinkedList<SubCell> selectedSet, int numClones, Random rand)
	{
		LinkedList<SubCell> newPop = new LinkedList<SubCell>();
		
		for(SubCell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationSubCell(current, numClones, rand));
		}
		
		return newPop;
	}
	
	/**
	 * Creates a set of clones for each Cell in the selected set
	 * @param selectedSet
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<DegenerateCell> cloningAndMutationDegenerateCell(LinkedList<DegenerateCell> selectedSet, int numClones, Random rand)
	{
		LinkedList<DegenerateCell> newPop = new LinkedList<DegenerateCell>();
		
		for(DegenerateCell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationDegenerateCell(current, numClones, rand));
		}
		
		return newPop;
	}
	
	
	/**
	 * Creates a cell from a set of subcells
	 * @param subCells
	 * @return
	 */
	public static Cell cellFromSubCells(SubCell [] subCells)
	{
		if(subCells.length != Antigen.NUM_COMPONENTS)
		{
			throw new RuntimeException("Cannot create a cell from "+subCells.length+" compoennts, expect " + Antigen.NUM_COMPONENTS);
		}
		
		boolean [] dest = new boolean[Antigen.numGoalStateBits()];
		
		// process each component
		int off = 0;
		for (int i = 0; i < subCells.length; i++)
		{
			boolean [] src = subCells[i].getData();
			System.arraycopy(src, 0, dest, off, src.length);
			off += src.length;
		}
		
		if(off != dest.length)
		{
			throw new RuntimeException("Error in master cell creation");
		}
		
		return new Cell(dest);
	}
	
	
	/**
	 * Creates a bit frequency histogram from a population of degenerate cells
	 * @param pop
	 * @return histogram of the format: [i][0] == '0', [i][1] == '1'
	 */
	public static int [][] bitHistogramFromDegenerateCells(LinkedList<DegenerateCell> pop)
	{		
		int [][] histogram = new int[Antigen.numGoalStateBits()][2];	// [i][0] == '0', [i][1] == '1'
		
		// process population
		for (DegenerateCell c : pop)
		{
			boolean [] data = c.getData();
			boolean [] mask = c.getMask();			
			
			for (int i = 0; i < mask.length; i++)
			{
				if(mask[i])
				{
					if(data[i])
					{
						histogram[i][1]++;
					}
					else
					{
						histogram[i][0]++;
					}
				}
			}
		}
		
		return histogram;
	}
	
	/**
	 * Create a cell from a bit histogram
	 * @param histogram - expected format: [i][0] == '0', [i][1] == '1'
	 * @param rand
	 * @return
	 */
	public static Cell cellFromBitHistogramProbabilistic(int [][] histogram, Random rand)
	{
		boolean [] data = new boolean[histogram.length];
		// process each bit
		for (int i = 0; i < data.length; i++)
		{
			// check for no information on the current bit
			// both zero or both the same
			if(histogram[i][0] == histogram[i][1])
			{
				// random
				data[i] = rand.nextBoolean();
			}
			else
			{
				// check for a zero case
				if(histogram[i][0] == 0)
				{
					data[i] = true; // nothing in false, choose true
				}
				else if(histogram[i][1] == 0)
				{
					data[i] = false; // nothing in true, choose false
				}
				else
				{
					// probability of zero
					double prob = histogram[i][0] / (histogram[i][0] + histogram[i][1]);
					data[i] = (rand.nextDouble() < prob) ? false : true;
				}
			} 
		}
		
		return new Cell(data);
	}
	
	/**
	 * Create a cell from a bit histogram
	 * @param histogram - expected format: [i][0] == '0', [i][1] == '1'
	 * @param rand
	 * @return
	 */
	public static Cell cellFromBitHistogramDeterministic(int [][] histogram, Random rand)
	{
		boolean [] data = new boolean[histogram.length];
		// process each bit
		for (int i = 0; i < data.length; i++)
		{
			// check for no information on the current bit
			// both zero or both the same
			if(histogram[i][0] == histogram[i][1])
			{
				// random
				data[i] = rand.nextBoolean();
			}
			else
			{
				// check for a zero case
				if(histogram[i][0] == 0)
				{
					data[i] = true; // nothing in false, choose true
				}
				else if(histogram[i][1] == 0)
				{
					data[i] = false; // nothing in true, choose false
				}
				// check for false
				else if(histogram[i][0] > histogram[i][1])
				{
					data[i] = false;
				}
				// check for true
				else if(histogram[i][0] < histogram[i][1])
				{
					data[i] = true;
				}
				else
				{
					throw new RuntimeException("Missed a case i="+i+" [0]="+ histogram[i][0]+", [1]="+ histogram[i][1]);
				}
			} 
		}
		
		return new Cell(data);
	}
	
	/**
	 * Compresses a degenerate repertoire down to a histogram and probabilistically makes one
	 * cell from the histogram
	 * 
	 * @param repertoire
	 * @param rand
	 * @return
	 */
	public static Cell cellFromDegenerateCellsProbabilistic(LinkedList<DegenerateCell> repertoire, Random rand)
	{
		// build a bit histogram
		int [][] histogram = bitHistogramFromDegenerateCells(repertoire);		
		// create cell		
		return cellFromBitHistogramProbabilistic(histogram, rand);
	}
	
	/**
	 * Compresses a degenerate repertoire down to a histogram and deterministicly makes one
	 * cell from the histogram
	 * 
	 * @param repertoire
	 * @param rand
	 * @return
	 */
	public static Cell cellFromDegenerateCellsDeterministic(LinkedList<DegenerateCell> repertoire, Random rand)
	{
		// build a bit histogram
		int [][] histogram = bitHistogramFromDegenerateCells(repertoire);		
		// create cell		
		return cellFromBitHistogramDeterministic(histogram, rand);
	}
	
	/**
	 * Compresses a degenerate repertoire down to a histogram and probabilistically makes 
	 * a set of cells from the histogram
	 * 
	 * @param repertoire
	 * @param numCells
	 * @param rand
	 * @return
	 */
	public static LinkedList<Cell> cellFromDegenerateCells(LinkedList<DegenerateCell> repertoire, int numCells, Random rand)
	{
		// build a bit histogram
		int [][] histogram = bitHistogramFromDegenerateCells(repertoire);		
		// create cells
		LinkedList<Cell> cells = new LinkedList<Cell>();		
		for (int i = 0; i < numCells; i++)
		{
			cells.add(cellFromBitHistogramProbabilistic(histogram, rand));
		}
		
		return cells;
	}	
	
	/**
	 * 
	 * @param component
	 * @param repertoire
	 */
	public static void assignComponentOfInterest(int component, LinkedList<SubCell> repertoire)
	{
		for(SubCell c : repertoire)
		{
			c.setComponent(component);
		}
	}
	
	/**
	 * Calculate Hamming distance between a bitstring with a mask and another bitstring
	 * 
	 * @param data1
	 * @param mask1
	 * @param data2
	 * @return
	 */
	public static double maskHammingDistance(boolean [] data1, boolean [] mask1, boolean [] data2)
	{		
		// number of differences
		int count = 0;
		
		for (int i = 0; i < mask1.length; i++)
		{
			if(mask1[i])
			{
				// check for match
				if(data1[i] != data2[i])
				{
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Calculate hamming distance between two strings with masks
	 * @param data1
	 * @param mask1
	 * @param data2
	 * @param mask2
	 * @return
	 */
	public static double maskHammingDistance(boolean [] data1, boolean [] mask1, boolean [] data2, boolean [] mask2)
	{		
		// number of differences
		int count = 0;
		
		for (int i = 0; i < mask1.length; i++)
		{
			// have to exist in both strings
			if(mask1[i] && mask2[i])
			{
				// check for match
				if(data1[i] != data2[i])
				{
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Calculate the hamming distance between two degenerate cells using cell masks
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double hammingDistance(DegenerateCell c1, DegenerateCell c2)
	{
		return maskHammingDistance(c1.getData(), c1.getMask(), c2.getData(), c2.getMask());
	}
	
	
	/**
	 * Calculate the hamming distance between a cell and a component
	 * @param c1
	 * @param c2
	 * @param componentNumber
	 * @return
	 */
	public static double hammingDistance(Cell c1, SubCell c2, int componentNumber)
	{
		boolean [] d1 = c1.getData();
		boolean [] d2 = c2.getData();
		int offset = componentNumber * d2.length; // offset
		
		return BitStringUtils.hammingDistance(d1, offset, d2.length, d2, 0, d2.length);
	}	
	
	/**
	 * Calculate the euclidean distance between a cell and a component
	 * @param c1
	 * @param c2
	 * @param componentNumber
	 * @return
	 */
	public static double euclideanDistance(Cell c1, SubCell c2, int componentNumber)
	{				
		return Math.abs(c2.getDecodedData() - c1.getDecodedData()[componentNumber]);
	}	
	
	/**
	 * Sub-divide the repertoire into the sub-repertoires based on component preference in the antigen
	 * @param antigen
	 * @param cells
	 * @return
	 */
	public static LinkedList<SubCell> [] subDivideRepertoireByComponentPreference(
			DegenerateAntigen antigen, 
			LinkedList<SubCell> cells)
	{
		LinkedList<SubCell> [] components = new LinkedList[antigen.getNumComponents()];
		
		for (int i = 0; i < components.length; i++)
		{
			components[i] = new LinkedList<SubCell>();
		}
		
		for (int i = 0; i < cells.size(); i++)
		{
			SubCell c = cells.get(i);
			int selected = -1;
			double best = Double.POSITIVE_INFINITY;
			for (int j = 0; j < components.length; j++)
			{
				c.setComponent(j);
				DegenerateUtils.assessCellAgainstAntigen(antigen, c);
				if(selected == -1 || c.getScore() < best)
				{
					selected = j;
					best = c.getScore();
				}
			}
			// assign 
			components[selected].add(c);
			c.setComponent(selected);
			c.evaluated(best);
		}
		
		return components;
	}
	
	/**
	 * Whether or not there is sufficient resource allocation in each component
	 * @param components
	 * @param minSize
	 * @return
	 */
	public static boolean isSufficientComponentAllocation(LinkedList<SubCell> [] components, int minSize)
	{
		boolean haveEnough = true;
		for (int j = 0; haveEnough && j < components.length; j++)
		{
			if(components[j].size() < minSize)
			{
				haveEnough = false;
			}
		}
		
		return haveEnough;		
	}
	
	/**
	 * Re-allocate an undersirable divided repertoire
	 * @param antigen
	 * @param components
	 * @param cells
	 * @param minSize
	 * @param rand
	 */
	public static void reallocateDividedRepertoire(			
			DegenerateAntigen antigen, 
			LinkedList<SubCell> [] components, 
			LinkedList<SubCell> cells,
			int minSize, 
			Random rand)
	{
		for (int i = 0; i < components.length; i++)
		{
			if(components[i].size() < minSize)
			{
				// acquire the n-best from across the entire repertoire
				DegenerateUtils.assignComponentOfInterest(i, cells);
				DegenerateUtils.assessRepertoireAgainstAntigen(antigen, cells);
				LinkedList<SubCell> selectedSet = CellUtils.selectCellSet(cells, rand, minSize);
				// remove the selected set from everywhere
				for (int j = 0; j < components.length; j++)
				{
					components[j].removeAll(selectedSet);
				}
				// add to the current one
				components[i].addAll(selectedSet);
			}
		}
	}
}
