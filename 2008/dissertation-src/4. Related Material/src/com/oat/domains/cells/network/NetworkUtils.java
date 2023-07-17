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
package com.oat.domains.cells.network;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 05/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class NetworkUtils
{
	/**
	 * Replace based on mapping hamming distance and existing score
	 * 
	 * @param <C>
	 * @param progeny
	 * @param repertoire
	 * @param rand
	 */
	public static <C extends NetworkCell> void replaceIntoRepertoireMappingHammingScore(
			LinkedList<C> progeny, 
			LinkedList<C> repertoire, 
			Random rand)
	{		
		// process the progeny
		for(C childCell : progeny)
		{
			replaceIntoRepertoireMappingHammingScore(childCell, repertoire, progeny, rand);
		}
	}
	
	public static <C extends NetworkCell> void replaceIntoRepertoireMappingEuclideanScore(
			LinkedList<C> progeny, 
			LinkedList<C> repertoire, 
			Random rand)
	{		
		// process the progeny
		for(C childCell : progeny)
		{
			replaceIntoRepertoireMappingEuclideanScore(childCell, repertoire, progeny, rand);
		}
	}
	
	/**
	 * Locate most similar based on mapped hamming distance with exclusion
	 * 
	 * @param <C>
	 * @param cell
	 * @param set
	 * @param exclude
	 * @param rand
	 * @return
	 */
	public static <C extends NetworkCell> C getMostSimilarMappingHammingWithExclusion(
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
			double d = mappedHammingDistance(cell, c);
			if(d < min)
			{
				min = d;
				best = c;
			}
		}
		
		return best;		
	}	
	
	public static <C extends NetworkCell> C getMostSimilarMappingEuclideanWithExclusion(
			C cell, 
			LinkedList<C> set, 
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
			double d = mappedEuclideanDistance(cell, c);
			if(d < min)
			{
				min = d;
				best = c;
			}
		}
		
		return best;		
	}
	
	/**
	 * Replace based on mapping hamming distance and existing score
	 * 
	 * @param <C>
	 * @param childCell
	 * @param repertoire
	 * @param exclusion
	 * @param rand
	 */
	public static <C extends NetworkCell> void replaceIntoRepertoireMappingHammingScore(
			C childCell, 
			LinkedList<C> repertoire, 
			LinkedList<C> exclusion, 
			Random rand)
	{
		// Euclidean similarity tournament for competition, with exclusion
		C similar = getMostSimilarMappingHammingWithExclusion(childCell, repertoire, exclusion, rand);
		// fitness tournament for resources
		if(childCell.getScore() <= similar.getScore())
		{
			repertoire.remove(similar);
			repertoire.add(childCell);
		}
	}
	
	/**
	 * Replace based on mapping hamming distance and existing score
	 * 
	 * @param <C>
	 * @param childCell
	 * @param repertoire
	 * @param exclusion
	 * @param rand
	 */
	public static <C extends NetworkCell> void replaceIntoRepertoireMappingEuclideanScore(
			C childCell, 
			LinkedList<C> repertoire, 
			LinkedList<C> exclusion, 
			Random rand)
	{
		// Euclidean similarity tournament for competition, with exclusion
		C similar = getMostSimilarMappingEuclideanWithExclusion(childCell, repertoire, exclusion, rand);
		// fitness tournament for resources
		if(childCell.getScore() <= similar.getScore())
		{
			repertoire.remove(similar);
			repertoire.add(childCell);
		}
	}
	

	/**
	 * Assess the repertoire's mapping against the antigen 
	 * @param antigen
	 * @param repertoire
	 */
	public static void assessMappedRepertoireAgainstAntigen(Antigen antigen, Collection<NetworkCell> repertoire)
	{
		// assess the repertoire
		for(NetworkCell cell : repertoire)
		{
			assessCellMappingAgainstAntigen(antigen, cell);
		}
	}		

	/**
	 * Assess the cell's mapping against the antigen 
	 * @param antigen
	 * @param cell
	 */
	public static void assessCellMappingAgainstAntigen(Antigen antigen, NetworkCell cell)
	{
		double score = antigen.costCell(cell.getDecodedData2());
		cell.evaluated(score);
	}
	
	/**
	 * Assess the repertoire against the network cells mapping
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstCellMappedHamming(LinkedList<C> r1, NetworkCell c2)
	{
		for(C c1 : r1)
		{
			double score = hammingDistanceAgainstMapping(c1, c2);
			c1.evaluated(score);
		}
	}
	
	
	
	/**
	 * mappings of the reperotire against the mapping of a cell
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static void assessMappedRepertoireAgainstCellMappingEuclidean(LinkedList<NetworkCell> r1, NetworkCell c2)
	{
		for(NetworkCell c1 : r1)
		{
			double score = mappedEuclideanDistance(c1, c2);
			c1.evaluated(score);
		}
	}
	
	/**
	 * Assess based on Euclidean distance between the repertoire cell data and the network cells decoded mapped data
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends Cell> void assessRepertoireAgainstCellMappingEuclidean(LinkedList<C> r1, NetworkCell c2)
	{
		for(C c1 : r1)
		{
			double score = euclideanDistanceAgainstMapping(c1, c2);
			c1.evaluated(score);
		}
	}
	
	/**
	 * Assess the repertoire of network cells mappings against the given cells normal representat 
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends DiscreteCell> void assessMappedRepertoireCellHamming(LinkedList<NetworkCell> r1, C c2)
	{
		for(NetworkCell c1 : r1)
		{
			double score = hammingDistanceAgainstMapping(c2, c1);
			c1.evaluated(score);
		}
	}
	
	/**
	 * Assess the repertoire of network cells mappings against the given cells normal representat 
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends Cell> void assessMappedRepertoireAgainstCellEuclidean(LinkedList<NetworkCell> r1, C c2)
	{
		for(NetworkCell c1 : r1)
		{
			double score = euclideanDistanceAgainstMapping(c2, c1);
			c1.evaluated(score);
		}
	}
	
	
	
	/**
	 * Create a new random repertoire of network cells
	 * @param rand
	 * @param size
	 * @param numBitsPerAntigen
	 * @return
	 */
	public static LinkedList<NetworkCell> getRandomNetworkRepertoire(Random rand, int size, int numBitsPerAntigen)
	{
		LinkedList<NetworkCell> repertoire = new LinkedList<NetworkCell>();		
		for (int i = 0; i < size; i++)
		{
			boolean [] data1 = RandomUtils.randomBitString(rand, numBitsPerAntigen);
			boolean [] data2 = RandomUtils.randomBitString(rand, numBitsPerAntigen);
			repertoire.add(new NetworkCell(data1, data2));
		}
		return repertoire;
	}	

	/**
	 * Cloning and mutation of a network cell
	 * @param bmu
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<NetworkCell> cloningAndMutationNetwork(NetworkCell bmu, int numClones, Random rand)
	{
		LinkedList<NetworkCell> newPop = new LinkedList<NetworkCell>();
		
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData1 = ArrayUtils.copyArray(bmu.getData());
			boolean [] cloneData2 = ArrayUtils.copyArray(bmu.getData2());
			// mutate
			EvolutionUtils.binaryMutate(cloneData1, rand, 1.0/cloneData1.length);
			EvolutionUtils.binaryMutate(cloneData2, rand, 1.0/cloneData2.length);
			// create
			NetworkCell clone = new NetworkCell(cloneData1, cloneData2);
			newPop.add(clone);
		}
		
		return newPop;
	}
	
	/**
	 * Cloning and mutation of network cells
	 * @param selectedSet
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<NetworkCell> cloningAndMutationNetwork(LinkedList<NetworkCell> selectedSet, int numClones, Random rand)
	{
		LinkedList<NetworkCell> newPop = new LinkedList<NetworkCell>();
		
		for(NetworkCell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationNetwork(current, numClones, rand));
		}
		
		return newPop;
	}
	
	/**
	 * Calculate the average mapped Hamming distance of the provided network repertore
	 * @param cells
	 * @return
	 */
	public static double averageMappedHammingDistance(LinkedList<NetworkCell> cells)
	{
		NetworkCell [] cArray = new NetworkCell[cells.size()];
		cells.toArray(cArray);
		return averageMappedHammingDistance(cArray);
	}
	
	/**
	 * Calculate the average Hamming distance between the mapped representation (second string)
	 * in the provided repertoire
	 * @param cells
	 * @return
	 */
	public static double averageMappedHammingDistance(NetworkCell [] cells)
	{
		double mSum = 0.0;
	
		for (int i = 0; i < cells.length; i++)
		{			
			// caluclate average distance for this cell
			double sum = 0.0;
			for (int j = 0; j < cells.length; j++)
			{				
				sum += mappedHammingDistance(cells[i], cells[j]);
			}
			// sum the averages
			mSum += (sum / cells.length);
		}
		
		// average the averages
		return mSum / cells.length;
	}
	
	
	/**
	 *  Average network cell hamming distance, cell (primary and secondary strings) are treated as one
	 * @param cells
	 * @return
	 */
	public static double averageNetworkCellHammingDistance(LinkedList<NetworkCell> cells)
	{
		NetworkCell [] cArray = new NetworkCell[cells.size()];
		cells.toArray(cArray);
		return averageNetworkCellHammingDistance(cArray);
	}
	
	/**
	 * Average network cell hamming distance, cell (primary and secondary strings) are treated as one
	 * @param cells
	 * @return
	 */
	public static double averageNetworkCellHammingDistance(NetworkCell [] cells)
	{
		double mSum = 0.0;
	
		for (int i = 0; i < cells.length; i++)
		{			
			// caluclate average distance for this cell
			double sum = 0.0;
			for (int j = 0; j < cells.length; j++)
			{	
				// normal data
				sum += CellUtils.hammingDistance(cells[i], cells[j]);
				// mapped data
				sum += mappedHammingDistance(cells[i], cells[j]);				
			}
			// sum the averages
			mSum += (sum / cells.length);
		}
		
		// average the averages
		return mSum / cells.length;
	}
	
	/**
	 * The hamming distance between the two cells mapped representation
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double mappedHammingDistance(NetworkCell c1, NetworkCell c2)
	{
		return BitStringUtils.hammingDistance(c1.getData2(), c2.getData2());
	}
	
	/**
	 * The euclidean distance between the two cells decoded mapped representation
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double mappedEuclideanDistance(NetworkCell c1, NetworkCell c2)
	{
		return AlgorithmUtils.euclideanDistance(c1.getDecodedData2(), c2.getDecodedData2());
	}
	
	/**
	 * Assess the given cell against the network cells mapping
	 * @param <C>
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static <C extends DiscreteCell> double hammingDistanceAgainstMapping(C c1, NetworkCell c2)
	{
		return BitStringUtils.hammingDistance(c1.getData(), c2.getData2());
	}
	
	/**
	 * Calculate the Euclidean distance between the cells decoded data and the second cells decoded mapped data
	 * @param <C>
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static <C extends Cell> double euclideanDistanceAgainstMapping(C c1, NetworkCell c2)
	{
		return AlgorithmUtils.euclideanDistance(c1.getDecodedData(), c2.getDecodedData2());
	}
}
