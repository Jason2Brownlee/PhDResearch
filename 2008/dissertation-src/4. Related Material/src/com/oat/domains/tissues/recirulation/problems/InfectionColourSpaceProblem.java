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
package com.oat.domains.tissues.recirulation.problems;

import java.util.Random;

import com.oat.InitialisationException;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
import com.oat.domains.tissues.Infection;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class InfectionColourSpaceProblem extends AntigenColourSpaceProblem
	implements InfectionProblem
{	

	@Override
	public void initialiseBeforeRun() throws InitialisationException
	{
		Random rand = new Random(seed);				
		// create the random set
		boolean [][] set = RandomUtils.randomBitStringSet(rand, Antigen.numGoalStateBits(), numAntigen, MIN_ANTIGEN_DISTANCE);				
		// create the problems
		antigen = new Infection[numAntigen];
		for (int i = 0; i < numAntigen; i++)
		{
			// create
			antigen[i] = new Infection();
			// initialise
			antigen[i].initialiseManually(set[i]);
		}
	}

	@Override
	public String getName()
	{
		return "Infection Colour Space Problem (ACSP)";
	}

	@Override
	public Infection getInfection(int infectionNumber)
	{
		return (Infection) antigen[infectionNumber];
	}

	@Override
	public Infection[] getInfections()
	{
		return (Infection[]) antigen;
	}

	@Override
	public int getNumInfections()
	{
		return numAntigen;
	}
}
