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
package com.oat.domains.cells.spatial.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.spatial.SpatialSubCell;
import com.oat.domains.cells.spatial.SpatialUtils;
import com.oat.domains.cells.spatial.algorithms.SCCSAComponentsAggregation;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: Average neighbourhood diversity, averaged across all neighbourhoods, averaged across each component
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
public class AverageSubCellNeighbourhoodDiversity extends GenericEpochCompletedProbe
{
	protected double diversity = Double.NaN;
	protected SCCSAComponentsAggregation algorithm;

	@Override
	public Object getProbeObservation()
	{
		return new Double(diversity);
	}

	@Override
	public void reset()
	{
		diversity = Double.NaN;
	}
	
	@Override
    public void initialiseBeforeRunInternal(Problem p, Algorithm a) throws InitialisationException
	{
		super.initialiseBeforeRunInternal(p, a);
		algorithm = null;
		if(a instanceof SCCSAComponentsAggregation)
		{
			algorithm = (SCCSAComponentsAggregation) a;			
		}
	}	

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{
		if(algorithm == null)
		{
			return;
		}		
		
		// get the spatail repertoire
		SpatialSubCell [][][] rep = algorithm.getSpatialSubCells();
		double master = 0.0;
		for (int k = 0; k < rep.length; k++)
		{
			double sum = 0;
			int numNeighbouroods = 0;
			// process the thing
			for (int i = 0; i < rep.length; i++)
			{
				for (int j = 0; j < rep[i].length; j++)
				{
					SpatialSubCell current = rep[k][i][j];
					// locate neighbourhood
					LinkedList<SpatialSubCell> neigh = SpatialUtils.getNeighbours(current, rep[k]);
					// calculate the average cell diversity in the neighbourhood
					sum += CellUtils.averageDistance(neigh);
					numNeighbouroods++;
				}
			}
			
			// average across all neighbourhoods
			master +=(sum / numNeighbouroods);
		}

		// average across all components
		diversity = master/rep.length;
	}

	@Override
	public String getName()
	{
		return "Average SubCell Neighbourhood Diversity (ASCND)";
	}
}
