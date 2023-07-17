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
package com.oat.domains.cells.degenerate.problems;

import java.util.Random;

import com.oat.InitialisationException;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
import com.oat.domains.cells.degenerate.DegenerateCell;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.utils.RandomUtils;

/**
 * 
 * Description: 
 *  
 * Date: 09/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DegenerateAntigenColourSpaceProblem extends AntigenColourSpaceProblem
{
	
	@Override
	public void initialiseBeforeRun() throws InitialisationException
	{
		Random rand = new Random(seed);				
		// create the random set
		boolean [][] set = RandomUtils.randomBitStringSet(rand, Antigen.numGoalStateBits(), numAntigen, MIN_ANTIGEN_DISTANCE);				
		// create the problems
		antigen = new Antigen[numAntigen];
		for (int i = 0; i < numAntigen; i++)
		{
			// create
			antigen[i] = new DegenerateAntigen();
			// initialise
			antigen[i].initialiseManually(set[i]);
		}
	}
	
	/**
	 * Cost of a sub cell
	 * @param solution
	 * @param antigenNumber
	 * @return
	 */
	public double costSubCell(SubCell solution, int antigenNumber)		
	{
		return ((DegenerateAntigen)antigen[antigenNumber]).costSubCell(solution);		
	}
	
	/**
	 * Cost of a degenerate cell
	 * @param solution
	 * @param antigenNumber
	 * @return
	 */
	public double costDegenerateCell(DegenerateCell solution, int antigenNumber)		
	{
		return ((DegenerateAntigen)antigen[antigenNumber]).costDegenerateCell(solution);		
	}

	@Override
	public String getName()
	{
		return "Degenerate Antigen Colour Space Problem (DACSP)";
	}
}
