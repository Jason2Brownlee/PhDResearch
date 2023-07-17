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
package com.oat.domains.cells.network.algorithms.dualexposure;

import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkCellularAlgorithm;
import com.oat.domains.cells.network.NetworkInteractionListener;
import com.oat.domains.cells.network.NetworkUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Decoupled Exposures
 *  
 * Date: 12/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class DE_NCCSA extends EpochAlgorithm<CellSet> implements NetworkCellularAlgorithm<NetworkCell>
{
	// config
	protected long seed = 1;
	protected int numCells = 100;
	protected int numSelected = 1;
	protected int numClones = 10; 
	// state
	protected Random rand;
	protected LinkedList<NetworkCell> cells;
	protected LinkedList<NetworkInteractionListener> listeners;

	
	public DE_NCCSA()
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
	
	
	protected abstract NetworkCell primaryExposure(Antigen primaryAntigen, Antigen secondaryAntigen);
	protected abstract NetworkCell secondaryExposure(Antigen primaryAntigen, Antigen secondaryAntigen);
	
	protected Cell [] exposure(Antigen a1, Antigen a2)
	{
		Cell [] bmus = new Cell[2]; 	
		// exposure
		NetworkCell b1 = primaryExposure(a1, a2);
		NetworkCell b2 = secondaryExposure(a1, a2);
		// create bmus
		bmus[0] = b1;
		bmus[1] = new Cell(b2.getData2());
		
		return bmus;
	}
	

	@Override
	protected final LinkedList<CellSet> internalExecuteEpoch(Problem problem, LinkedList<CellSet> cp)
	{
		AntigenProblem p = (AntigenProblem) problem;
		int numAntigen = p.getNumAntigen();	
		
		if((numAntigen%2)!=0)
		{
			throw new AlgorithmRunException("Expect an even number of antigen, " + numAntigen%2);
		}		
		
		Cell [] bmus = new Cell[numAntigen];		
		// process each antigen
		for (int i = 0; i < numAntigen; i+=2)
		{
			Cell [] tmp = exposure(p.getAntigen(i), p.getAntigen(i+1));			
			bmus[i] = tmp[0];
			bmus[i+1] = tmp[1];
		}		
		// create a cell set
		LinkedList<CellSet> nextgen = new LinkedList<CellSet>();
		nextgen.add(new CellSet(bmus));
		return nextgen;
	}	
	

	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}

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
