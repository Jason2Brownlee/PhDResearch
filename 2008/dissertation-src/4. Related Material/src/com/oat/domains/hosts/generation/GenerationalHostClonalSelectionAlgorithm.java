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
package com.oat.domains.hosts.generation;

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.population.PopulationHostClonalSelectionAlgorithm;

/**
 * Description: 
 *  
 * Date: 25/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class GenerationalHostClonalSelectionAlgorithm extends PopulationHostClonalSelectionAlgorithm
{
	// config
	protected int epochsPerGenerationalChange = 100;
	
	// state
	protected int epochCount;
	protected LinkedList<GenerationalChangeListener> generationListeners;
	

	
	public GenerationalHostClonalSelectionAlgorithm()
	{
		generationListeners = new LinkedList<GenerationalChangeListener>();
	}
	
	public void registerGenerationalChangeListener(GenerationalChangeListener l)
	{
		generationListeners.add(l);
	}	
	public boolean removeGenerationalChangeListener(GenerationalChangeListener l)
	{
		return generationListeners.remove(l);
	}	
	public void triggerGenerationalChangeEvent(Host [] oldGeneration, Host [] newGeneration)
	{
		for(GenerationalChangeListener l : generationListeners)
		{
			l.generationChange(oldGeneration, newGeneration);
		}
	}
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{		
		// reset
		epochCount = 0;
		// do parent init
		return super.internalInitialiseBeforeRun(problem);
	}
	
	
	@Override
	public void hostInteractions(HabitatProblem p)
	{	
		// an epoch has just completed
		epochCount++;
		// check stop condition
		if((epochCount % epochsPerGenerationalChange) == 0)
		{
			// perform generational change
			Host [] nextgen = createNextGeneration(hosts, p);
			// trigger an event
			triggerGenerationalChangeEvent(hosts, nextgen);
			// replace
			hosts = nextgen;	
		}
	}
	
	/**
	 * Create the next generation from the current generation
	 * @param pop
	 * @param p
	 * @return
	 */
	protected abstract Host [] createNextGeneration(Host [] pop, HabitatProblem p);
	
	

	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{
		super.validateConfiguration();
		
		if(epochsPerGenerationalChange <= 0)
		{
			throw new InvalidConfigurationException("epochsPerGenerationalChange must be >= 1, "+epochsPerGenerationalChange);
		}
	}
	
	
	public int getEpochsPerGenerationalChange()
	{
		return epochsPerGenerationalChange;
	}

	public void setEpochsPerGenerationalChange(int epochsPerGenerationalChange)
	{
		this.epochsPerGenerationalChange = epochsPerGenerationalChange;
	}
}
