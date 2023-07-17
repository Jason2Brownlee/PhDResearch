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
package com.oat.domains.tissues;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;



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
public class TissueUtils
{
	
	public static Cell getRepertoireBMU(LinkedList<Cell> repertoire, Infection infection)
	{
		for(Cell c : repertoire)
		{
			double d = infection.costCell(c);
			c.evaluated(d);
		}

		Collections.sort(repertoire);
		return repertoire.getFirst();
	}
	
	/**
	 * A measure of how different the two repertoire's are.
	 * 
	 * @param rep1
	 * @param rep2
	 * @return
	 */
	public static double interRepertoireDiversity(LinkedList<Cell> rep1, LinkedList<Cell> rep2)
	{		
		int [] hist1 = toBitHistogram(rep1);
		int [] hist2 = toBitHistogram(rep2);
		
		double sumDiff = 0;
		
		for (int i = 0; i < hist1.length; i++)
		{
			sumDiff += Math.abs(hist1[i] - hist2[i]);
		}
		
		return sumDiff;
	}
	
	/**
	 * Compress a repertoire of bitstrings to a histogram
	 * @param rep
	 * @return
	 */
	public static int [] toBitHistogram(LinkedList<Cell> rep)
	{
		int [] hist = new int[rep.getFirst().getData().length];
		
		for(Cell c : rep)
		{
			boolean [] data = c.getData();
			for (int i = 0; i < data.length; i++)
			{
				if(data[i])
				{
					hist[i]++;
				}
			}
		}
		
		return hist;
	}
}
