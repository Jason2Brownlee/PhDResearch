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
package com.oat.domains.hosts.population.transmission.algorithms;

import java.util.LinkedList;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatExposureListener;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.population.PopulationHostClonalSelectionAlgorithm;

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
public class PathogenTransmissionAlgorithm extends
		PopulationHostClonalSelectionAlgorithm
{
	public static enum TRANSMISSION_METHOD {RandomPairings, SpatialRandom}
	
	// config
	protected TRANSMISSION_METHOD transmissionMethod = TRANSMISSION_METHOD.RandomPairings;
	
	// state
	protected LinkedList<Integer> [] sampledExposuresLastEpoch;
	protected boolean ignoreExposures = false;
	
	
	public PathogenTransmissionAlgorithm()
	{
		this.registerExposureListener(new InternalExposureListener());
	}
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		LinkedList<CellSet> result = super.internalInitialiseBeforeRun(problem);
		sampledExposuresLastEpoch = new LinkedList[numHosts];
		for (int i = 0; i < sampledExposuresLastEpoch.length; i++)
		{
			sampledExposuresLastEpoch[i] = new LinkedList<Integer>();
		}
		ignoreExposures = false;
		return result;
	}
	
	protected class InternalExposureListener implements HabitatExposureListener 
	{
		@Override
		public void exposure(int hostNumber, Host repertoire,
				int habitatNumber, Habitat habitat)
		{
			sampleExposure(hostNumber, habitatNumber);			
		}		
	}	

	@Override
	public void hostInteractions(HabitatProblem p)
	{
		// ignore exposures
		ignoreExposures = true;
		
		// do transmission		
		switch(transmissionMethod)
		{
			case RandomPairings:
			{
				randomPairingsTransmission(p);
				break;
			}
			case SpatialRandom:
			{
				spatialRandomTransmission(p);
				break;
			}
			default:
			{
				throw new AlgorithmRunException("Unknown transmission method: " + transmissionMethod);
			}
		}
		
		// no longer ignore exposures
		ignoreExposures = false;
		
		// clear last sampled exposures		
		for (int i = 0; i < sampledExposuresLastEpoch.length; i++)
		{
			sampledExposuresLastEpoch[i].clear();
		}
	}
	
	protected void sampleExposure(int hostNumber, int habitatNumber)
	{
		if(ignoreExposures)
		{
			return;
		}
		
		sampledExposuresLastEpoch[hostNumber].add(habitatNumber);
	}	

	
	protected void randomPairingsTransmission(HabitatProblem p)
	{
		for (int i = 0; i < sampledExposuresLastEpoch.length; i++)
		{
			if(!sampledExposuresLastEpoch[i].isEmpty())
			{
				// select one of the exposures to transmit
				int selectedExposure = sampledExposuresLastEpoch[i].get(rand.nextInt(sampledExposuresLastEpoch[i].size()));
				// select one of the other hosts besides this host
				int otherHost = rand.nextInt(numHosts);
				while(otherHost == i)
				{
					otherHost = rand.nextInt(numHosts);
				}
				// do the exposure
				doSpecificExposure(otherHost, p, selectedExposure);
			}
		}
	}
	
	protected void spatialRandomTransmission(HabitatProblem p)
	{
		for (int i = 0; i < sampledExposuresLastEpoch.length; i++)
		{
			if(!sampledExposuresLastEpoch[i].isEmpty())
			{
				// select one of the exposures to transmit
				int selectedExposure = sampledExposuresLastEpoch[i].get(rand.nextInt(sampledExposuresLastEpoch[i].size()));
				// select one of the other hosts besides this host
				boolean left = rand.nextBoolean();
				int otherHost = -1;
				// select the other host
				if(left)
				{
					if(i == sampledExposuresLastEpoch.length-1)
					{
						otherHost = 0;
					}
					else
					{
						otherHost = i+1;
					}
				}
				// right
				else
				{
					if(i == 0)
					{
						otherHost = sampledExposuresLastEpoch.length-1;
					}
					else
					{
						otherHost = i-1;
					}
				}
				// do the exposure
				doSpecificExposure(otherHost, p, selectedExposure);
			}
		}
	}
	


	@Override
	public String getName()
	{
		return "Pathogen Transmission Algorithm (PT-PHCSA)";
	}

	public TRANSMISSION_METHOD getTransmissionMethod()
	{
		return transmissionMethod;
	}

	public void setTransmissionMethod(TRANSMISSION_METHOD transmissionMethod)
	{
		this.transmissionMethod = transmissionMethod;
	}
}
