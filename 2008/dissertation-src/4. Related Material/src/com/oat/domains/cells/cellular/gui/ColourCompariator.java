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
package com.oat.domains.cells.cellular.gui;

import java.awt.Color;
import java.util.Comparator;

/**
 * Description: 
 *  
 * Date: 14/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ColourCompariator implements Comparator<Color>
{

	@Override
	public int compare(Color o1, Color o2)
	{
		double s1 = o1.getRed() + o1.getBlue() + o1.getGreen();
		double s2 = o2.getRed() + o2.getBlue() + o2.getGreen();
		
		if(s1 < s2)
		{
			return -1;
		}
		else if(s1 > s2)
		{
			return +1;
		}
		
		return 0;
	}

}
