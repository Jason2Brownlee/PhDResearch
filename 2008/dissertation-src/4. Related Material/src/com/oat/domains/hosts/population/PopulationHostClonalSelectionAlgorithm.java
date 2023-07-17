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
package com.oat.domains.hosts.population;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.hosts.HER;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatExposureListener;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.HostAlgorithm;
import com.oat.domains.tissues.InfectionExposureListener;
import com.oat.utils.EvolutionUtils;

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
public abstract class PopulationHostClonalSelectionAlgorithm extends EpochAlgorithm<CellSet> 
	implements HostAlgorithm
{		
	// config
	protected int numHosts = 10;
	protected long seed = 1;
	protected int probabilistExposureDurationLength = 15;
	protected HER exposureMode = HER.Probabilistic;

	// general state
	protected Host [] hosts;
	protected Random rand;	
	protected LinkedList<HabitatExposureListener> listeners;
	// probabilistic exposure state
	protected double [][] histogram;
	protected int currentDurationLength;
	protected int [] currentSelection;	
	
	
	public PopulationHostClonalSelectionAlgorithm()
	{
		listeners = new LinkedList<HabitatExposureListener>();
	}		
	
	protected void initialiseProbabilisticExposures(int numHabitats)
	{
		currentDurationLength = 0;
		histogram = new double[numHabitats][numHosts];		 
		currentSelection = new int[numHabitats];
		Arrays.fill(currentSelection, -1);
	}

	public Cell repertoireExposureInfection(HabitatProblem p, int habitatNumber)
	{
		// check for a reset
		if(currentSelection[habitatNumber] == -1 || 
				currentDurationLength >= probabilistExposureDurationLength)
		{
			// make selection
			int selection = EvolutionUtils.biasedRouletteWheelSelection(histogram[habitatNumber], rand);
			// store selection
			currentSelection[habitatNumber] = selection;
			// reset count
			currentDurationLength = 0;
			// increment the frequency			
			histogram[habitatNumber][currentSelection[habitatNumber]]++;			
		}				
		
		// perform an exposure
		currentDurationLength++;		
		// simple one-to-one exposure scheme
		return doSpecificExposure(currentSelection[habitatNumber], p, habitatNumber);
	}

	
	public Cell repertoireExposureRandom(HabitatProblem p, int habitatNumber)
	{
		// select repertoire (wraps around the number of repertories)
		int repNo = rand.nextInt(numHosts);
		// simple one-to-one exposure scheme
		return doSpecificExposure(repNo, p, habitatNumber);
	}
	
	public Cell repertoireExposurePoint(HabitatProblem p, int habitatNumber)
	{
		// always the same
		int repNo = 1;		
		// simple one-to-one exposure scheme
		return doSpecificExposure(repNo, p, habitatNumber);
	}
	
	public Cell repertoireExposureAsymmetric(HabitatProblem p, int habitatNumber)
	{
		// select repertoire (wraps around the number of repertories)
		int repNo = habitatNumber % numHosts;				
		// trigger exposure event
		return doSpecificExposure(repNo, p, habitatNumber);
	}
	
	public Cell repertoireExposureSymmetric(HabitatProblem p, int habitatNumber)
	{
		LinkedList<Cell> repertoireBMUs = new LinkedList<Cell>();
		
		// expose to each repertoire
		for (int i = 0; i < hosts.length; i++)
		{
			// exposure
			Cell bmu = doSpecificExposure(i, p, habitatNumber);
			// record
			repertoireBMUs.add(bmu);
		}
		
		Collections.shuffle(repertoireBMUs, rand); // random tie handling
		Collections.sort(repertoireBMUs); // order by affinity
		return repertoireBMUs.getFirst(); // return the best
	}
	
	
	protected Cell doSpecificExposure(int hostNumber, HabitatProblem p, int habitatNumber)
	{
		// retrieve host
		Host host = hosts[hostNumber];
		// exposure
		Habitat habitat = p.getHabitat(habitatNumber);
		Cell bmu = host.exposure(habitat);
		// trigger event
		triggerExposureEvent(hostNumber, host, habitatNumber, habitat);
		return bmu;
	}
	
	
	
	public Cell exposure(HabitatProblem p, int infectionNumber)
	{
		Cell bmu = null;
		
		switch(exposureMode)
		{			
		case Asymmetric:
			bmu = repertoireExposureAsymmetric(p, infectionNumber);
			break;
		
		case Symmetric:
			bmu = repertoireExposureSymmetric(p, infectionNumber);
			break;
			
		case Random:
			bmu = repertoireExposureRandom(p, infectionNumber);
			break;
			
		case Point:
			bmu = repertoireExposurePoint(p, infectionNumber);
			break;
			
		case Probabilistic: 
			bmu = repertoireExposureInfection(p, infectionNumber);
			break;
			
		default:
			throw new RuntimeException("Invalid exposure mode: " + exposureMode);
		}
		
		return bmu;
	}

	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		// prepare exposure things
		HabitatProblem p = (HabitatProblem) problem;
		initialiseProbabilisticExposures(p.getNumHabitats());
		// prepare tissues
		hosts = prepareHosts(problem);		
		return null;
	}	
	
	protected Host[] prepareHosts(Problem problem)
	{
		Host [] ccsas = new Host[numHosts];
		for (int i = 0; i < ccsas.length; i++)
		{
			// create
			ccsas[i] = new Host();
			// configure
			ccsas[i].setSeed(rand.nextLong());
			// initialise
			ccsas[i].manuallyInitialiseBeforeRun(problem);
		}
		
		return ccsas;
	}
	
	
	/**
	 * All about host-host interactions
	 * @param p
	 */
	public abstract void hostInteractions(HabitatProblem p);
	
	
	public CellSet systemExposure(HabitatProblem p)
	{
		int numSubProblems = p.getNumHabitats();		
		Cell [] bmus = new Cell[numSubProblems];
		// process each sub problem
		for (int i = 0; i < numSubProblems; i++)
		{
			bmus[i] = exposure(p, i);
		}		
		return new CellSet(bmus);
	}
	
	
	@Override
	protected LinkedList<CellSet> internalExecuteEpoch(Problem problem, LinkedList<CellSet> population)
	{
		HabitatProblem p = (HabitatProblem) problem;
		// perform exposure
		LinkedList<CellSet> nextgen = new LinkedList<CellSet>();
		CellSet set = systemExposure(p);
		nextgen.add(set);		
		// special population things
		hostInteractions(p);		
		return nextgen;
	}

	
	public void registerExposureListener(HabitatExposureListener aListener)
	{
		listeners.add(aListener);
	}
	
	protected void triggerExposureEvent(int hostNumber, Host host, int habitatNumber, Habitat habitat)
	{
		for(HabitatExposureListener list : listeners)
		{
			list.exposure(hostNumber, host, habitatNumber, habitat);
		}
	}
	
	public boolean removeExposureListener(HabitatExposureListener aListener)
	{
		return listeners.remove(aListener);
	}


	@Override
	protected void internalPostEvaluation(Problem problem, LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{
		for (int i = 0; i < hosts.length; i++)
		{
			hosts[i].manualPostEvaluation(problem, oldPopulation, newPopulation);
		}
	}
	
	@Override
	public Host[] getHosts()
	{
		return hosts;
	}

	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{	
		// master repertoire configuration
		if(numHosts<=0)
		{
			throw new InvalidConfigurationException("numHosts must be > 0.");
		}
		
		// cannot validate Tissues because they are not created yet
	}

	public int getNumHosts()
	{
		return numHosts;
	}

	public void setNumHosts(int numHosts)
	{
		this.numHosts = numHosts;
	}

	public int getProbabilistExposureDurationLength()
	{
		return probabilistExposureDurationLength;
	}

	public void setProbabilistExposureDurationLength(
			int probabilistExposureDurationLength)
	{
		this.probabilistExposureDurationLength = probabilistExposureDurationLength;
	}

	public HER getExposureMode()
	{
		return exposureMode;
	}

	public void setExposureMode(HER exposureMode)
	{
		this.exposureMode = exposureMode;
	}
}
