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
package com.oat.domains.cells.spatial.algorithms.components;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.spatial.SpatialCell;
import com.oat.domains.cells.spatial.SpatialSubCell;
import com.oat.domains.cells.spatial.SpatialUtils;

/**
 * Description: Component BottomUp
 * Select components from anywhere within the component repertoire, replacements must occur
 * at the minimised intersection of all three component repertoires.
 * Trying to improve nighborhood-based organisation at both scales 
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
public class SCCSA_BottomUp extends SCCSA_TopDown
{
	
	@Override
	public Cell exposure(Antigen antigen)
	{				
		SpatialSubCell [] bmuBits = new SpatialSubCell[repertoire.length];
		
		// assess all repertoiers
		for (int i = 0; i < repertoire.length; i++)
		{
			// assess the components
			assessComponentRepertoire(repertoire[i], antigen);
		}
		// build primary sites for replacement
		LinkedList<DummySolution> replacementSites = selectCoordinates();
		
		// process each component
		for (int i = 0; i < repertoire.length; i++)
		{
			// compress the component repertoire
			LinkedList<SpatialSubCell> rep = compressComponentRepertoire(repertoire[i]);
			// selection
			LinkedList<SpatialSubCell> selectedSet = CellUtils.selectCellSet(rep, rand, numSelected);
			// remember the best selected for the bmu
			bmuBits[i] = selectedSet.getFirst();						
			// process independantly
			for (int j = 0; j < selectedSet.size(); j++)
			{
				SpatialSubCell selectedCell = selectedSet.get(j);			
				// cloning and mutation
				LinkedList<SpatialSubCell> clones = SpatialUtils.cloningAndMutationSpatialSubCell(selectedCell, numClones, rand);
				// assess the clones against the antigen
				CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
				// select the replacement site for clones
				DummySolution ro = replacementSites.get(j);
				SpatialSubCell replacementSite = repertoire[i][ro.x][ro.y];
				// replace clones into the neighbourhood
				replaceIntoSpatialRepertoire(replacementSite, clones, repertoire[i]);
			}
		}
		
		return DegenerateUtils.cellFromSubCells(bmuBits);		
	}	
}
