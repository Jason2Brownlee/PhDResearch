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
package com.oat.domains.hosts.generation.maternal.algorithms;

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.generation.GenerationalHostClonalSelectionAlgorithm;
import com.oat.utils.AlgorithmUtils;

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
public class MaternalImmunityHostClonalSelectionAlgorithm extends
		GenerationalHostClonalSelectionAlgorithm
{
	// config
	protected int numMaternalCells = 10;
	
	@Override
	protected Host[] createNextGeneration(Host[] pop, HabitatProblem p)
	{
		// create new hosts as per normal
		Host [] nextegen = prepareHosts((Problem)p);
		
		if(numMaternalCells != 0)
		{
			// parent child
			for (int i = 0; i < nextegen.length; i++)
			{
				// clone a set of cells from parent
				LinkedList<Cell> parentCells = copyRandomCellPool(numMaternalCells, pop[i]);
				// integrate into child
				integrateSharedCells(nextegen[i], parentCells);
			}
		}
		
		return nextegen;
	}
	
	/**
	 * Helper to clone cells from a host
	 * @param poolSize
	 * @param host
	 * @return
	 */
	protected LinkedList<Cell> copyRandomCellPool(int poolSize, Host host)
	{
		LinkedList<Cell> cells = new LinkedList<Cell>();
		
		// draw the random sample with reselection
		LinkedList<Cell> repertoire = host.getRepertoire();		
		while(cells.size() < poolSize)
		{
			Cell copy = new Cell(repertoire.get(rand.nextInt(repertoire.size())));
			cells.add(copy);
		}
		
		return cells;
	}
	/**
	 * Helper to integrate a pool of cells into a host
	 * @param host
	 * @param cells
	 */
	protected void integrateSharedCells(Host host, LinkedList<Cell> newcells)
	{
		LinkedList<Cell> repertoire = host.getRepertoire();
		
		CellUtils.replaceIntoRepertoireSimilarity(newcells, repertoire, rand);
	}
	

	@Override
	public String getName()
	{
		return "Maternal Immunity Host Clonal Selection Algorithm (MI-HCSA)";
	}
	
	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{
		super.validateConfiguration();
		
		if(!AlgorithmUtils.inBounds(numMaternalCells, 0, Host.NUM_CELLS))
		{
			throw new InvalidConfigurationException("numMaternalCells must be >=0 and <= "+Host.NUM_CELLS+", " + numMaternalCells);
		}
	}
	public int getNumMaternalCells()
	{
		return numMaternalCells;
	}

	public void setNumMaternalCells(int numMaternalCells)
	{
		this.numMaternalCells = numMaternalCells;
	}	
}
