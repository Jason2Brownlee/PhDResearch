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
package com.oat.domains.hosts.population.transmission.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.gui.ColourCompariator;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.HostAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 10/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HostPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	// data
	protected LinkedList<Color> [] repertoireColours;
	protected HostAlgorithm algorithm;
	protected ColourCompariator comparator;
	
	public HostPlot()
	{
		setName("Hosts");
		comparator = new ColourCompariator();
	}
	
	@Override
	public void problemChangedEvent(Problem p)
	{
		synchronized(this)
		{			
			clear();
		}
		
		repaint();
	}
	
	@Override
	public void algorithmChangedEvent(Algorithm a)
	{
		synchronized(this)
		{			
			algorithm = null;
			clear();
			if(a instanceof HostAlgorithm)
			{
				algorithm = (HostAlgorithm) a;
			}
		}		
		repaint();		
	}
	

	@Override
	public void clear()
	{
		synchronized(this)
		{
			repertoireColours = null;
		}
		
		repaint();
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			if(algorithm == null)
			{
				return;
			}
			
			Host [] hosts = algorithm.getHosts();
			repertoireColours = new LinkedList[hosts.length];
			// process each repertoire
			for (int i = 0; i < hosts.length; i++)
			{
				repertoireColours[i] = new LinkedList<Color>();
				LinkedList<Cell> cells = hosts[i].getRepertoire();
				for (Solution s : cells)
				{
					Color c = vectorToColor(((Cell)s).getDecodedData());
					repertoireColours[i].add(c);
				}
				// order the colours by brightness
				Collections.sort(repertoireColours[i], comparator);
			}
		}		
		
		repaint();
	}	
	
	protected Color vectorToColor(double [] v)
	{		
		return new Color((float)v[0], (float)v[1], (float)v[2]);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(algorithm == null || repertoireColours == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / repertoireColours.length);			
			int longestList = 0;
			for (int i = 0; i < repertoireColours.length; i++)
			{
				if(repertoireColours[i].size() > longestList)
				{
					longestList = repertoireColours[i].size();
				}
			}
			int height = (int) Math.floor((double)getHeight() / longestList); 
			
			
			// draw all repertoires
			for (int i = 0; i < repertoireColours.length; i++)
			{				
				for (int j = 0; j < repertoireColours[i].size(); j++)
				{
					drawColor(g, repertoireColours[i].get(j), i*width, j*height, width, height);
				}
			}
		}
	}	
	
	public void drawColor(Graphics g, Color c, int x, int y, int width, int height)
	{
		g.setColor(c);
		g.fillRect(x, y, width, height);				
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}	
}
