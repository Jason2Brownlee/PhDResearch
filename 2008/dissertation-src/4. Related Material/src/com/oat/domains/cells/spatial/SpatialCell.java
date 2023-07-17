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
package com.oat.domains.cells.spatial;

import com.oat.domains.cells.cellular.Cell;

/**
 * Description: Addition of a coordinate for the cell
 *  
 * Date: 04/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SpatialCell extends Cell implements LatticeCell
{	
	protected int [] coordinate;	

	public SpatialCell(boolean[] data)
	{
		super(data);
	}

	public SpatialCell(Cell otherCell)
	{
		super(otherCell);
	}

	public int[] getCoordinate()
	{
		return coordinate;
	}

	public void setCoordinate(int[] coordinate)
	{
		this.coordinate = coordinate;
	}
}
