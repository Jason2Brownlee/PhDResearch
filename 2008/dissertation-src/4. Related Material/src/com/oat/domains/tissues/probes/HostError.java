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
package com.oat.domains.tissues.probes;

import java.util.LinkedList;

import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.tissues.Infection;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.probes.GenericEpochCompletedProbe;
import com.oat.utils.MeasureUtils;

/**
 * Description: 
 *  
 * Date: 20/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HostError extends GenericEpochCompletedProbe
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
		Cell [] cells = ((CellSet) cp.getFirst()).getCells();		
		Infection [] infections = ((InfectionProblem)p).getInfections();		
		double [] errors = new double[cells.length];	
		
		for (int i = 0; i < infections.length; i++)
		{
			errors[i] = infections[i].costCell(cells[i]);
		}
		
		error = MeasureUtils.calculateAverageError(errors);
	}

	@Override
	public String getName()
	{
		return "Host Error (HE)";
	}
}
