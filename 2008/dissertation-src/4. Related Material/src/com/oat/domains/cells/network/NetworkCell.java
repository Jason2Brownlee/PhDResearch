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
package com.oat.domains.cells.network;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BinaryDecodeMode;
import com.oat.utils.BitStringUtils;

/**
 * Description: 
 *  
 * Date: 06/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class NetworkCell extends Cell
{
	protected final boolean [] data2;
	protected final double [] decodedData2;
	
	/**
	 * 
	 * @param data
	 * @param aData2
	 */
	public NetworkCell(boolean[] data, boolean [] aData2)
	{
		super(data);
		data2 = aData2;
		decodedData2 = BitStringUtils.decode(BinaryDecodeMode.GrayCode, data2, Antigen.MINMAX);
	}
	
	/**
	 * 
	 * @param otherCell
	 */
	public NetworkCell(NetworkCell otherCell)
	{
		super(otherCell);
		data2 = ArrayUtils.copyArray(otherCell.data2);
		decodedData2 = ArrayUtils.copyArray(otherCell.decodedData2);		
	}

	public boolean[] getData2()
	{
		return data2;
	}

	public double[] getDecodedData2()
	{
		return decodedData2;
	}
}
