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
package com.oat.domains.cells.cellular.algorithms;

import java.util.LinkedList;
import java.util.Random;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Replacement Cellular Clonal Selection Algorithm (RCCSA) 
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
public class ReplacementClonalSelectionAlgorithm extends GenericCellularClonalSelectionAlgorithm<Cell>
{
	// config
	protected long seed = 1;
	protected int numCells = 100;
	protected int numSelected = 2;
	protected int numClones = 5; 
	// data
	protected Random rand;
	protected LinkedList<Cell> cells;

	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		AntigenProblem ap = (AntigenProblem) problem;
		cells = CellUtils.getRandomCellRepertoire(rand, numCells, ap.getNumBitsPerAntigen());
		// no initial population
		return null;
	}	
	
	@Override
	public boolean isCellBased()
	{
		return true;
	}

	@Override
	public LinkedList<Cell> getRepertoire()
	{	
		return cells;
	}
		
	
	@Override
	public Cell exposure(Antigen antigen)
	{		
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, cells);
		// select the activated set
		LinkedList<Cell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);			
		// cloning and mutation
		LinkedList<Cell> clones = CellUtils.cloningAndMutationCell(selectedSet, numClones, rand);
		// assess the clones against the antigen
		CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
		// replace the response into the repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		// get the bmu
		Cell bmu = CellUtils.getRepertoireBMU(cells, rand);
		// return the bmu
		return bmu;
	}		

	@Override
	protected void internalPostEvaluation(Problem problem, LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}
	
	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
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
		return "Replacement Cellular Clonal Selection Algorithm (RCCSA)";
	}

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}
	
	@Override
	public Random getRandom()
	{
		return rand;
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
}

