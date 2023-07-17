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
package com.oat.domains.tissues.inflammation.algorithms;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.tissues.Tissue;

/**
 * Description: 
 *  
 * Date: 21/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class InflammationTissue extends Tissue
{
	
	
	public void localReplacement(Antigen antigen, LinkedList<Cell> progeny, LinkedList<Cell> repertoire)
	{		
		for(Cell childCell : progeny)
		{
			if(repertoire.size() < numCells)
			{
				// simply add
				repertoire.add(childCell);
			}
			// make room
			else			
			{	
				CellUtils.replaceIntoRepertoireSimilarityScore(childCell, repertoire, progeny, rand);
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
		localReplacement(antigen, clones, cells);
		// get the bmu
		Cell bmu = CellUtils.getRepertoireBMU(cells, rand);
		// return the bmu
		return bmu;
	}
	
	
	@Override
	public String getName()
	{
		return "Inflammation Tissue";
	}
}
