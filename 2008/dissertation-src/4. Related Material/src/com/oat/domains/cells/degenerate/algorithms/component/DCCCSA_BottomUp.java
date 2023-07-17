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
package com.oat.domains.cells.degenerate.algorithms.component;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigen;

/**
 * Description: 
 *  
 * Date: 10/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DCCCSA_BottomUp extends DCCCSA
{	
	
	public DCCCSA_BottomUp()
	{
		numSelected = 10;
		numClones = 1;
	}
	
	
	// data
	protected LinkedList<SubCell> cells;

	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		AntigenProblem ap = (AntigenProblem) problem;
		cells = DegenerateUtils.getRandomSubCellRepertoire(rand, numCells, Antigen.BITS_PER_COMPONENT);
		// no initial population
		return null;
	}		

	@Override
	public LinkedList<SubCell> getRepertoire()
	{	
		return cells;
	}


	
	
	@Override
	public Cell exposure(Antigen a)
	{
		DegenerateAntigen antigen = (DegenerateAntigen) a;
		// min cells is the selected set divided by 3
		int minCells = numSelected / 3; 		
		// divide up the repertoire
		LinkedList<SubCell> [] components = DegenerateUtils.subDivideRepertoireByComponentPreference(antigen, cells);
		// check for the case where preference does not extend across entire antigen
		if(!DegenerateUtils.isSufficientComponentAllocation(components, minCells))		
		{			
			do
			{
				DegenerateUtils.reallocateDividedRepertoire(antigen, components, cells, minCells, rand);
			}
			while(!DegenerateUtils.isSufficientComponentAllocation(components, minCells));
			
			// re-index the lot
			for (int i = 0; i < components.length; i++)
			{
				DegenerateUtils.assignComponentOfInterest(i, components[i]);
			}
		}				
		
		// build a bmu
		SubCell [] degenerateBMU = new SubCell[antigen.getNumComponents()];
		for (int i = 0; i < components.length; i++)
		{
			degenerateBMU[i] = CellUtils.getRepertoireBMU(components[i], rand);
		}		
		
		// do cloning				
		LinkedList<SubCell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);
		LinkedList<SubCell> clones = DegenerateUtils.cloningAndMutationSubCell(selectedSet, numClones, rand);
		DegenerateUtils.assessRepertoireAgainstAntigen(antigen, clones); // already have components from cloning
		// replace into master repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		
		return DegenerateUtils.cellFromSubCells(degenerateBMU);	
	}	

	@Override
	public String getName()
	{
		return "DCCCSA-BottomUp";
	}
}

