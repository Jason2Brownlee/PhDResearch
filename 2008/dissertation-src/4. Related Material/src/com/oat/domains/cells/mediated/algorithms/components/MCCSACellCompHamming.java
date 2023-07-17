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
package com.oat.domains.cells.mediated.algorithms.components;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.mediated.MediatedUtils;

/**
 * Description: 
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
public class MCCSACellCompHamming extends MCCSACellCompEuclidean
{
	
	@Override
	protected void accessComponentRepertoireAgainstRepertoire(
			LinkedList<SubCell> tCellSet, 
			LinkedList<Cell> bCellSet, 
			int component)
	{
		// euclidean distance
		MediatedUtils.assessComponentRepertoireAgainstRepertoireHamming(tCellSet, bCellSet, component);
	}
	
	@Override
	public String getName()
	{
		return "MCCSA Cell-to-Comp (Hamming)";
	}
}
