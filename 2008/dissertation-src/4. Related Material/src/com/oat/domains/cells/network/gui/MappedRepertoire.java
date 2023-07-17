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
package com.oat.domains.cells.network.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.gui.ColourCompariator;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkCellularAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: Mapped Repertoire
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
public class MappedRepertoire extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	protected AntigenProblem problem;	
	protected NetworkCellularAlgorithm<NetworkCell> algorithm;	
	
	protected LinkedList<Color> problemColours;
	protected LinkedList<Color> primaryColours;
	protected LinkedList<Color> secondaryColours;
	
	protected ColourCompariator comparator;
	
	
	public MappedRepertoire()
	{
		setName("Mapped");
		problemColours = new LinkedList<Color>();
		primaryColours = new LinkedList<Color>();
		secondaryColours = new LinkedList<Color>();
		comparator = new ColourCompariator();
	}
	
	@Override
	public void problemChangedEvent(Problem p)
	{
		synchronized(this)
		{	
			problem = null;
			clear();			
			if(p instanceof AntigenProblem)
			{
				problem = (AntigenProblem) p;
			}
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
			if(a instanceof NetworkCellularAlgorithm)
			{
				if(!((NetworkCellularAlgorithm) a).isCellBased())
				{
					algorithm = (NetworkCellularAlgorithm) a;
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
			primaryColours.clear();
			secondaryColours.clear();
			problemColours.clear();
		}
		
		repaint();
	}

	
	protected void prepareProblem()
	{
		// lazy, so we know the problem has been initialised
		if(problemColours.isEmpty())
		{
			problemColours.addAll(DrawingUtils.antigenProblemToColourList(problem));
		}
	}
	
	protected void extractColours(LinkedList<NetworkCell> repertoire)
	{		
		LinkedList<NetworkCell> r = (LinkedList<NetworkCell>) repertoire.clone();
		Collections.shuffle(r);
		Collections.sort(r);
		
		for(NetworkCell c : r)
		{
			primaryColours.add(DrawingUtils.cellToColour(c));
			secondaryColours.add(DrawingUtils.vectorToColor(c.getDecodedData2()));
		}		
	}
	
	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			// check if problem colours can be collected
			if(problem != null)
			{
				prepareProblem();
			}
			
			if(algorithm != null)
			{			
				// always clear the repertoire colours
				primaryColours.clear();
				secondaryColours.clear();
				extractColours(algorithm.getRepertoire());
			}
		}		
		
		repaint();
	}	
	
	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(algorithm == null || problem == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / 3.0);
			int xOff = 0;
			
			// draw primary colours
			if(!primaryColours.isEmpty())
			{
				int height = (int) Math.floor((double)getHeight() / primaryColours.size());
				for (int i = 0; i < primaryColours.size(); i++)
				{
					DrawingUtils.drawColor(g, primaryColours.get(i), xOff, i*height, width, height);					
				}
			}
			xOff += width;
			
			// draw secondary colours
			if(!secondaryColours.isEmpty())
			{
				int height = (int) Math.floor((double)getHeight() / secondaryColours.size());
				for (int i = 0; i < secondaryColours.size(); i++)
				{
					DrawingUtils.drawColor(g, secondaryColours.get(i), xOff, i*height, width, height);					
				}
			}
			xOff += width;
			
			// draw problem colours
			if(!problemColours.isEmpty())
			{
				int height = (int) Math.floor((double)getHeight() / problemColours.size());
				for (int i = 0; i < problemColours.size(); i++)
				{
					DrawingUtils.drawColor(g, problemColours.get(i), xOff, i*height, width, height);					
				}
			}	
		}
	}
	
}
