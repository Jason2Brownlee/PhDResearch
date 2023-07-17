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
public class HostDiversity extends GenericEpochCompletedProbe
{
	protected double diversity = Double.NaN;
	protected TissueAlgorithm algorithm;

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
		if(a instanceof TissueAlgorithm)
		{
			algorithm = (TissueAlgorithm) a;
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
			Tissue [] repertoires = algorithm.getTissues();
			double sum = 0;
			
			// process each repertoire
			for (int i = 0; i < repertoires.length; i++)
			{
				LinkedList<Cell> rep1 = repertoires[i].getRepertoire();
				double isum = 0;

				for (int j = 0; j < repertoires.length; j++)
				{
					if(i == j)
					{
						continue;
					}
					
					LinkedList<Cell> rep2 = repertoires[j].getRepertoire();
					isum += TissueUtils.interRepertoireDiversity(rep1, rep2);
				}
				
				sum += (isum / repertoires.length);
			}
			
			diversity = (sum / repertoires.length);
		}
	}

	@Override
	public String getName()
	{
		return "Host Diversity (HD)";
	}
}
