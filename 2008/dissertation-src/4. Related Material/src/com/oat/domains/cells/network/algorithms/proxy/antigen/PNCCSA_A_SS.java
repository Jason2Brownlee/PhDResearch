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
package com.oat.domains.cells.network.algorithms.proxy.antigen;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkUtils;
import com.oat.domains.cells.network.algorithms.proxy.PNCCSA;

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
public class PNCCSA_A_SS extends PNCCSA
{
	@Override
	protected void respondFirstBMU(Antigen antigen, NetworkCell bmu1, NetworkCell bmu2)
	{
		LinkedList<NetworkCell> clones = NetworkUtils.cloningAndMutationNetwork(bmu1, numClones, rand);
		// assess secondary against antigen
		NetworkUtils.assessMappedRepertoireAgainstAntigen(antigen, cells);
		NetworkUtils.assessMappedRepertoireAgainstAntigen(antigen, clones);		
		// replace based on primary string
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, cells, rand);
	}
}
