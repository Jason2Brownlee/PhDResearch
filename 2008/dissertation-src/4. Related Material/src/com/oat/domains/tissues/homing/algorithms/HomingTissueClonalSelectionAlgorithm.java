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
package com.oat.domains.tissues.homing.algorithms;

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.homing.HomingCell;
import com.oat.domains.tissues.recirulation.algorithms.TissueClonalSelectionAlgorithm;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Specialization of RTCSA
 * - Homing cells and Homing RCCSA
 * - preferential migration based on preferential tissue id
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
public class HomingTissueClonalSelectionAlgorithm extends TissueClonalSelectionAlgorithm
{
	// new config
	protected int migrationSize = 5;
	protected double preferenceProbability = 0.80; // 80% chance of staying	
	protected int numCellsToImprint = 1;
	


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
			migrants[i] = selectMigrants(tissues[i].getRepertoire(), ((HomingTissue)tissues[i]).getTissueId());
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
	
	protected LinkedList<Cell> selectMigrants(LinkedList<Cell> repertoire, int repertoireId)
	{
		LinkedList<Cell> migrants = new LinkedList<Cell>();
		
		// support for differential migration based on preference		
		while(migrants.size() < migrationSize)
		{
			// select a random cell
			int selectionIndex = rand.nextInt(repertoire.size());
			HomingCell selectedCell = (HomingCell) repertoire.get(selectionIndex);
			boolean migrate = false;
			// check for no preference
			if(selectedCell.getPreferredRepertoireNumber() == HomingCell.NO_PREFERENCE)
			{
				migrate = true; // no pref
			}
			// check for no expressed preference
			else if(selectedCell.getPreferredRepertoireNumber() != repertoireId)
			{
				migrate = true; // does not apply
			}
			else if(rand.nextDouble() > preferenceProbability)
			{
				// preference is expressed but ignored
				migrate = true; // does not apply
			}
			// else preference is asserted			
			
			// do migration
			if(migrate)
			{
				// remove from repertoire and store in buffer
				migrants.add(repertoire.remove(selectionIndex));
			}
		}
		
		return migrants;
	}
	
	@Override
	protected Tissue [] prepareTissues(Problem problem)
	{
		HomingTissue [] ccsas = new HomingTissue[numTissues];
		for (int i = 0; i < ccsas.length; i++)
		{
			// create
			ccsas[i] = new HomingTissue(i+1);
			// configure
			ccsas[i].setSeed(rand.nextLong());
			// new config
			ccsas[i].setNumCellsToImprint(numCellsToImprint); 
			// initialise
			ccsas[i].internalInitialiseBeforeRun(problem);
		}
		
		return ccsas;
	}
	
	
	@Override
	public void validateConfiguration() 
		throws InvalidConfigurationException
	{
		super.validateConfiguration();
		
		if(!AlgorithmUtils.inBounds(migrationSize, 0, Tissue.NUM_CELLS))
		{
			throw new InvalidConfigurationException("Invalid migrationSize "+migrationSize+", expect between 0 and "+Tissue.NUM_CELLS);
		}
		else if(!AlgorithmUtils.inBounds(preferenceProbability, 0, 1))
		{
			throw new InvalidConfigurationException("Invalid preferenceProbability "+preferenceProbability+", expect between 0 and 1.");
		}
		else if(!AlgorithmUtils.inBounds(numCellsToImprint, 0, Tissue.NUM_CELLS))
		{
			throw new InvalidConfigurationException("Invalid numCellsToImprint "+numCellsToImprint+", expect between 0 and "+Tissue.NUM_CELLS);
		}
	}	

	@Override
	public String getName()
	{
		return "Homing Tissue Clonal Selection Algorithm (HTCSA)";
	}

	public int getMigrationSize()
	{
		return migrationSize;
	}

	public void setMigrationSize(int migrationSize)
	{
		this.migrationSize = migrationSize;
	}

	public double getPreferenceProbability()
	{
		return preferenceProbability;
	}

	public void setPreferenceProbability(double preferenceProbability)
	{
		this.preferenceProbability = preferenceProbability;
	}

	public int getNumCellsToImprint()
	{
		return numCellsToImprint;
	}

	public void setNumCellsToImprint(int numCellsToImprint)
	{
		this.numCellsToImprint = numCellsToImprint;
	}
}
