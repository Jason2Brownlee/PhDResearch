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
public class VaccinationTransmissionAlgorithm extends
		PopulationHostClonalSelectionAlgorithm
{
	// config
	protected int vaccinationSize = 5;
	
	// state
	protected LinkedList<Integer> [] sampledExposuresLastEpoch;
	protected boolean ignoreExposures = false;
	
	
	public VaccinationTransmissionAlgorithm()
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
		public void exposure(int hostNumber, Host repertoire, int habitatNumber, Habitat habitat)
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
		randomSampleVaccination(p);		
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

	protected void randomSampleVaccination(HabitatProblem p)
	{
		// select a vaccinating host
		LinkedList<Integer> vaccinatingHosts = new  LinkedList<Integer>();
		for (int j = 0; j < sampledExposuresLastEpoch.length; j++)
		{
			if(!sampledExposuresLastEpoch[j].isEmpty())
			{
				vaccinatingHosts.add(j);
			}
		}
		if(vaccinatingHosts.isEmpty())
		{
			throw new AlgorithmRunException("Unable to do transmission, no sampled exposures!");
		}
		// select a host
		int vaccinatingHost = vaccinatingHosts.get(rand.nextInt(vaccinatingHosts.size()));		
		// select one of the exposures to transmit
		int selectedExposure = sampledExposuresLastEpoch[vaccinatingHost].get(rand.nextInt(sampledExposuresLastEpoch[vaccinatingHost].size()));
		// prepare a list of all other hosts that may be vaccinated besides the vaccinating host
		LinkedList<Integer> sample = new  LinkedList<Integer>();
		for (int j = 0; j < sampledExposuresLastEpoch.length; j++)
		{
			if(j != vaccinatingHost)
			{
				sample.add(j);
			}
		}
		// cull to size
		while(sample.size() > vaccinationSize)
		{
			sample.remove(rand.nextInt(sample.size()));
		}
		// do vaccinations (exposures)
		for (int j = 0; j < sample.size(); j++)
		{
			doSpecificExposure(sample.get(j), p, selectedExposure);
		}
	}
	
	@Override
	public String getName()
	{
		return "Vaccination Transmission Algorithm (VT-PHCSA)";
	}

	public int getVaccinationSize()
	{
		return vaccinationSize;
	}

	public void setVaccinationSize(int vaccinationSize)
	{
		this.vaccinationSize = vaccinationSize;
	}
}
