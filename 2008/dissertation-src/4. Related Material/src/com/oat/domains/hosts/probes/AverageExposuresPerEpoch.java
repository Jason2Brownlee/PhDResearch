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
package com.oat.domains.hosts.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatExposureListener;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.HostAlgorithm;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: Average Exposures Per Epoch (AEPE) 
 *  
 * Date: 10/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageExposuresPerEpoch extends GenericEpochCompletedProbe implements HabitatExposureListener
{
	protected boolean enabled;
	protected long numExposures;
	protected long numEpochs;
	

	@Override
	public Object getProbeObservation()
	{
		return new Double(numExposures / numEpochs);
	}
	
	@Override
    public void initialiseBeforeRunInternal(Problem p, Algorithm a) throws InitialisationException
	{
		super.initialiseBeforeRunInternal(p, a);
		
		
		if(a instanceof HostAlgorithm)
		{
			enabled = true;
			((HostAlgorithm)a).registerExposureListener(this);
		}
		else
		{
			enabled = false;
		}
	}	

	@Override
	public void cleanupAfterRun(Problem p, Algorithm a)	throws InitialisationException
	{		
		super.cleanupAfterRun(p, a);
		
		if(enabled)
		{
			if(!((HostAlgorithm)a).removeExposureListener(this))
			{
				throw new InitialisationException("Unable to remove exposure listener from algorithm, does not exist as listener for algorithm."); 
			}
		}
	}
	
	@Override
	public void reset()
	{
		numExposures = 0;
		numEpochs = 0;
	}

	

	@Override
	public void exposure(int hostNumber, Host host, int habitatNumber, Habitat habitat)
	{
		if(enabled)
		{
			numExposures++;
		}
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(enabled)
		{
			numEpochs++;
		}
	}

	@Override
	public String getName()
	{
		return "Average Exposures Per Epoch";
	}
}
