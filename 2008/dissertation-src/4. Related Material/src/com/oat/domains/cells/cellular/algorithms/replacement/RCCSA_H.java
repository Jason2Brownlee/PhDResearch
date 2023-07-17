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
package com.oat.domains.cells.cellular.algorithms.replacement;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.ReplacementClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;

/**
 * Description: CCSA(N+N)
 * 
 * Date: 02/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RCCSA_H extends ReplacementClonalSelectionAlgorithm
{
	public RCCSA_H()
	{
		numCells = 60; 
		numSelected = 2; 
		numClones = 3; 
		// clones per antigen will be 10% (6)
	}
	
	public void replaceIntoRepertoire(Antigen antigen, LinkedList<Cell> progeny, LinkedList<Cell> repertoire)
	{
		Collections.shuffle(progeny, rand);
		// process the progeny
		for(Cell childCell : progeny)
		{
			// Euclidean similarity tournament for competition
			// NO EXCLUSION
			Cell similar = CellUtils.getMostSimilarWithExclusion(childCell, repertoire, null, rand);
			// fitness tournament for resources
			if(antigen.isBetterOrSame(childCell, similar))
			{
				repertoire.remove(similar);
				repertoire.add(childCell);
			}
		}
	}
	
	@Override
	public Cell exposure(Antigen antigen)
	{		
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, cells);
		// select the activated set
		LinkedList<Cell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);			
		// cloning and mutation
		LinkedList<Cell> clones = CellUtils.cloningAndMutationCell(selectedSet, numClones, rand);
		// assess the clones against the antigen
		CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
		// replace the response into the repertoire
		replaceIntoRepertoire(antigen, clones, cells);
		// get the bmu
		Cell bmu = CellUtils.getRepertoireBMU(cells, rand);
		// return the bmu
		return bmu;
	}	
	
	
	@Override
	public String getName()
	{
		return "RCCSA-H";
	}
}

