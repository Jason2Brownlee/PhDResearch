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

import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.ReplacementClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.mediated.MediatedUtils;
import com.oat.domains.cells.network.NetworkInteractionListener;
import com.oat.domains.cells.network.NetworkInteractionsCellularAlgorithm;

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
public class RNCCSA_NM extends ReplacementClonalSelectionAlgorithm
	implements NetworkInteractionsCellularAlgorithm<Cell>
{	
	protected Cell lastBmu;
	protected LinkedList<NetworkInteractionListener> listeners;
	
	
	/**
	 * 
	 */
	public RNCCSA_NM()
	{
		listeners = new LinkedList<NetworkInteractionListener>();
		numCells = 100;
		numSelected = 1;
		numClones = 5; 	 
	}
	
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		lastBmu = null;
		return super.internalInitialiseBeforeRun(problem);
	}
	
	
	protected Cell expose(Cell aCell)
	{		
		// assess repertoire
		MediatedUtils.assessRepertoireAgainstCellEuclidean(cells, aCell);		
		// select the activated set
		LinkedList<Cell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);	
		Cell bmu = CellUtils.getRepertoireBMU(selectedSet, rand);
		// cloning and mutation
		LinkedList<Cell> clones = CellUtils.cloningAndMutationCell(selectedSet, numClones, rand);
		// assess the clones against the antigen
		MediatedUtils.assessRepertoireAgainstCellEuclidean(clones, aCell);
		// replace the response into the repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		
		return bmu;
	}

	@Override
	public Cell exposure(Antigen antigen)	
	{
		// expose to the antigen
		Cell bmu = super.exposure(antigen);		
		// expose to the last bmu
		if(lastBmu != null)
		{
			Cell bmu2 = expose(lastBmu);
			triggerNetworkInteractionEvent(antigen, bmu, lastBmu, bmu2);
		}
		// replace the last bmu with the current bmu
		lastBmu = bmu;
		// return the current bmu
		return bmu;
	}	
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}


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
	
	protected void triggerNetworkInteractionEvent(Antigen antigen1, Cell bmu1, Cell antigen2, Cell bmu2)
	{		
		for(NetworkInteractionListener l : listeners)
		{
			l.interaction(this, antigen1, bmu1, antigen2, bmu2);
		}
	}
}
