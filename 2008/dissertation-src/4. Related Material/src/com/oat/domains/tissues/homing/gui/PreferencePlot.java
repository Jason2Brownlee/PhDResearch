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
package com.oat.domains.tissues.homing.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.gui.ColourCompariator;
import com.oat.domains.tissues.Tissue;
import com.oat.domains.tissues.homing.HomingCell;
import com.oat.domains.tissues.homing.algorithms.HomingTissueClonalSelectionAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 07/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class PreferencePlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	// data
	protected LinkedList<Color> [] repertoireColours;
	protected HomingTissueClonalSelectionAlgorithm algorithm;
	protected ColourCompariator comparator;
	protected Random rand;
	protected Color [] repertoireIds;
	
	public final static Color NO_PREF = Color.white;
	
	public PreferencePlot()
	{
		setName("Homing");
		comparator = new ColourCompariator();
		rand = new Random();
	}
	
	
	public Color randomColour()
	{
		return new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
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
			if(a instanceof HomingTissueClonalSelectionAlgorithm)
			{
				algorithm = (HomingTissueClonalSelectionAlgorithm) a;
				repertoireIds = new Color[algorithm.getNumTissues()];
				for (int i = 0; i < repertoireIds.length; i++)
				{
					repertoireIds[i] = randomColour();
				}
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
			
			Tissue [] repertoires = algorithm.getTissues();
			repertoireColours = new LinkedList[repertoires.length];
			// process each repertoire
			for (int i = 0; i < repertoires.length; i++)
			{
				repertoireColours[i] = new LinkedList<Color>();
				LinkedList<Cell> cells = repertoires[i].getRepertoire();
				for (Cell s : cells)
				{
					int id = ((HomingCell)s).getPreferredRepertoireNumber();
					if(id == HomingCell.NO_PREFERENCE)
					{
						repertoireColours[i].add(NO_PREF);
					}
					else
					{
						repertoireColours[i].add(repertoireIds[id-1]);
					}
				}
				// order the colours by brightness
				Collections.sort(repertoireColours[i], comparator);
			}
		}		
		
		repaint();
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
			int height = (int) Math.floor((double)getHeight() / (repertoireColours[0].size() + 5)); 
						
			// draw all repertoires
			for (int i = 0; i < repertoireColours.length; i++)
			{
				// draw repertoire colour
				drawColor(g, repertoireIds[i], i*width, 0, width, 5*height);
				
				for (int j = 0; j < repertoireColours[i].size(); j++)
				{
					drawColor(g, repertoireColours[i].get(j), i*width, (5*height)+(j*height), width, height);
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
