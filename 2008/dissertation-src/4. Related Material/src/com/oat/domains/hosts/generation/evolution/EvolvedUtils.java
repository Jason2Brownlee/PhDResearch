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
package com.oat.domains.hosts.generation.evolution;

import java.util.Random;

import com.oat.utils.RandomUtils;

/**
 * Description: 
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
public class EvolvedUtils
{
	/**
	 * Create a repertoire as an array of binary strings
	 * 
	 * @param rand
	 * @param size
	 * @param numBitsPerCell
	 * @return
	 */
	public static boolean [][] getRandomBinaryRepertoireArray(Random rand, int size, int numBitsPerCell)
	{
		boolean [][] r = new boolean[size][];
		for (int i = 0; i < r.length; i++)
		{
			r[i] = RandomUtils.randomBitString(rand,numBitsPerCell);
		}
		return r;
	}
}
