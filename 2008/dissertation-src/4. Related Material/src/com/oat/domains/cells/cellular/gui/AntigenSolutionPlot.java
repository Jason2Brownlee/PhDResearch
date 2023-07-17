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
import java.util.LinkedList;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: Plot of the antigen problem and current solution 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AntigenSolutionPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener
{	
	protected AntigenProblem antigenProblem;	
	protected LinkedList<Color> problemColours;
	protected LinkedList<Color> popColours;
	
	
	public AntigenSolutionPlot()
	{
		setName("Solution");
		problemColours = new LinkedList<Color>();
		popColours = new LinkedList<Color>();
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
	public void clear()
	{
		synchronized(this)
		{
			problemColours.clear();
			popColours.clear();
		}
		
		repaint();
	}
	
	protected void prepareProblem()
	{
		// lazy, so we know the problem has been initialised
		if(problemColours.isEmpty())
		{
			Antigen [] antigen = antigenProblem.getAntigen();
			for (int i = 0; i < antigen.length; i++)
			{
				problemColours.add(antigen[i].getGoalCoordinateColour());
			}
		}
	}
	
	protected <T extends Solution> void prepareSolution(LinkedList<T> currentPop)
	{
		// convert the current population to colours
		popColours.clear();			
		// expect a population of size 1
		if(currentPop.size() == 1)
		{
			Cell [] cells = ((CellSet)currentPop.getFirst()).getCells();
			
			for (Solution s : cells)
			{
				Color c = DrawingUtils.vectorToColor(((Cell)s).getDecodedData());
				popColours.add(c);
			}
		}
	}

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			if(antigenProblem == null)
			{
				return;
			}			

			// prepare the problem
			prepareProblem();
			// prepare the solution
			prepareSolution(currentPop);
		}		
		
		repaint();
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(antigenProblem == null || (problemColours.isEmpty() && popColours.isEmpty()))
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / 2.0);
			int height = (int) Math.floor((double)getHeight() / problemColours.size()); 
			
			int h2 = height/2;
			int w2 = width/2; 
			int w3 = w2/3;
			
			// draw the population colours
			for (int i = 0; i < popColours.size(); i++)
			{
				DrawingUtils.drawColor(g, popColours.get(i), 0, i*height, width, height);
				Color [] components = DrawingUtils.getComponents(popColours.get(i));
				for (int j = 0; j < components.length; j++)
				{
					DrawingUtils.drawColor(g, components[j], 0+(j*w3), i*height+(h2/2), w3, h2);
				}
			}
			
			// draw the problem colours
			for (int i = 0; i < problemColours.size(); i++)
			{
				DrawingUtils.drawColor(g, problemColours.get(i), width, i*height, width, height);
				Color [] components = DrawingUtils.getComponents(problemColours.get(i));
				for (int j = 0; j < components.length; j++)
				{
					DrawingUtils.drawColor(g, components[j], width+(j*w3), i*height+(h2/2), w3, h2);
				}
			}		
		}
	}
	
	
}
