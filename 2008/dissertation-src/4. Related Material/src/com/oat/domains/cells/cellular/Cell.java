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

import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BinaryDecodeMode;
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
public class Cell extends DiscreteCell 
{
	// state
	protected final double [] decodedData;
	
	
	public Cell(Cell aOtherCell)
	{
		super(ArrayUtils.copyArray(aOtherCell.data));
		decodedData = ArrayUtils.copyArray(aOtherCell.decodedData);
		
		if(aOtherCell.isEvaluated)
		{
			evaluated(aOtherCell.getScore());
		}
	}	
	
	public Cell(boolean [] aData)
	{
		super(aData);
		// hack
		decodedData = BitStringUtils.decode(BinaryDecodeMode.GrayCode, data, Antigen.MINMAX);
	}
	
	public double[] getDecodedData()
	{
		return decodedData;
	}

	@Override
	public double distance(DiscreteCell other)
	{
		if(other instanceof Cell)
		{
			return CellUtils.hammingDistance(this, other);
		}
		
		throw new RuntimeException("Unknown cell type: " + other.getClass().getName());
	}
}
