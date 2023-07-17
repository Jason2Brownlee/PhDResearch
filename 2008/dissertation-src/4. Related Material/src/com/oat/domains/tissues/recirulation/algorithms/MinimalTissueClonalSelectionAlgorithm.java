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
package com.oat.domains.tissues.recirulation.algorithms;

import com.oat.domains.tissues.InfectionProblem;

/**
 * Description: 
 *  
 * Date: 26/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class MinimalTissueClonalSelectionAlgorithm extends TissueClonalSelectionAlgorithm
{		
	public void trafficLymphocytes(InfectionProblem p)
	{
		// nothing
	}

	@Override
	public String getName()
	{
		return "Minimal Tissue Clonal Selection Algorithm (MTCSA)";
	}
}
