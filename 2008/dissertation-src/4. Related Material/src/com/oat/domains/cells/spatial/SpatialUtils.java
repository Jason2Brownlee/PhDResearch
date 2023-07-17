/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006, 2007  Jason Brownlee

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
package com.oat.domains.cells.spatial;

import java.util.LinkedList;
import java.util.Random;

import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
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
public class SpatialUtils
{
	/**
	 * Locate and return the neighbourhood of the given spatial cell. Set contains
	 * All nine cells in the neighbourhood including self (the 5th at index 4).
	 * 
	 * @param <C>
	 * @param cell
	 * @param r
	 * @return
	 */
	public static <C extends LatticeCell> LinkedList<C> getNeighbours(C cell, C [][] r)
	{
		LinkedList<C> neighbours = new LinkedList<C>();
		int [] origin = cell.getCoordinate();
		
		// lines
		for (int x = origin[0]-1; x <= origin[0]+1; x++)
		{
			int px = x;
			
			if(px<0)
			{
				px = r.length-1;
			}
			else if(px>r.length-1)
			{
				px = 0;
			}
			
			// positions
			for (int y = origin[1]-1; y <= origin[1]+1; y++)
			{
				int py = y;
				if(py<0)
				{
					py = r.length-1;
				}
				else if(py>r.length-1)
				{
					py = 0;
				}
				
				// do it
				neighbours.add(r[px][py]);
			}
		}
		
		if(neighbours.size() != 9)
		{
			throw new RuntimeException("Error collecting neighbours");
		}
		
		return neighbours;
	}
	
	
	/**
	 * Create a set of mutated clones for the provided Cell
	 * @param bmu
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<SpatialCell> cloningAndMutationSpatialCell(SpatialCell bmu, int numClones, Random rand)
	{
		LinkedList<SpatialCell> newPop = new LinkedList<SpatialCell>();
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			// mutate
			double mutationRate = 1.0 / cloneData.length;
			EvolutionUtils.binaryMutate(cloneData, rand, mutationRate);
			SpatialCell clone = new SpatialCell(cloneData);
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
	public static LinkedList<SpatialCell> cloningAndMutationSpatialCell(LinkedList<SpatialCell> selectedSet, int numClones, Random rand)
	{
		LinkedList<SpatialCell> newPop = new LinkedList<SpatialCell>();
		
		for(SpatialCell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationSpatialCell(current, numClones, rand));
		}
		
		return newPop;
	}
	
	/**
	 * Creates a cell from a set of subcells
	 * @param subCells
	 * @return
	 */
	public static SpatialCell spatialCellFromSpatialSubCells(SpatialSubCell [] subCells, int [] coord)
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
		
		SpatialCell cell = new SpatialCell(dest);
		cell.setCoordinate(coord);
		return cell;
	}
	
	
	/**
	 * Create a spatial sub cell repertoire with fixed components
	 * @param size
	 * @param component
	 * @param rand
	 * @return
	 */
	public static SpatialSubCell [][] createNewSubCellRepertoire(int size, int component, Random rand)
	{
		SpatialSubCell [][] repertoire = new SpatialSubCell[size][size];
		for (int i = 0; i < repertoire.length; i++)
		{
			for (int j = 0; j < repertoire[i].length; j++)
			{
				boolean [] data = RandomUtils.randomBitString(rand, Antigen.BITS_PER_COMPONENT);
				repertoire[i][j] = new SpatialSubCell(data);
				repertoire[i][j].setComponent(component);
				repertoire[i][j].setCoordinate(new int[]{i, j});
			}
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
	public static LinkedList<SpatialSubCell> cloningAndMutationSpatialSubCell(SpatialSubCell bmu, int numClones, Random rand)
	{
		LinkedList<SpatialSubCell> newPop = new LinkedList<SpatialSubCell>();
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			// mutate
			double mutationRate = 1.0 / cloneData.length;
			EvolutionUtils.binaryMutate(cloneData, rand, mutationRate);
			SpatialSubCell clone = new SpatialSubCell(cloneData);
			clone.setComponent(bmu.getComponent());
			newPop.add(clone);
		}
		
		return newPop;
	}
}
