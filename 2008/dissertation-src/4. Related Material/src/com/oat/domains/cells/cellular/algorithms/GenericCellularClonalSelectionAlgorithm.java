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
package com.oat.domains.cells.cellular.algorithms;

import java.util.LinkedList;

import com.oat.EpochAlgorithm;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellularAlgorithm;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.problems.Antigen;


/**
 * Description: Cellular Clonal Selection Algorithm (CCSA)
 * It is all about exposures to sets of antigen. A system does not know how many (it may), and it does not know
 * the order of exposure, just that it must respond the best that it can.
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
public abstract class GenericCellularClonalSelectionAlgorithm<C extends DiscreteCell> extends EpochAlgorithm<CellSet> implements CellularAlgorithm<C>
{	
	/**
	 * Expose the system to information to which it must respond
	 * @param antigen
	 * @return
	 */
	public abstract Cell exposure(Antigen antigen);
	
	/**
	 * Decouple the antigen ordering from the exposure.
	 * @param ap
	 * @param antigenNumber
	 * @return
	 */
	public final Cell exposure(AntigenProblem ap, int antigenNumber)
	{		
		Antigen antigen = ap.getAntigen(antigenNumber);
		return exposure(antigen);
	}	
	
	@Override
	protected final LinkedList<CellSet> internalExecuteEpoch(Problem problem, LinkedList<CellSet> cp)
	{
		AntigenProblem p = (AntigenProblem) problem;
		int numAntigen = p.getNumAntigen();			
		Cell [] bmus = new Cell[numAntigen];		
		// process each antigen
		for (int i = 0; i < bmus.length; i++)
		{
			bmus[i] = exposure(p, i);
		}		
		// create a cell set
		LinkedList<CellSet> nextgen = new LinkedList<CellSet>();
		nextgen.add(new CellSet(bmus));
		return nextgen;
	}	
}
