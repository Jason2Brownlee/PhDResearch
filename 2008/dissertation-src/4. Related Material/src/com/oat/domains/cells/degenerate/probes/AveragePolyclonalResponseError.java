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
package com.oat.domains.cells.degenerate.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateCell;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.degenerate.algorithms.component.DCCCSA;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigenColourSpaceProblem;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: 
 *  
 * Date: 10/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AveragePolyclonalResponseError extends GenericEpochCompletedProbe
{
	protected double error = Double.NaN;
	protected DCCCSA algorithm;

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
		
		if(a instanceof DCCCSA)
		{
			algorithm = (DCCCSA) a;
		}
	}	

	@Override
	public void reset()
	{
		error = Double.NaN;
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(algorithm != null)
		{
			DegenerateAntigenColourSpaceProblem dacsp = (DegenerateAntigenColourSpaceProblem) p;
			LinkedList<SubCell> repertoire = algorithm.getRepertoire();
			
			
			// average repertoire error per component
			
			double tSub = 0.0;			
			// process each antigen
			for (int i = 0; i < dacsp.getNumAntigen(); i++)
			{
				for (int j = 0; j < Antigen.NUM_COMPONENTS; j++)
				{
					double rSub = 0.0;
					// assess the repertoire
					for(SubCell c : repertoire)
					{
						c.setComponent(j);
						rSub += dacsp.costSubCell(c, i);
					}
					// average component error over all the repertoire
					tSub += (rSub / repertoire.size());
				}
			}
			// averaged over all antigen
			error = (tSub / (dacsp.getNumAntigen()*Antigen.NUM_COMPONENTS));
		}
	}

	@Override
	public String getName()
	{
		return "Average Polyclonal Response Error (APRE)";
	}
}
