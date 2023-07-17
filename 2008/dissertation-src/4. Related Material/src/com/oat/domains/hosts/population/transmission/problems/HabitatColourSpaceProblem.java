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
package com.oat.domains.hosts.population.transmission.problems;

import java.util.Random;

import com.oat.InitialisationException;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 24/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HabitatColourSpaceProblem extends AntigenColourSpaceProblem
	implements HabitatProblem
{	
	@Override
	public void initialiseBeforeRun() throws InitialisationException
	{
		Random rand = new Random(seed);				
		// create the random set
		boolean [][] set = RandomUtils.randomBitStringSet(rand, Antigen.numGoalStateBits(), numAntigen, MIN_ANTIGEN_DISTANCE);				
		// create the problems
		antigen = new Habitat[numAntigen];
		for (int i = 0; i < numAntigen; i++)
		{
			// create
			antigen[i] = new Habitat();
			// initialise
			antigen[i].initialiseManually(set[i]);
		}
	}

	@Override
	public String getName()
	{
		return "Habitat Colour Space Problem (HCSP)";
	}

	@Override
	public Habitat[] getHabitats()
	{
		return (Habitat[]) antigen;
	}

	@Override
	public Habitat getHabitat(int habitatNumber)
	{
		return (Habitat) antigen[habitatNumber];
	}

	@Override
	public int getNumHabitats()
	{
		return numAntigen;
	}
}
