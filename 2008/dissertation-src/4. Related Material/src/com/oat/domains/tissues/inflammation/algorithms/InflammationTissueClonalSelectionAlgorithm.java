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
package com.oat.domains.tissues.inflammation.algorithms;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.tissues.InfectionExposureListener;
import com.oat.domains.tissues.Infection;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.recirulation.algorithms.RecirculationTissueClonalSelectionAlgorithm;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: 
 *  
 * Date: 21/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class InflammationTissueClonalSelectionAlgorithm extends
		RecirculationTissueClonalSelectionAlgorithm
{
	// config
	protected int maximumRepertoireSize = Tissue.NUM_CELLS+10; // 60
	
	
	public InflammationTissueClonalSelectionAlgorithm()
	{
		// register special listener
		registerExposureListener(new InternalExposureListener());
	}
	
	protected class InternalExposureListener implements InfectionExposureListener
	{
		@Override
		public void exposure(int repertoireNumber, Tissue repertoire, int patternNumber, Infection antigen)
		{
			// increase selected repertoire size
			increaseRepertoireSizeToMaximum(repertoire);
		}		
	}
	
	@Override
	public CellSet systemExposure(InfectionProblem p)
	{
		// execute system exposures
		CellSet result = super.systemExposure(p);
		// decrease repertoire sizes
		decreaseRepertoireSizes();
		// return the result
		return result;
	}
	
	protected void increaseRepertoireSizeToMaximum(Tissue repertoire)
	{
		repertoire.setNumCells(maximumRepertoireSize);		
	}
	
	protected void decreaseRepertoireSizes()
	{
		for (int i = 0; i < tissues.length; i++)			
		{
			// check for an assigned size larger than the default
			int currentSize = tissues[i].getNumCells();			
			if(currentSize > Tissue.NUM_CELLS)
			{
				// decrease allocated size
				int allocatedSize = currentSize - 1;
				tissues[i].setNumCells(allocatedSize);
				
				// check if used size exceeds allocated size
				while(tissues[i].getRepertoire().size() > allocatedSize)
				{
					// delete a random element
					int selection = rand.nextInt(tissues[i].getRepertoire().size());
					tissues[i].getRepertoire().remove(selection);
				}
			}
			
			// check for invalid sizes
			if(!AlgorithmUtils.inBounds(tissues[i].getNumCells(), Tissue.NUM_CELLS, maximumRepertoireSize))
			{
				throw new AlgorithmRunException("Invalid configured repertoire size " + tissues[i].getNumCells());
			}
			if(!AlgorithmUtils.inBounds(tissues[i].getRepertoire().size(), Tissue.NUM_CELLS, maximumRepertoireSize))
			{
				throw new AlgorithmRunException("Invalid actual repertoire size " + tissues[i].getRepertoire().size());
			}
		}
	}	
	
	
	protected Tissue [] prepareTissues(Problem problem)
	{
		InflammationTissue [] ccsas = new InflammationTissue[numTissues];
		for (int i = 0; i < ccsas.length; i++)
		{
			// create
			ccsas[i] = new InflammationTissue();
			// configure
			ccsas[i].setSeed(rand.nextLong());
			// initialise (hack)
			ccsas[i].manuallyInitialiseBeforeRun(problem);
		}
		
		return ccsas;
	}


	@Override
	public String getName()
	{
		return "Inflammation Tissue Clonal Selection Algorithm (ITCSA)";
	}


	public int getMaximumRepertoireSize()
	{
		return maximumRepertoireSize;
	}
	public void setMaximumRepertoireSize(int maximumRepertoireSize)
	{
		this.maximumRepertoireSize = maximumRepertoireSize;
	}
	
}
