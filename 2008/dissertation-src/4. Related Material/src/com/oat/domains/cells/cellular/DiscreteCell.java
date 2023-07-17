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
package com.oat.domains.cells.cellular;

import com.oat.Solution;
import com.oat.SolutionEvaluationException;
import com.oat.utils.BitStringUtils;

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
public abstract class DiscreteCell extends Solution
{
	protected final boolean [] data;
	
	
	public DiscreteCell(boolean [] aData)
	{
		data = aData;
	}	
	
	@Override
	public boolean equals(Object o)
	{
		return o == this;
	}
	
	@Override
    public void evaluated(double aCost)
		throws SolutionEvaluationException
	{
		// allow re-evaluation
	    isEvaluated = false;
	    super.evaluated(aCost);
	}
	
	public String toString()
	{
		return BitStringUtils.toString(data);
	}
	
	public boolean[] getData()
	{
		return data;
	}
	
	/**
	 * Calculate the distance between this cell and another cell
	 * @param other
	 * @return
	 */
	public abstract double distance(DiscreteCell other);
}
