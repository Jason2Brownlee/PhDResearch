/*
 Optimization Algorithm Toolkit (OAT)
 http://sourceforge.net/projects/optalgtoolkit
 Copyright (C) 2006  Jason Brownlee

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.stopcondition;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.stopcondition.GeneticSolutionEvaluatedStopCondition;

/**
 * 
 * Description: 
 *  
 * Date: 30/10/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class FoundOptima extends GeneticSolutionEvaluatedStopCondition
{
	public final static double MIN_DISTANCE = 0.0001;	
		
	// state
	protected boolean optimaFound;
	
	
	
	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
	}

	@Override
	protected void initialiseBeforeRunInternal(Problem p, Algorithm a)
		throws InitialisationException
	{
		super.initialiseBeforeRunInternal(p, a);
		optimaFound = false;
	}	
	
	@Override
	public String getName()
	{
		return "Found Optimal Score";
	}

	@Override
	public boolean mustStopInternal()
	{
		return optimaFound;
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		if(!optimaFound)
		{
			// check for spot on 
			if(evaluatedSolution.getScore() == 0.0)
			{
				optimaFound = true; 
			}
			// check for really close
			if(evaluatedSolution.getScore() <= MIN_DISTANCE)
			{
				optimaFound = true; 
			}
		}
	}
}
