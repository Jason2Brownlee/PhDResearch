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

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.GenericCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigen;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Degenerate Component Cellular Clonal Selection Algorithm (DC-CCSA)
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
public class DCCCSA_Explicit extends DCCCSA
{	
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

	
	/**
	 * 
	 * @param antigen
	 * @param component
	 * @return
	 */
	public SubCell expose(DegenerateAntigen antigen, int component)
	{
		// assign component of interest
		DegenerateUtils.assignComponentOfInterest(component, cells);
		// assess repertoire
		DegenerateUtils.assessRepertoireAgainstAntigen(antigen, cells);
		// select the activated set
		LinkedList<SubCell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);			
		// cloning and mutation
		LinkedList<SubCell> clones = DegenerateUtils.cloningAndMutationSubCell(selectedSet, numClones, rand);
		// assign component of interest
		DegenerateUtils.assignComponentOfInterest(component, clones);
		// assess the clones against the antigen
		DegenerateUtils.assessRepertoireAgainstAntigen(antigen, clones);
		// replace the response into the repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		// get the component bmu
		return CellUtils.getRepertoireBMU(cells, rand);
	}
	
	@Override
	public Cell exposure(Antigen antigen)
	{		
		SubCell [] degenerateBMU = new SubCell[antigen.getNumComponents()];
		
		// process each component
		for (int i = 0; i < degenerateBMU.length; i++)
		{
			degenerateBMU[i] = expose((DegenerateAntigen)antigen, i); 
		}
		
		return DegenerateUtils.cellFromSubCells(degenerateBMU);		
	}		

	

	@Override
	public String getName()
	{
		return "DCCCSA-Explicit";
	}

}

