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

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.tissues.Infection;
import com.oat.domains.tissues.InfectionProblem;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.TissueAlgorithm;
import com.oat.domains.tissues.TissueUtils;
import com.oat.probes.GenericEpochCompletedProbe;

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
public class AverageTissueError extends GenericEpochCompletedProbe
{
	protected double error = Double.NaN;
	protected TissueAlgorithm algorithm;
	protected InfectionProblem problem;

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
		problem = null;
		if(a instanceof TissueAlgorithm)
		{
			algorithm = (TissueAlgorithm) a;
		}
		if(p instanceof InfectionProblem)
		{
			problem = (InfectionProblem) p;
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
		if(algorithm != null && problem != null)
		{
			Tissue [] tissues = algorithm.getTissues();
			Infection [] infections = problem.getInfections();
			
			double sum = 0;
			
			// process each repertoire
			for (int i = 0; i < tissues.length; i++)
			{				
				LinkedList<Cell> repertoire = tissues[i].getRepertoire();
				
				double avgErr = 0.0;				
				// assess for each pattern
				for (int j = 0; j < infections.length; j++)
				{
					// assess the repertoire and locates the best
					Cell bmu = TissueUtils.getRepertoireBMU(repertoire, infections[j]);
					// score is known
					avgErr += bmu.getScore();
				}
				// sum the average errors for each repertoire
				sum += (avgErr / infections.length);				
			}
			
			error = (sum / tissues.length);
		}
	}

	@Override
	public String getName()
	{
		return "Average Tissue Error (ATE)";
	}
}
