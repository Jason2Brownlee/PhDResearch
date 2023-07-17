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
package com.oat.stopcondition;

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.stopcondition.GenericEpochStopCondition;

/**
 * Description: 
 *  
 * Date: 31/10/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class FoundOptimaOrMaxEpochs extends GenericEpochStopCondition
{
	public final static double MIN_DISTANCE = 0.0001;
	
	// config
	protected long maxEpochs = 1000;
	
	// state
	protected long epochCount;
	protected boolean mustStop;
	
	public FoundOptimaOrMaxEpochs()
	{}
	
	public FoundOptimaOrMaxEpochs(int aMaxEpochs)
	{
		setMaxEpochs(aMaxEpochs);
	}
	
	
	public void reset()
	{
		super.reset();
		epochCount = 0; // 1-n
		mustStop = false;
	}

	@Override
	public boolean mustStopInternal()
	{
		return mustStop;
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{
		// another epoch has occured
		epochCount++;
		
		if(mustStop)
		{
			return; // do nothing
		}
		
		// check things
		if(epochCount >= maxEpochs)
		{
			mustStop = true;
		}
		else
		{
			// scan population for an optima
			for(Solution s : currentPop)
			{
				double score = s.getScore();
				if(score == 0 || score <= MIN_DISTANCE)
				{
					mustStop = true;
					break;
				}
			}
		}
	}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		if(maxEpochs <= 0)
		{
			throw new InvalidConfigurationException("Invalid maxEpochs" + maxEpochs);
		}

	}

	@Override
	public String getName()
	{
		return "Epochs or Optima Stop Condition (EOSC)";
	}

	public long getMaxEpochs()
	{
		return maxEpochs;
	}

	public void setMaxEpochs(long maxEpochs)
	{
		this.maxEpochs = maxEpochs;
	}
}
