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

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.domains.tissues.Tissue;
import com.oat.utils.AlgorithmUtils;

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
public class RecirculationTissueClonalSelectionAlgorithm extends MinimalTissueClonalSelectionAlgorithm
{
	// config
	protected int migrationSize = 5;
	
	
	@Override
	public void trafficLymphocytes(InfectionProblem p)
	{
		if(migrationSize<=0)
		{
			return;
		}
		
		LinkedList<Cell> [] migrants = new LinkedList[tissues.length];
		
		// select migrants
		for (int i = 0; i < tissues.length; i++)
		{
			migrants[i] = selectMigrants(tissues[i].getRepertoire());
		}
		
		// insert migrants
		for (int i = 0; i < tissues.length; i++)
		{
			LinkedList<Cell> target = tissues[i].getRepertoire();
			LinkedList<Cell> source = null;
			
			if(i == 0)
			{
				source = migrants[migrants.length-1];
			}
			else if(i == migrants.length-1)
			{
				source = migrants[0];
			}
			else
			{
				source = migrants[i + 1];
			}
		
			target.addAll(source);
		}
	}
	
	protected LinkedList<Cell> selectMigrants(LinkedList<Cell> repertoire)
	{
		LinkedList<Cell> migrants = new LinkedList<Cell>();
		
		while(migrants.size() < migrationSize)
		{
			// random selection
			migrants.add(repertoire.remove(rand.nextInt(repertoire.size())));
		}
		
		return migrants;
	}


	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{
		super.validateConfiguration();
		
		if(!AlgorithmUtils.inBounds(migrationSize, 0, Tissue.NUM_CELLS))
		{
			throw new InvalidConfigurationException("Invalid migration size "+migrationSize+", expect between 0 and "+Tissue.NUM_CELLS);
		}
	}

	@Override
	public String getName()
	{
		return "Recirulation Tissue Clonal Selection Algorithm (RTCSA)";
	}

	public int getMigrationSize()
	{
		return migrationSize;
	}

	public void setMigrationSize(int migrationSize)
	{
		this.migrationSize = migrationSize;
	}
}
