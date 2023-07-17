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
package com.oat.domains.hosts.population.sharing.algorithms;

import java.util.LinkedList;

import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.population.PopulationHostClonalSelectionAlgorithm;
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
public class SharedImmunityHostClonalSelectionAlgorithm extends
		PopulationHostClonalSelectionAlgorithm
{
	// paramters
	protected int numSharers = 1;
	protected int numRecipients = 5;
	protected int numSharedCells = 5;
	

	@Override
	public void hostInteractions(HabitatProblem p)
	{
		// check for enabled 
		if(numSharers > 0)
		{
			// select sharing hosts
			LinkedList<Host> sharingHosts = selectRandomSetOfHosts(numSharers);
			// do sharing 
			for(Host host : sharingHosts)
			{
				// select hosts to share with
				LinkedList<Host> recpipientHosts = selectRandomSetOfHosts(numRecipients);
				// perform sharing
				shareCells(host, recpipientHosts);
			}
		}
	}

	protected LinkedList<Host> selectRandomSetOfHosts(int aNumHosts)
	{
		if(aNumHosts > hosts.length)
		{
			throw new AlgorithmRunException("The desired number of hosts ("+aNumHosts+") exceeds the number of hosts in the population ("+hosts.length+").");
		}
		
		// select hosts to participate
		LinkedList<Host> sharingHosts = new LinkedList<Host>();
		// add all
		for (int i = 0; i < hosts.length; i++)
		{
			sharingHosts.add(hosts[i]);
		}
		// shrink to size
		while(sharingHosts.size() > aNumHosts)
		{
			sharingHosts.remove(rand.nextInt(sharingHosts.size()));
		}
		
		return sharingHosts;
	}
	
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
	
	protected void integrateSharedCells(Host host, LinkedList<Cell> newcells)
	{
		LinkedList<Cell> repertoire = host.getRepertoire();
		
		CellUtils.replaceIntoRepertoireSimilarity(newcells, repertoire, rand);
		
	}
	
	protected void shareCells(Host host, LinkedList<Host> recpipientHosts)
	{
		for(Host receptHost : recpipientHosts)
		{
			// select a pool of cells from the host
			LinkedList<Cell> pool = copyRandomCellPool(numSharedCells, host);
			// integrate into the recept host
			integrateSharedCells(receptHost, pool);
		}
	}	
	
	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{
		super.validateConfiguration();
		
		if(!AlgorithmUtils.inBounds(numSharers, 0, numHosts))
		{
			throw new InvalidConfigurationException("numSharers must be >= 0 and <= numhosts="+numHosts);
		}
		// check for enabled
		if(numSharers != 0)
		{
			if(!AlgorithmUtils.inBounds(numSharedCells, 1, Host.NUM_CELLS))
			{
				throw new InvalidConfigurationException("numSharedCells must be >= 1 and <= repertoireSize="+Host.NUM_CELLS);
			}			
			if(!AlgorithmUtils.inBounds(numRecipients, 1, numHosts))
			{
				throw new InvalidConfigurationException("numRecipients must be >= 1 and <= numHosts="+numHosts);
			}
		}		
	}
	
	
	@Override
	public String getName()
	{
		return "Shared Imminity Host Clonal Selection Algorithm (SI-HCSA)";
	}

	public int getNumSharers()
	{
		return numSharers;
	}

	public void setNumSharers(int numSharers)
	{
		this.numSharers = numSharers;
	}

	public int getNumRecipients()
	{
		return numRecipients;
	}

	public void setNumRecipients(int numRecipients)
	{
		this.numRecipients = numRecipients;
	}

	public int getNumSharedCells()
	{
		return numSharedCells;
	}

	public void setNumSharedCells(int numSharedCells)
	{
		this.numSharedCells = numSharedCells;
	}
}
