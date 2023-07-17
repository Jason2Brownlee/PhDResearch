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
package com.oat.domains.cells.mediated.gui;

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
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.gui.ColourCompariator;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.domains.cells.mediated.MediatedCellularAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 06/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class MediatedPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	protected MediatedCellularAlgorithm algorithm;
	
	protected AntigenProblem problem;	
	protected LinkedList<Color> bcellColours;
	protected LinkedList<Color> tcellColours;
	protected ColourCompariator compare;
	
	
	public MediatedPlot()
	{
		setName("Mediated");
		bcellColours = new LinkedList<Color>();
		tcellColours = new LinkedList<Color>();
		compare = new ColourCompariator();
	}
	
	@Override
	public void algorithmChangedEvent(Algorithm a)
	{
		synchronized(this)
		{			
			algorithm = null;
			clear();
		
			if(a instanceof MediatedCellularAlgorithm)
			{
				algorithm = (MediatedCellularAlgorithm) a;				
			}
		}
		
		repaint();
	}
	
	@Override
	public void problemChangedEvent(Problem p)
	{
		synchronized(this)
		{			
			clear();
		
			if(p instanceof AntigenProblem)
			{
				problem = (AntigenProblem) p;
			}
		}
		
		repaint();
	}

	@Override
	public void clear()
	{
		synchronized(this)
		{
			bcellColours.clear();
			tcellColours.clear();
		}
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			if(problem == null || algorithm == null)
			{
				return;
			}					
			// b cells
			populateColourList(algorithm.getBCells(), bcellColours);
			// t cells
			populateColourList(algorithm.getTCells(), tcellColours);
			// connections
		}
		
		repaint();
	}	
	
	protected <C extends DiscreteCell> void populateColourList(LinkedList<C> cells, LinkedList<Color> colours)
	{
		colours.clear();
		
		if(cells == null)
		{
			return;
		}
		
		if(cells.getFirst() instanceof Cell)
		{
			for (C c : cells)
			{
				Color col = DrawingUtils.vectorToColor(((Cell)c).getDecodedData());
				colours.add(col);
			}
			
			// order them in some way
			Collections.sort(colours, compare);
		}
	}
	
	

	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(problem == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things			
			int height = (int) Math.floor((double)getHeight() / 2.0); 
			
			// draw b cells
			int bwidth = (int) Math.floor((double)getWidth() / bcellColours.size());
			for (int i = 0; i < bcellColours.size(); i++)
			{
				DrawingUtils.drawColor(g, bcellColours.get(i), i*bwidth, 0, bwidth, height);				
			}
			
			// draw t cells
			int twidth = (int) Math.floor((double)getWidth() / tcellColours.size());
			for (int i = 0; i < tcellColours.size(); i++)
			{
				DrawingUtils.drawColor(g, tcellColours.get(i), i*twidth, getHeight()-height, twidth, height);				
			}			
		}
	}
}
