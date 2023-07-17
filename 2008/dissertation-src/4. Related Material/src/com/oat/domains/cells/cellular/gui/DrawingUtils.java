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
package com.oat.domains.cells.cellular.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.problems.Antigen;

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
public class DrawingUtils
{
	/**
	 * Retrieve the problem colour list
	 * 
	 * @param ap
	 * @return
	 */
	public static LinkedList<Color> antigenProblemToColourList(AntigenProblem ap)
	{
		LinkedList<Color> list = new LinkedList<Color>();
		
		Antigen [] antigen = ap.getAntigen();
		for (int i = 0; i < antigen.length; i++)
		{
			list.add(antigen[i].getGoalCoordinateColour());
		}
		
		return list;
	}
	
	/**
	 * Create a colour from a single component value
	 * @param value
	 * @param component
	 * @return
	 */
	public static Color createComponentColour(double value, int component)
	{		
		Color c = null; 
		
		switch (component)
		{
		case 0:
			c = new Color((float)value, (float)0, (float)0);
			break;
		case 1:
			c = new Color((float)0, (float)value, (float)0);
			break;
		case 2:
			c = new Color((float)0, (float)0, (float)value);
			break;
		default:
			throw new RuntimeException("Unknown component: " + component);			
		}
		
		return c;
	}
	
	
	/**
	 * Convert the provided vector into a comlour
	 * @param v
	 * @return
	 */
	public static Color vectorToColor(double [] v)
	{		
		if(v.length != 3)
		{
			throw new RuntimeException("Provided vector must have three components, " + v.length);
		}
		
		return new Color((float)v[0], (float)v[1], (float)v[2]);
	}
	
	/**
	 * Convert the provided cell to a colour
	 * @param <C>
	 * @param cell
	 * @return
	 */
	public static <C extends Cell> Color cellToColour(C cell)
	{
		return vectorToColor(cell.getDecodedData());
	}
	
	/**
	 * Break the provided colour down into its components
	 * @param c
	 * @return
	 */
	public static Color [] getComponents(Color c)
	{
		Color [] comp = new Color[3];		
		float [] components = c.getComponents(null);
		
		comp[0] = new Color(components[0], 0, 0);
		comp[1] = new Color(0, components[1], 0);
		comp[2] = new Color(0, 0, components[2]);
		
		return comp;
	}
	
	/**
	 * Draw a rectangle with a black line and the provided colour
	 * @param g
	 * @param c
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawColor(Graphics g, Color c, int x, int y, int width, int height)
	{
		g.setColor(c);
		g.fillRect(x, y, width, height);				
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}
}
