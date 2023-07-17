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
package com.oat.domains.cells.probes;

import java.util.LinkedList;

import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigen;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigenColourSpaceProblem;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: Average component error per antigen, averaged over all antigen
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
public class CellSetAverageComponentEuclideanError extends GenericEpochCompletedProbe
{
	protected double error = Double.NaN;	

	@Override
	public Object getProbeObservation()
	{
		return new Double(error);
	}	

	@Override
	public void reset()
	{
		error = Double.NaN;
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		DegenerateAntigenColourSpaceProblem dap = null;
		if(p instanceof DegenerateAntigenColourSpaceProblem)
		{
			dap = (DegenerateAntigenColourSpaceProblem) p ;
		}
		else
		{
			return;
		}
		
		CellSet set = ((CellSet) cp.getFirst());		
		Cell [] cells = set.getCells();		
		Antigen [] antigen = dap.getAntigen();
		
		double overallError = 0.0;
		// process antigen
		for (int i = 0; i < antigen.length; i++)
		{
			// process components
			double err = 0.0;
			double d [] = cells[i].getDecodedData();
			for (int j = 0; j < antigen[i].getNumComponents(); j++)
			{
				err += ((DegenerateAntigen)antigen[i]).costSubCell(d[j], j);
			}
			
			// divide by the number of components
			overallError += (err / antigen[i].getNumComponents());
		}
		
		// divide by the number of antigen
		error = overallError / antigen.length;
	}

	@Override
	public String getName()
	{
		return "Cell Set Average Component Euclidean Error (CSACEE)";
	}
}
