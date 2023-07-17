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
public class RCCSA_H_EP extends ReplacementClonalSelectionAlgorithm
{	
	public RCCSA_H_EP()
	{
		numCells = 60; 
		numSelected = 2; 
		numClones = 3; 
		// clones per antigen will be 10% (6)
	}
	
	@Override
	public String getName()
	{
		return "RCCSA-H-EP";
	}
}

