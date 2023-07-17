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
package com.oat.domains.tissues;

import java.util.LinkedList;

import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.algorithms.RCCSA;


/**
 * Description: 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee
 * @param <C> 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class Tissue extends RCCSA
{
	/**
	 * Nominal number of cells in a tissue
	 */
	public final static int NUM_CELLS = 50;
	
	
	/**
	 * Fixed config
	 */
	public Tissue()
	{
		super(NUM_CELLS, 1, 5);
	}
	
	
	@Override
	public String getName()
	{
		return "Tissue";
	}	
	
	/**
	 * Each infection is an antigen of 1
	 * @param infection
	 * @return
	 */
	public Cell exposure(Infection infection)
	{
		return exposure(infection, 0);
	}

	/**
	 * Manual initialisation, for use outside of the framework
	 * @param problem
	 */
	public void manuallyInitialiseBeforeRun(Problem problem)
	{
		internalInitialiseBeforeRun(problem);
	}
	
	/**
	 * Manual post evaluation 
	 * @param problem
	 * @param oldPopulation
	 * @param newPopulation
	 */
	public void manualPostEvaluation(Problem problem, LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{
		internalPostEvaluation(problem, oldPopulation, newPopulation);
	}
}
