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
package com.oat.domains.cells.cellular.algorithms.replacement;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.CellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;

/**
 * Description: 
 *  
 * Date: 03/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CCSANNSample extends CellularClonalSelectionAlgorithm
{
	public CCSANNSample()
	{
		numCells = 60; 
		numSelected = 2; 
		numClones = 3; 
		// clones per antigen will be 10% (6)
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
		
		// select a larger set
		int competitionSize = clones.size();
		selectedSet = CellUtils.selectCellSet(cells, rand, competitionSize);	
		// displace the selected set
		cells.removeAll(selectedSet);
		// union the selected set and the clones
		clones.addAll(selectedSet); // 12
		// select the BMU
		Cell bmu = CellUtils.getRepertoireBMU(clones,  rand);	
		// reduce clonal set to the right size
		clones = CellUtils.selectCellSet(clones, rand, competitionSize);	
		// insert back into the repertoire
		cells.addAll(clones);
		
		// return the bmu
		return bmu;
	}	
	
	
	@Override
	public String getName()
	{
		return "CCSA(N+N)-GS";
	}
}
