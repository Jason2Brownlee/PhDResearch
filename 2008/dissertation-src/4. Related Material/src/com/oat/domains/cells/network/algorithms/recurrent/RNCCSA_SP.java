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

import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkUtils;

/**
 * 
 * Description: 
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
public class RNCCSA_SP extends RNCCSA
{
	@Override
	protected NetworkCell exposureAntigen(Antigen trigger)
	{
		CellUtils.assessRepertoireAgainstAntigen(trigger, cells);
		LinkedList<NetworkCell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);
		NetworkCell bmu = CellUtils.getRepertoireBMU(selectedSet, rand);
		LinkedList<NetworkCell> clones = NetworkUtils.cloningAndMutationNetwork(selectedSet, numClones, rand);
		// assess secondary
		NetworkUtils.assessMappedRepertoireAgainstAntigen(trigger, clones);
		NetworkUtils.assessMappedRepertoireAgainstAntigen(trigger, cells);
		// replace secondary
		NetworkUtils.replaceIntoRepertoireMappingHammingScore(clones, cells, rand);		
		return bmu;
	}

	@Override
	protected NetworkCell exposureCell(NetworkCell trigger)
	{
		NetworkUtils.assessRepertoireAgainstCellMappingEuclidean(cells, trigger);		
		LinkedList<NetworkCell> selectedSet = CellUtils.selectCellSet(cells, rand, numSelected);	
		NetworkCell bmu = CellUtils.getRepertoireBMU(selectedSet, rand);
		LinkedList<NetworkCell> clones = NetworkUtils.cloningAndMutationNetwork(selectedSet, numClones, rand);
		// assess primary
		NetworkUtils.assessRepertoireAgainstCellMappingEuclidean(clones, trigger);
		// replace primary
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
		return bmu;	
	}
}
