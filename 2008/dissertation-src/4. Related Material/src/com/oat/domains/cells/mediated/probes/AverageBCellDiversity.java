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
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.CellularAlgorithm;
import com.oat.domains.cells.mediated.MediatedCellularAlgorithm;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageBCellDiversity extends GenericEpochCompletedProbe
{
	protected double diversity = Double.NaN;
	protected MediatedCellularAlgorithm algorithm;

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
		
		if(a instanceof MediatedCellularAlgorithm)
		{
			algorithm = (MediatedCellularAlgorithm) a;
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
			diversity = CellUtils.averageDistance(algorithm.getBCells());
		}
	}

	@Override
	public String getName()
	{
		return "Average B-Cell Diversity (ABCD)";
	}
}
