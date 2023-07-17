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
package com.oat.domains.cells.mediated.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.mediated.MediatedCellularAlgorithm;
import com.oat.domains.cells.mediated.RepertoireInteractionListener;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: Average T-Cell Mapping Error (ATCME)
 *  
 * Date: 05/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageTCellMappingError extends GenericEpochCompletedProbe
	implements RepertoireInteractionListener
{
	protected double error = Double.NaN;
	protected MediatedCellularAlgorithm algorithm;
	
	// state
	protected double sumError;
	protected int numInteractions;
	

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
		
		if(a instanceof MediatedCellularAlgorithm)
		{			
			algorithm = (MediatedCellularAlgorithm) a;
			// register
			algorithm.registerRepertoireInteractionListener(this);
		}
	}	
	
	@Override
	public void cleanupAfterRun(Problem p, Algorithm a)	throws InitialisationException
	{
		super.cleanupAfterRun(p, a);
		
		if(algorithm != null)
		{
			if(!algorithm.removeRepertoireInteractionListener(this))
			{
				throw new RuntimeException("Unalbe to deregister interaction listener, not registered to begin with.");
			}
		}
	}
	
	

	@Override
	public <B extends DiscreteCell, T extends DiscreteCell> void interaction(
			Antigen antigen, 
			MediatedCellularAlgorithm<B,T> algorithm, 
			LinkedList<B> selectedBCells, 
			LinkedList<T> selectedTCells)
	{	
		// calculate
		double d = 0.0;
		LinkedList<T> allTCells = algorithm.getTCells();
		for(T c : allTCells)
		{
			d += c.getScore();
		}
		
		// store
		sumError += (d/allTCells.size());
		numInteractions++;
	}

	@Override
	public void reset()
	{
		error = Double.NaN;
		sumError = 0;
		numInteractions = 0;
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(algorithm != null)
		{
			error = sumError / numInteractions;
			sumError = 0;
			numInteractions = 0;
		}
	}

	@Override
	public String getName()
	{
		return "Average T-Cell Mapping Error (ATCME)";
	}
}
