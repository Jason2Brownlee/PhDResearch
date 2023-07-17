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
package com.oat.domains.cells.network.algorithms.recurrent;

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
 * Description: Exposure and matching to the second bmu's second mapping
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
public abstract class RNCCSA extends
		GenericCellularClonalSelectionAlgorithm<NetworkCell> implements
		NetworkInteractionsCellularAlgorithm<NetworkCell>
{
	// config
	protected long seed = 1;
	protected int numCells = 100;
	protected int numSelected = 1;
	protected int numClones = 5; 
	// state
	protected Random rand;
	protected LinkedList<NetworkCell> cells;
	protected NetworkCell lastBmu;
	protected LinkedList<NetworkInteractionListener> listeners;
	
	public RNCCSA()
	{
		listeners = new LinkedList<NetworkInteractionListener>();
	}
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		lastBmu = null;
		rand = new Random(seed);
		AntigenProblem ap = (AntigenProblem) problem;
		cells = NetworkUtils.getRandomNetworkRepertoire(rand, numCells, ap.getNumBitsPerAntigen());
		// no initial population
		return null;
	}

	protected abstract NetworkCell exposureAntigen(Antigen antigen);	
	
	protected abstract NetworkCell exposureCell(NetworkCell aCell);


	@Override
	public Cell exposure(Antigen antigen)	
	{
		// expose to the antigen
		NetworkCell bmu = exposureAntigen(antigen);		
		// expose to the last bmu
		if(lastBmu != null)
		{
			NetworkCell bmu2 = exposureCell(lastBmu);
			triggerNetworkInteractionEvent(antigen, bmu, lastBmu, bmu2);
		}
		// replace the last bmu with the current bmu
		lastBmu = bmu;
		// return the current bmu
		return bmu;
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
	
	protected void triggerNetworkInteractionEvent(Antigen antigen1, NetworkCell bmu1, NetworkCell antigen2, NetworkCell bmu2)
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
		// selection size
		if(!AlgorithmUtils.inBounds(numSelected, 1, numCells))
		{
			throw new InvalidConfigurationException("numSelected must be between 1 and numCells ("+numCells+"): " + numSelected);
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

	public int getNumSelected()
	{
		return numSelected;
	}

	public void setNumSelected(int numSelected)
	{
		this.numSelected = numSelected;
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
