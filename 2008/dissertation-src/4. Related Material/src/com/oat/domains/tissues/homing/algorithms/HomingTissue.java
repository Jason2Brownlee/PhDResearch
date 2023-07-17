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
package com.oat.domains.tissues.homing.algorithms;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.homing.HomingCell;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HomingTissue extends Tissue
{
	// config
	protected int numCellsToImprint = 1;
	// state
	protected final int tissueId;
	
	
	/**
	 * 
	 * @param aId
	 */
	public HomingTissue(int aId)
	{
		tissueId = aId;
	}
	
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);			
		cells = new LinkedList<Cell>();		
		for (int i = 0; i < numCells; i++)
		{
			boolean [] data = RandomUtils.randomBitString(rand, Antigen.NUM_COMPONENTS, Antigen.BITS_PER_COMPONENT);
			cells.add(new HomingCell(data));
		}		
		// no initial population
		return null;
	}
	

	
	@Override
	public Cell exposure(Antigen antigen)
	{		
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, cells);
		
		// imprint most activated cells
		imprintCells(cells);
		
		// select the activated set
		LinkedList<Cell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);			
		// cloning and mutation
		LinkedList<Cell> clones = CellUtils.cloningAndMutationCell(selectedSet, numClones, rand);
		// assess the clones against the antigen
		CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
		// replace the response into the repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		// get the bmu
		Cell bmu = CellUtils.getRepertoireBMU(cells, rand);
		// return the bmu
		return bmu;
	}
	
	/**
	 * Use a homing variation of cloning and mutation
	 * @param selectedSet
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<Cell> cloningAndMutationHoming(LinkedList<Cell> selectedSet, int numClones, Random rand)
	{
		LinkedList<Cell> newPop = new LinkedList<Cell>();
		
		for(Cell current : selectedSet)
		{			
			newPop.addAll(cloningAndMutationHoming((HomingCell)current, numClones, rand));
		}
		
		return newPop;
	}
	
	/**
	 * clone and mutate homing cells
	 * @param bmu
	 * @param numClones
	 * @param rand
	 * @return
	 */
	public static LinkedList<Cell> cloningAndMutationHoming(HomingCell bmu, int numClones, Random rand)
	{
		LinkedList<Cell> newPop = new LinkedList<Cell>();
		
		for (int j = 0; j < numClones; j++)
		{
			// copy
			boolean [] cloneData = ArrayUtils.copyArray(bmu.getData());
			// mutate
			EvolutionUtils.binaryMutate(cloneData, rand, 1.0/cloneData.length);
			// specifically a homing type cell
			HomingCell clone = new HomingCell(cloneData);
			newPop.add(clone);
		}
		
		return newPop;
	}
	
	
	/**
	 * New method for imprinting cells
	 * @param selected
	 */
	protected void imprintCells(LinkedList<Cell> repertoire)
	{
		if(numCellsToImprint == 0)
		{
			return;
		}
		
		Collections.sort(repertoire);
		for (int i = 0; i < numCellsToImprint; i++)
		{
			((HomingCell)repertoire.get(i)).setPreferredRepertoireNumber(tissueId);
		}
	}	
	
	@Override
	public String getName()
	{
		return "Homing Tissue";
	}	
	
	public int getNumCellsToImprint()
	{
		return numCellsToImprint;
	}
	public void setNumCellsToImprint(int numCellsToImprint)
	{
		this.numCellsToImprint = numCellsToImprint;
	}
	public int getTissueId()
	{
		return tissueId;
	}	
}
