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
package com.oat.domains.cells.degenerate;

import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.utils.ArrayUtils;
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
public class DegenerateCell extends DiscreteCell 
{
	// state
	protected final boolean [] mask;	
	
	public DegenerateCell(DegenerateCell aOtherCell)
	{
		super(ArrayUtils.copyArray(aOtherCell.data));
		mask = ArrayUtils.copyArray(aOtherCell.mask);
	}	
	public DegenerateCell(boolean [] aData, boolean [] aMask)
	{
		super(aData);
		mask = aMask;
	}	
	
	public String toString()
	{
		return "Data="+BitStringUtils.toString(data)+",Mask="+BitStringUtils.toString(mask);
	}
	public boolean[] getMask()
	{
		return mask;
	}	
	
	@Override
	public double distance(DiscreteCell other)
	{
		if(other instanceof DegenerateCell)
		{
			return DegenerateUtils.hammingDistance(this, (DegenerateCell)other);
		}
		
		throw new RuntimeException("Unknown cell type: " + other.getClass().getName());
	}
	
	
	/**
	 * The number of bits used by this cell as defined by its mask
	 * @return
	 */
	public int getNumBits()
	{
		int count = 0;
		
		for (int i = 0; i < mask.length; i++)
		{
			if(mask[i])
			{
				count++;
			}
		}
		
		return count;
	}
}
