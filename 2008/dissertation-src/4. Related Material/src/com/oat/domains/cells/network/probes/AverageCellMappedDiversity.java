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
package com.oat.domains.cells.network.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkCellularAlgorithm;
import com.oat.domains.cells.network.NetworkUtils;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: 
 *  
 * Date: 13/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageCellMappedDiversity extends GenericEpochCompletedProbe
{
	protected double diversity = Double.NaN;
	protected NetworkCellularAlgorithm<NetworkCell> algorithm;

	@Override
	public Object getProbeObservation()
	{
		return new Double(diversity);
	}
	
	@Override
    public void initialiseBeforeRunInternal(Problem p, Algorithm a) throws InitialisationException
	{
		super.initialiseBeforeRunInternal(p, a);
		algorithm = null;
		if(a instanceof NetworkCellularAlgorithm)
		{
			if(!((NetworkCellularAlgorithm) a).isCellBased())
			{
				algorithm = (NetworkCellularAlgorithm) a;
			}
		}
	}	

	@Override
	public void reset()
	{
		diversity = Double.NaN;
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(algorithm != null)
		{
			// get the repertoire
			LinkedList<NetworkCell> repertoire = algorithm.getRepertoire();
			// calculate the diversity
			if(repertoire != null)
			{
				diversity = NetworkUtils.averageMappedHammingDistance(repertoire);
			}
		}
	}

	@Override
	public String getName()
	{
		return "Average Cell Mapped Diversity (ACMD)";
	}
}
