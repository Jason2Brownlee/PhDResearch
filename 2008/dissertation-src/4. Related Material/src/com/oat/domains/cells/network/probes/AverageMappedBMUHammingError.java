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
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkCellularAlgorithm;
import com.oat.domains.cells.network.NetworkInteractionListener;
import com.oat.domains.cells.network.NetworkInteractionsCellularAlgorithm;
import com.oat.domains.cells.network.NetworkUtils;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: 
 *  
 * Date: 16/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageMappedBMUHammingError extends GenericEpochCompletedProbe implements NetworkInteractionListener
{
	protected double error = Double.NaN;
	protected NetworkInteractionsCellularAlgorithm algorithm;	
	
	protected double sumEuclideanError;
	protected int numPairs;

	
	@Override
	public Object getProbeObservation()
	{
		return new Double(error);
	}
	
	@Override
    public void initialiseBeforeRunInternal(Problem p, Algorithm a) throws InitialisationException
	{
		super.initialiseBeforeRunInternal(p, a);
		algorithm = null;
		if(a instanceof NetworkInteractionsCellularAlgorithm)
		{
			algorithm = (NetworkInteractionsCellularAlgorithm) a;
			algorithm.registerNetworkInteractionListener(this);
		}
	}	
	
	@Override
	public void cleanupAfterRun(Problem p, Algorithm a)	throws InitialisationException
	{
		super.cleanupAfterRun(p, a);
		if(algorithm != null)
		{
			if(!algorithm.removeNetworkInteractionListener(this))
			{
				throw new InitialisationException("Unable to remove NetworkInteractionListener, not registered.");
			}
		}
	}

	@Override
	public void reset()
	{
		sumEuclideanError = 0;
		numPairs = 0;
		error = Double.NaN;
	}
	
	
	@Override
	public <C extends Cell> void interaction(
			NetworkCellularAlgorithm<C> algorithm, 
			Antigen antigen1, 
			C bmu1,
			C antigen2, 
			C bmu2)
	{
		if(algorithm.isCellBased())
		{
			sumEuclideanError += CellUtils.hammingDistance((Cell)antigen2, (Cell)bmu2);
		}
		else
		{
			// network cell goes in second on this method
			sumEuclideanError += NetworkUtils.hammingDistanceAgainstMapping((Cell)bmu2, (NetworkCell)antigen2);
		}		
		numPairs++;		
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(algorithm != null)
		{
			error = (sumEuclideanError / numPairs);
			sumEuclideanError = 0.0;
			numPairs = 0;
		}
	}	

	@Override
	public String getName()
	{
		return "Average Mapped BMU Hamming Error (AMBMUHE)";
	}
}
