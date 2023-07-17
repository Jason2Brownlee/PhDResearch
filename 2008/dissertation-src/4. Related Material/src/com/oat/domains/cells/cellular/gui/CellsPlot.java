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
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellularAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 03/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CellsPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	// data
	protected LinkedList<Color> problemColours;
	protected LinkedList<Color> repertoireColours;
	protected AntigenProblem antigenProblem;
	protected CellularAlgorithm<Cell> algorithm;
	protected ColourCompariator comparator;
	
	public CellsPlot()
	{
		setName("Cells");
		repertoireColours = new LinkedList<Color>();
		problemColours = new LinkedList<Color>();
		comparator = new ColourCompariator();
	}
	
	@Override
	public void problemChangedEvent(Problem p)
	{
		synchronized(this)
		{			
			clear();
		
			if(p instanceof AntigenProblem)
			{
				antigenProblem = (AntigenProblem) p;
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
			if(a instanceof CellularAlgorithm)
			{
				if(((CellularAlgorithm) a).isCellBased())
				{
					algorithm = (CellularAlgorithm) a;
				}
			}
		}		
		repaint();		
	}
	
	protected void prepareProblem()
	{
		// lazy, so we know the problem has been initialised
		if(problemColours.isEmpty())
		{
			problemColours.addAll(DrawingUtils.antigenProblemToColourList(antigenProblem));
		}
	}

	@Override
	public void clear()
	{
		synchronized(this)
		{
			repertoireColours.clear();
			problemColours.clear();
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
			
			prepareProblem();
			
			LinkedList<Cell> cells = algorithm.getRepertoire();
			repertoireColours.clear();						
			for (Solution s : cells)
			{
				Color c = DrawingUtils.vectorToColor(((Cell)s).getDecodedData());
				repertoireColours.add(c);
			}
			// order the colours by brightness
			Collections.sort(repertoireColours, comparator);
		}
			
		
		repaint();
	}	

	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(algorithm == null || repertoireColours.isEmpty())
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / 4);				
			int height = (int) Math.floor((double)getHeight() / repertoireColours.size()); 			
			
			// draw repertoire						
			for (int j = 0; j < repertoireColours.size(); j++)
			{
				DrawingUtils.drawColor(g, repertoireColours.get(j), width, j*height, width, height);
			}
			
			// draw problem colours
			if(!problemColours.isEmpty())
			{
				int rHeight = height*repertoireColours.size();
				int h = (int) Math.floor((double)rHeight / problemColours.size()); 
				
				// draw problems						
				for (int j = 0; j < problemColours.size(); j++)
				{
					DrawingUtils.drawColor(g, problemColours.get(j), width*2, j*h, width, h);
				}
			}
		}
	}	
}
