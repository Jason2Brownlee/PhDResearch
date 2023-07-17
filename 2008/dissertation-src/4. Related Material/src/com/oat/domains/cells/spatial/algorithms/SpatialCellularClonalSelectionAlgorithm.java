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
package com.oat.domains.cells.spatial.algorithms;

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
import com.oat.domains.cells.spatial.SpatialCell;
import com.oat.domains.cells.spatial.SpatialCellularAlgorithm;
import com.oat.domains.cells.spatial.SpatialUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: Spatial Cellular Clonal Selection Algorithm (SCCSA) 
 * Uses neighbourhood based replacement
 *  
 * Date: 04/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SpatialCellularClonalSelectionAlgorithm extends GenericCellularClonalSelectionAlgorithm<SpatialCell> 
	implements SpatialCellularAlgorithm<SpatialCell> 
{
	// config
	protected long seed = 1;
	protected int numCells = 10; 
	protected int numClones = 5; 	
	protected int numSelected = 2;
	
	// data
	protected Random rand;
	protected SpatialCell [][] repertoire;
	

	/**
	 * Neighbourhood replacement
	 */
	protected void replaceIntoSpatialRepertoire(
			SpatialCell parentCell, 
			LinkedList<SpatialCell> progenySet, 
			Antigen antigen)
	{
		// locate the parental neighbourhood
		LinkedList<SpatialCell> neighbourhood = SpatialUtils.getNeighbours(parentCell, repertoire);
		
		// process the progeny
		for(SpatialCell childCell : progenySet)
		{
			// locate a cell to replace
			SpatialCell similar = CellUtils.getMostSimilarWithExclusion(childCell, neighbourhood, progenySet, rand);
			// fitness tournament for resources
			if(antigen.isBetterOrSame(childCell, similar))
			{
				int [] coord = similar.getCoordinate();
				repertoire[coord[0]][coord[1]] = childCell;
				childCell.setCoordinate(coord);
				// also do it in the fake repertoire
				neighbourhood.remove(similar);
				neighbourhood.add(childCell);
			}
		}
	}	
	
	@Override
	public Cell exposure(Antigen antigen)
	{		
		LinkedList<SpatialCell> cells = getRepertoire();
		
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, cells);
		// select the activated set
		LinkedList<SpatialCell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);	
		
		// process independantly
		for(SpatialCell selectedCell : selectedSet)
		{
			// cloning and mutation
			LinkedList<SpatialCell> clones = SpatialUtils.cloningAndMutationSpatialCell(selectedCell, numClones, rand);
			// assess the clones against the antigen
			CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
			// replace the response into the repertoire
			replaceIntoSpatialRepertoire(selectedCell, clones, antigen);
		}
		
		// get the bmu (SLOW!)
		Cell bmu = CellUtils.getRepertoireBMU(getRepertoire(), rand);
		// return the bmu
		return bmu;
	}

	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		repertoire = new SpatialCell[numCells][numCells];
		
		AntigenProblem ap = (AntigenProblem) problem;					
		for (int i = 0; i < repertoire.length; i++)
		{
			for (int j = 0; j < repertoire[i].length; j++)
			{
				boolean [] data = RandomUtils.randomBitString(rand, ap.getNumBitsPerAntigen());
				repertoire[i][j] = new SpatialCell(data);
				repertoire[i][j].setCoordinate(new int[]{i, j});
			}
		}		
		
		// no initial population
		return null;
	}

	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}

	@Override
	public SpatialCell[][] getSpatialRepertoire()
	{
		return repertoire;
	}

	@Override
	public Random getRandom()
	{
		return rand;
	}

	@Override
	public LinkedList<SpatialCell> getRepertoire()
	{
		LinkedList<SpatialCell> tmp = new LinkedList<SpatialCell>();
		
		for (int i = 0; i < repertoire.length; i++)
		{
			for (int j = 0; j < repertoire[i].length; j++)
			{
				tmp.add(repertoire[i][j]);
			}
		}
		
		return tmp;
	}

	@Override
	public boolean isCellBased()
	{
		return true;
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
		return "Spatial Cellular Clonal Selection Algorithm (SCCSA) ";
	}

	public int getNumClones()
	{
		return numClones;
	}

	public void setNumClones(int numClones)
	{
		this.numClones = numClones;
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

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}
}
