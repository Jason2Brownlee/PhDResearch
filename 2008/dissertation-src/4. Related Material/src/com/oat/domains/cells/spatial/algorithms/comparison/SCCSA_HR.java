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
package com.oat.domains.cells.spatial.algorithms.comparison;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.spatial.SpatialCell;

/**
 * Description: Repertoire-wide replacement, no spatial neighbourhoods 
 *  
 *  Holistic Replacement (HR)
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
public class SCCSA_HR extends SCCSA_NR
{
	@Override
	protected void replaceIntoSpatialRepertoire(SpatialCell parentCell, LinkedList<SpatialCell> progenySet, Antigen antigen)
	{
		LinkedList<SpatialCell> cells = getRepertoire();
		
		// process the progeny
		for(SpatialCell childCell : progenySet)
		{
			// locate a cell to replace
			SpatialCell similar = CellUtils.getMostSimilarWithExclusion(childCell, cells, progenySet, rand);
			// fitness tournament for resources
			if(antigen.isBetterOrSame(childCell, similar))
			{
				int [] coord = similar.getCoordinate();
				repertoire[coord[0]][coord[1]] = childCell;
				childCell.setCoordinate(coord);				
				// also do it in the fake repertoire
				cells.remove(similar);
				cells.add(childCell);
			}
		}
	}
}
