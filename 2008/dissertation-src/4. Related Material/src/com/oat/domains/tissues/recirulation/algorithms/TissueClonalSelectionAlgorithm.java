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
package com.oat.domains.tissues.recirulation.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.tissues.InfectionExposureListener;
import com.oat.domains.tissues.Infection;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.TissueAlgorithm;
import com.oat.utils.EvolutionUtils;

/**
 * Description: 
 *  
 * Date: 26/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class TissueClonalSelectionAlgorithm extends EpochAlgorithm<CellSet> 
	implements TissueAlgorithm<Cell>
{			
	// config
	protected int numTissues = 10;
	protected long seed = 1;
	protected int probabilistExposureDurationLength = 15;
	protected TER exposureMode = TER.Probabilistic;

	// general state
	protected Tissue [] tissues;
	protected Random rand;	
	protected LinkedList<InfectionExposureListener> listeners;
	// probabilistic exposure state
	protected double [][] histogram;
	protected int currentDurationLength;
	protected int [] currentSelection;	
	
	
	public TissueClonalSelectionAlgorithm()
	{
		listeners = new LinkedList<InfectionExposureListener>();
	}		
	
	protected void initialiseProbabilisticExposures(int numInfections)
	{
		currentDurationLength = 0;
		histogram = new double[numInfections][numTissues];		 
		currentSelection = new int[numInfections];
		Arrays.fill(currentSelection, -1);
	}

	public Cell repertoireExposureInfection(InfectionProblem p, int pattNo)
	{
		// check for a reset
		if(currentSelection[pattNo] == -1 || 
				currentDurationLength >= probabilistExposureDurationLength)
		{
			// make selection
			int selection = EvolutionUtils.biasedRouletteWheelSelection(histogram[pattNo], rand);
			// store selection
			currentSelection[pattNo] = selection;
			// reset count
			currentDurationLength = 0;
			// increment the frequency			
			histogram[pattNo][currentSelection[pattNo]]++;			
		}				
		
		// perform an exposure
		currentDurationLength++;		
		// simple one-to-one exposure scheme
		return doSpecificExposure(currentSelection[pattNo], p, pattNo);
	}

	
	public Cell repertoireExposureRandom(InfectionProblem p, int infectionNumber)
	{
		// select repertoire (wraps around the number of repertories)
		int repNo = rand.nextInt(tissues.length);
		// simple one-to-one exposure scheme
		return doSpecificExposure(repNo, p, infectionNumber);
	}
	
	public Cell repertoireExposurePoint(InfectionProblem p, int infectionNumber)
	{
		// always the same
		int repNo = 1;		
		// simple one-to-one exposure scheme
		return doSpecificExposure(repNo, p, infectionNumber);
	}
	
	public Cell repertoireExposureAsymmetric(InfectionProblem p, int infectionNumber)
	{
		// select repertoire (wraps around the number of repertories)
		int repNo = infectionNumber % numTissues;				
		// trigger exposure event
		return doSpecificExposure(repNo, p, infectionNumber);
	}
	
	public Cell repertoireExposureSymmetric(InfectionProblem p, int infectionNumber)
	{
		LinkedList<Cell> repertoireBMUs = new LinkedList<Cell>();
		
		// expose to each repertoire
		for (int i = 0; i < tissues.length; i++)
		{
			// exposure
			Cell bmu = doSpecificExposure(i, p, infectionNumber);
			// record
			repertoireBMUs.add(bmu);
		}
		
		Collections.shuffle(repertoireBMUs, rand); // random tie handling
		Collections.sort(repertoireBMUs); // order by affinity
		return repertoireBMUs.getFirst(); // return the best
	}
	
	
	protected Cell doSpecificExposure(int tissueNumber, InfectionProblem p, int infectionNumber)
	{
		// retrieve host
		Tissue tissue = tissues[tissueNumber];
		// exposure
		Infection infection = p.getInfection(infectionNumber);
		Cell bmu = tissue.exposure(infection);
		// trigger event
		triggerExposureEvent(tissueNumber, tissue, infectionNumber, infection);
		return bmu;
	}
	
	
	
	public Cell repertoireExposure(InfectionProblem p, int infectionNumber)
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
		InfectionProblem infection = (InfectionProblem) problem;
		initialiseProbabilisticExposures(infection.getNumInfections());
		// prepare tissues
		tissues = prepareTissues(problem);		
		return null;
	}	
	
	protected Tissue[] prepareTissues(Problem problem)
	{
		Tissue [] ccsas = new Tissue[numTissues];
		for (int i = 0; i < ccsas.length; i++)
		{
			// create
			ccsas[i] = new Tissue();
			// configure
			ccsas[i].setSeed(rand.nextLong());
			// initialise
			ccsas[i].manuallyInitialiseBeforeRun(problem);
		}
		
		return ccsas;
	}
	
	
	/**
	 * All about tissue-tissue interactions
	 * @param p
	 */
	public abstract void trafficLymphocytes(InfectionProblem p);
	
	
	public CellSet systemExposure(InfectionProblem p)
	{
		int numSubProblems = p.getNumInfections();		
		Cell [] bmus = new Cell[numSubProblems];
		// process each sub problem
		for (int i = 0; i < numSubProblems; i++)
		{
			bmus[i] = repertoireExposure(p, i);
		}		
		return new CellSet(bmus);
	}
	
	
	@Override
	protected LinkedList<CellSet> internalExecuteEpoch(Problem problem, LinkedList<CellSet> population)
	{
		InfectionProblem p = (InfectionProblem) problem;
		// perform exposure
		LinkedList<CellSet> nextgen = new LinkedList<CellSet>();
		CellSet set = systemExposure(p);
		nextgen.add(set);
		
		// traffic cells
		trafficLymphocytes(p);
		
		return nextgen;
	}

	
	public void registerExposureListener(InfectionExposureListener aListener)
	{
		listeners.add(aListener);
	}
	
	protected void triggerExposureEvent(int repNo, Tissue rep, int patNo, Infection pat)
	{
		for(InfectionExposureListener list : listeners)
		{
			list.exposure(repNo, rep, patNo, pat);
		}
	}
	
	public boolean removeExposureListener(InfectionExposureListener aListener)
	{
		return listeners.remove(aListener);
	}


	@Override
	protected void internalPostEvaluation(Problem problem, LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{
		for (int i = 0; i < tissues.length; i++)
		{
			tissues[i].manualPostEvaluation(problem, oldPopulation, newPopulation);
		}
	}

	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{		
		// master repertoire configuration
		if(numTissues<=0)
		{
			throw new InvalidConfigurationException("Number of tissues must be > 0, " + numTissues);
		}
		
		// cannot validate tissues because they are not *created* yet
	}

	

	

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}	

	public Tissue[] getTissues()
	{
		return tissues;
	}
	
	public TER getExposureMode()
	{
		return exposureMode;
	}

	public void setExposureMode(TER exposureMode)
	{
		this.exposureMode = exposureMode;
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
	
	public int getNumTissues()
	{
		return numTissues;
	}

	public void setNumTissues(int numTissues)
	{
		this.numTissues = numTissues;
	}
}
