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
package com.oat.domains.hosts.probes;

import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.HostAlgorithm;
import com.oat.domains.hosts.HostUtils;
import com.oat.probes.GenericEpochCompletedProbe;

/**
 * Description: 
 *  
 * Date: 11/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AverageHostError extends GenericEpochCompletedProbe
{
	protected double error = Double.NaN;
	protected HostAlgorithm algorithm;
	protected HabitatProblem problem;

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
		if(a instanceof HostAlgorithm)
		{
			algorithm = (HostAlgorithm) a;
		}
		if(p instanceof HabitatProblem)
		{
			problem = (HabitatProblem) p;
		}
	}	

	@Override
	public void reset()
	{
		error = Double.NaN;
	}
	

	
	/**
	 * 
	 * @param algorithm
	 * @param problem
	 * @return
	 */
	public static double calculateAverageHostError(HostAlgorithm algorithm, HabitatProblem problem)
	{
		return calculateAverageHostError(algorithm.getHosts(), problem.getHabitats());
	}
		
	/**
	 * 
	 * @param hosts
	 * @param subproblems
	 * @return
	 */
	public static double calculateAverageHostError(Host [] hosts, Habitat [] habitats)
	{		
		double sum = 0;
		
		// process each repertoire
		for (int i = 0; i < hosts.length; i++)
		{				
			// sum the average errors for each repertoire
			sum += calculateHostBMUError(hosts[i], habitats);				
		}
		
		return (sum / hosts.length);	
	}
	
	public static double calculateHostBMUError(Host hosts, Habitat [] subproblems)
	{			
		LinkedList<Cell> repertoire = hosts.getRepertoire();
		
		double avgErr = 0.0;				
		// assess for each pattern
		for (int j = 0; j < subproblems.length; j++)
		{
			// assess cells and sort
			Cell bmu = HostUtils.getRepertoireBMU(repertoire, subproblems[j]);
			// score is stored in cell
			avgErr += bmu.getScore();
		}

		return (avgErr / subproblems.length);
	}
	

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> cp)
	{	
		if(algorithm != null && problem != null)
		{
			error = calculateAverageHostError(algorithm, problem);
		}
	}

	@Override
	public String getName()
	{
		return "Average Host Error (AHE)";
	}
}
