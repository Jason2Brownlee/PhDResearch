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

import java.util.Arrays;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.CellularAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.probes.GenericEpochCompletedProbe;
import com.oat.utils.MeasureUtils;

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
public class AbsoluteCellError extends GenericEpochCompletedProbe
{
	protected double error = Double.NaN;
	protected CellularAlgorithm algorithm;

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
		
		if(a instanceof CellularAlgorithm)
		{			
			if(((CellularAlgorithm)a).isCellBased())
			{
				algorithm = (CellularAlgorithm) a;
			}
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
			LinkedList<Cell> repertoire = algorithm.getRepertoire();		
			AntigenProblem ap = (AntigenProblem) p;
			Antigen [] antigen = ap.getAntigen();
			
			double err = 0;
			for (int i = 0; i < antigen.length; i++)
			{
				// assess against the antigen
				CellUtils.assessRepertoireAgainstAntigen(antigen[i], repertoire);				
				// get the bmu
				Cell bmu = CellUtils.getRepertoireBMU(repertoire, algorithm.getRandom());
				// error is known because it is stored in the cell
				err += bmu.getScore();
			}
			error = err;
		}
	}

	@Override
	public String getName()
	{
		return "Absolute Cell Error (AbCE)";
	}
}
