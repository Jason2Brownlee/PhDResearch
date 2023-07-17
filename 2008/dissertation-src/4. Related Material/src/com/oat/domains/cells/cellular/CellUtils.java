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
package com.oat.domains.cells.cellular;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 07/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CellUtils
{	
	/**
	 * Assess the given repertoire against the given antigen
	 * @param <C>
	 * @param antigen
	 * @param repertoire
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstAntigen(Antigen antigen, Collection<C> repertoire)
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
	public static <C extends DiscreteCell> void assessCellAgainstAntigen(Antigen antigen, C cell)
	{
		double score = antigen.costDiscreteCell(cell);
		cell.evaluated(score);
	}
	
	
	/**
	 * Locate the best matching cell in the repertoire, with random tie handling.
	 * Assumes the reperotire has already been evaluated
	 * @param repertoire
	 * @param rand
	 * @return - best matching cell in the repertoire
	 */
	public static <C extends DiscreteCell> C getRepertoireBMU(LinkedList<C> repertoire, Random rand)
	{
		// jumble the repertoire
		Collections.shuffle(repertoire, rand);
		// order the repertoire
		Collections.sort(repertoire);
		// return the best solution according to the antigen
		return repertoire.getFirst();
	}
	
	/**
	 * Return a set of the best matching units, presumes assessment
	 * @param <C>
	 * @param repertoire
	 * @param rand
	 * @return
	 */
	public static <C extends DiscreteCell> LinkedList<C> getRepertoireBMUSet(
			LinkedList<C> repertoire, 
			Random rand)
	{
		// jumble the repertoire
		Collections.shuffle(repertoire, rand);
		// order the repertoire
		Collections.sort(repertoire);
		// collect the set
		LinkedList<C> best = new LinkedList<C>();
		best.add(repertoire.getFirst());
		double bmuScore = best.getFirst().getScore();
		for (int i = 1; i < repertoire.size(); i++)
		{
			C c = repertoire.get(i);
			// check if not the same fitness
			if(c.getScore() != bmuScore)
			{
				break;
			}
			// otherwise keep collecting
			best.add(c);
		}		
		
		return best;
	}
	
	
	/**
	 * Order the repertoire and set the best subset ordered by utility
	 * @param <C>
	 * @param repertoire
	 * @param rand
	 * @param selectionSize
	 * @return
	 */
	public static <C extends DiscreteCell> LinkedList<C> selectCellSet(
			LinkedList<C> repertoire, 
			Random rand, 
			int selectionSize)
	{
		return selectCellSetWithExclusion(repertoire, null, rand, selectionSize);
	}	
	
	/**
	 * Order the repertoire and set the best subset ordered by utility, exclude cells from the exclude set
	 * @param <C>
	 * @param repertoire
	 * @param exclude
	 * @param rand
	 * @param selectionSize
	 * @return
	 */
	public static <C extends DiscreteCell> LinkedList<C> selectCellSetWithExclusion(
			List<C> repertoire, 
			List<C> exclude, 
			Random rand, 
			int selectionSize)
	{
		// remove any existing positional bias
		Collections.shuffle(repertoire, rand);
		// order by utility
		Collections.sort(repertoire);	
		
		LinkedList<C> selectedSet = new LinkedList<C>();		
		for (int i = 0; selectedSet.size() < selectionSize && i < repertoire.size(); i++)
		{			
			C current = repertoire.get(i);			
			// check if using exclusion and current should be excluded
			if(exclude != null && exclude.contains(current))
			{
				continue;
			}
			// store
			selectedSet.add(current);
		}
		
		if(selectedSet.size() != selectionSize)
		{
			throw new RuntimeException("Insufficient cells in the repertoire to select activated set (used exclusion="+(exclude != null)+")");
		}
		
		return selectedSet;
	}
	
	/**
	 * Assess the average Hamming distance for a given cell against all other cells in the repertoire
	 * @param <D>
	 * @param cells
	 * @return
	 */
	public static <D extends DiscreteCell> double averageDistance(List<D> cells)
	{
		return averageDistance(cells.toArray(new DiscreteCell[cells.size()]));
	}
	
	/**
	 * Average of the average distances between a given cell and all other cells in the repertoire
	 * 
	 * @param cells
	 * @return
	 */
	public static double averageDistance(DiscreteCell [] cells)
	{
		double mSum = 0.0;
	
		for (int i = 0; i < cells.length; i++)
		{			
			// caluclate average distance for this cell
			double sum = 0.0;
			for (int j = 0; j < cells.length; j++)
			{				
				sum += cells[i].distance(cells[j]);
			}
			// sum the averages
			mSum += (sum / cells.length);
		}
		
		// average the averages
		return mSum / cells.length;
	}
	
	
	
	/**
	 * Assess the average Hamming distance for a given cell against all other cells in the repertoire
	 * @param <D>
	 * @param cells
	 * @return
	 */
	public static <D extends DiscreteCell> double averageDistanceRatio(List<D> cells)
	{		
		return averageDistanceRatio(cells.toArray(new DiscreteCell[cells.size()]));
	}
	
	/**
	 * Average of the average distances between a given cell and all other cells in the repertoire
	 * 
	 * @param cells
	 * @return
	 */
	public static double averageDistanceRatio(DiscreteCell [] cells)
	{
		double mSum = 0.0;
	
		for (int i = 0; i < cells.length; i++)
		{			
			// caluclate average distance for this cell
			double sum = 0.0;
			for (int j = 0; j < cells.length; j++)
			{				
				// ratio
				sum += cells[i].distance(cells[j]);
			}
			// sum the averages
			mSum += (sum / cells.length);
		}
		
		// average the averages
		return mSum / cells.length;
	}
	
	
	
	/**
	 * Assess the average Hamming distance for a given antigen against all other antigen in the set
	 * @param antigen
	 * @return
	 */
	public static double averageHammingDistance(Antigen [] antigen)
	{
		double mSum = 0.0;
	
		for (int i = 0; i < antigen.length; i++)
		{			
			// caluclate average distance for this cell
			double sum = 0.0;
			for (int j = 0; j < antigen.length; j++)
			{				
				sum += antigen[i].costAntigenHamming(antigen[j]);
			}
			// sum the averages
			mSum += (sum / antigen.length);
		}
		
		// average the averages
		return mSum / antigen.length;
	}
	
	/**
	 * Creates a random collection of cells
	 * 
	 * @param rand
	 * @param numCells
	 * @param numBitsPerCell
	 * @return
	 */
	public static LinkedList<Cell> getRandomCellRepertoire(Random rand, int numCells, int numBitsPerCell)
	{
		LinkedList<Cell> repertoire = new LinkedList<Cell>();		
		for (int i = 0; i < numCells; i++)
		{
			boolean [] data = RandomUtils.randomBitString(rand, numBitsPerCell);
			repertoire.add(new Cell(data));
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
	public static LinkedList<Cell> cloningAndMutationCell(Cell bmu, int numClones, Random rand)
	{
		LinkedList<Cell> newPop = new LinkedList<Cell>();
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			// mutate
			double mutationRate = 1.0 / cloneData.length;
			EvolutionUtils.binaryMutate(cloneData, rand, mutationRate);
			Cell clone = new Cell(cloneData);
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
	public static LinkedList<Cell> cloningAndMutationCell(LinkedList<Cell> selectedSet, int numClones, Random rand)
	{
		LinkedList<Cell> newPop = new LinkedList<Cell>();
		
		for(Cell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationCell(current, numClones, rand));
		}
		
		return newPop;
	}
	
	/**
	 * Replacement based on Hamming distance and fitness tournament with exclusion
	 * 
	 * @param <C>
	 * @param progeny
	 * @param repertoire
	 * @param rand
	 */
	public static <C extends DiscreteCell> void replaceIntoRepertoireSimilarityScore(
			LinkedList<C> progeny, 
			LinkedList<C> repertoire, 
			Random rand)
	{		
		// process the progeny
		for(C childCell : progeny)
		{
			replaceIntoRepertoireSimilarityScore(childCell, repertoire, progeny, rand);
		}
	}	
	
	/**
	 * Replacement based on Hamming distance and fitness tournament with exclusion
	 * 
	 * @param <C>
	 * @param childCell
	 * @param repertoire
	 * @param exclusion
	 * @param rand
	 */
	public static <C extends DiscreteCell> void replaceIntoRepertoireSimilarityScore(
			C childCell, 
			LinkedList<C> repertoire, 
			LinkedList<C> exclusion, 
			Random rand)
	{
		// Euclidean similarity tournament for competition, with exclusion
		C similar = CellUtils.getMostSimilarWithExclusion(childCell, repertoire, exclusion, rand);
		// fitness tournament for resources
		if(childCell.getScore() <= similar.getScore())
		{
			repertoire.remove(similar);
			repertoire.add(childCell);
		}
	}
	
	
	/**
	 * Replacement based on Hamming distance with exclusion
	 * 
	 * @param <C>
	 * @param progeny
	 * @param repertoire
	 * @param rand
	 */
	public static <C extends DiscreteCell> void replaceIntoRepertoireSimilarity(
			LinkedList<C> progeny, 
			LinkedList<C> repertoire, 
			Random rand)
	{		
		// process the progeny
		for(C childCell : progeny)
		{
			replaceIntoRepertoireSimilarity(childCell, repertoire, progeny, rand);
		}
	}	
	
	/**
	 * Replacement based on Hamming distance with exclusion
	 * 
	 * @param <C>
	 * @param childCell
	 * @param repertoire
	 * @param exclusion
	 * @param rand
	 */
	public static <C extends DiscreteCell> void replaceIntoRepertoireSimilarity(
			C childCell, 
			LinkedList<C> repertoire,
			LinkedList<C> exclusion, 
			Random rand)
	{
		// Euclidean similarity tournament for competition, with exclusion
		C similar = CellUtils.getMostSimilarWithExclusion(childCell, repertoire, exclusion, rand);
		// fitness tournament for resources
		repertoire.remove(similar);
		repertoire.add(childCell);
	}
	
	
	/**
	 * Locate the most similar cell in the repertoire using an exclusion set.
	 * Randomizes the set before searching to remove positional bias
	 * 
	 * @param <C>
	 * @param cell
	 * @param set
	 * @param exclude
	 * @param rand
	 * @return
	 */
	public static <C extends DiscreteCell> C getMostSimilarWithExclusion(
			C cell, LinkedList<C> set, 
			LinkedList<C> exclude, 
			Random rand)
	{
		// randomise the repertoire to remove positional bias
		Collections.shuffle(set, rand);	
		
		double  min = Double.POSITIVE_INFINITY;
		C best = null;
		
		for(C c : set)
		{
			// check exclusion
			if(exclude!=null && exclude.contains(c))
			{
				continue;
			}
			// assess distance
			double d = cell.distance(c);
			if(d < min)
			{
				min = d;
				best = c;
			}
		}
		
		return best;		
	}	
	
	/**
	 * Calculate the hamming distance between the two cells
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double hammingDistance(DiscreteCell c1, DiscreteCell c2)
	{
		return BitStringUtils.hammingDistance(c1.getData(), c2.getData());
	}
	
	/**
	 * Calculate the Euclidean distance between the two cells
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double euclideanDistance(Cell c1, Cell c2)
	{
		return AlgorithmUtils.euclideanDistance(c1.getDecodedData(), c2.getDecodedData());
	}
	
	/**
	 * 
	 * @param set
	 */
	public static <C extends DiscreteCell> void clearScores(LinkedList<C> set, double score)
	{
		for(C c : set)
		{
			c.evaluated(score);
		}
	}
}
