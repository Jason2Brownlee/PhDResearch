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
package com.oat.domains.cells.network.algorithms.proxy;

import java.util.LinkedList;
import java.util.Random;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.GenericCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkInteractionListener;
import com.oat.domains.cells.network.NetworkInteractionsCellularAlgorithm;
import com.oat.domains.cells.network.NetworkUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: 
 *  
 * Date: 06/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class PNCCSA extends
		GenericCellularClonalSelectionAlgorithm<NetworkCell> implements
		NetworkInteractionsCellularAlgorithm<NetworkCell>
{
	// config
	protected long seed = 1;
	protected int numCells = 100;
	protected int numClones = 5; 
	// state
	protected Random rand;
	protected LinkedList<NetworkCell> cells;
	protected LinkedList<NetworkInteractionListener> listeners;
	
	public PNCCSA()
	{
		listeners = new LinkedList<NetworkInteractionListener>();
	}
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		AntigenProblem ap = (AntigenProblem) problem;
		cells = NetworkUtils.getRandomNetworkRepertoire(rand, numCells, ap.getNumBitsPerAntigen());
		// no initial population
		return null;
	}
	
	
	protected NetworkCell exposureAntigen(Antigen antigen)
	{		
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, cells);
		return CellUtils.getRepertoireBMU(cells, rand);
	}
	protected NetworkCell exposureCell(NetworkCell aCell)
	{		
		// assess repertoire against mapping
		NetworkUtils.assessRepertoireAgainstCellMappingEuclidean(cells, aCell);	
		return CellUtils.getRepertoireBMU(cells, rand);
	}
	
	protected abstract void respondFirstBMU(Antigen antigen, NetworkCell bmu1, NetworkCell bmu2);
	
	
	//protected abstract void respondSecondBMU(Antigen antigen, NetworkCell bmu1, NetworkCell bmu2);
	
	
	protected void respondSecondBMU(Antigen antigen, NetworkCell bmu1, NetworkCell bmu2)
	{		
		LinkedList<NetworkCell> clones = NetworkUtils.cloningAndMutationNetwork(bmu2, numClones, rand);
		// assess based on mapping to the first bmu
		NetworkUtils.assessRepertoireAgainstCellMappingEuclidean(cells, bmu1);
		NetworkUtils.assessRepertoireAgainstCellMappingEuclidean(clones, bmu1);
		// replace based on primary string
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
	}
	

	@Override
	public Cell exposure(Antigen antigen)	
	{
		// first exposure
		NetworkCell bmu1 = exposureAntigen(antigen);		
		NetworkCell bmu2 = exposureCell(bmu1);
		// respond
		respondFirstBMU(antigen, bmu1, bmu2);
		respondSecondBMU(antigen, bmu1, bmu2);
		// trigger the event
		triggerNetworkInteractionEvent(antigen, bmu1, bmu1, bmu2);
		// return the second cell
		return bmu2;
	}


	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}

	@Override
	public void registerNetworkInteractionListener(NetworkInteractionListener l)
	{
		listeners.add(l);		
	}

	@Override
	public boolean removeNetworkInteractionListener(NetworkInteractionListener l)
	{
		return listeners.remove(l);
	}
	
	protected void triggerNetworkInteractionEvent(
			Antigen antigen1, 
			NetworkCell bmu1, 
			NetworkCell antigen2, 
			NetworkCell bmu2)
	{		
		for(NetworkInteractionListener l : listeners)
		{
			l.interaction(this, antigen1, bmu1, antigen2, bmu2);
		}
	}

	@Override
	public Random getRandom()
	{
		return rand;
	}

	@Override
	public LinkedList<NetworkCell> getRepertoire()
	{
		return cells;
	}

	@Override
	public boolean isCellBased()
	{
		return false;
	}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		// num cells
		if(numCells<=0)
		{
			throw new InvalidConfigurationException("numCells must be > 0.");
		}
		// cloning size
		if(numClones<=0)
		{
			throw new InvalidConfigurationException("numClones must be > 0.");
		}
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
	
	
	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}

	public int getNumCells()
	{
		return numCells;
	}

	public void setNumCells(int numCells)
	{
		this.numCells = numCells;
	}
	
	public int getNumClones()
	{
		return numClones;
	}

	public void setNumClones(int numClones)
	{
		this.numClones = numClones;
	}
}
