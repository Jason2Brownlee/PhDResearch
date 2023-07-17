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
package com.oat.domains.cells.spatial.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.domains.cells.spatial.SpatialCellularAlgorithm;
import com.oat.domains.cells.spatial.SpatialSubCell;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

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
public class SpatialSubCellPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	protected AntigenProblem problem;	
	protected SpatialCellularAlgorithm<SpatialSubCell> algorithm;
	
	protected Color [][][] colours;
	
	
	
	public SpatialSubCellPlot()
	{
		setName("Spatial SubCell");
	}
	
	@Override
	public void algorithmChangedEvent(Algorithm a)
	{
		synchronized(this)
		{			
			algorithm = null;
			clear();
		
			if(a instanceof SpatialCellularAlgorithm)
			{
				if(!((SpatialCellularAlgorithm) a).isCellBased())
				{
					algorithm = (SpatialCellularAlgorithm) a;
				}
			}
		}
		
		repaint();
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
	public void clear()
	{
		synchronized(this)
		{
			colours = null;
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
			
			LinkedList<SpatialSubCell> allCells = algorithm.getRepertoire();
			int size = (int) Math.sqrt(allCells.size()/3);
			colours = new Color[3][size][size];
			
			// build the colours
			for (SpatialSubCell c : allCells)
			{
				int x = c.getComponent();
				int [] coord = c.getCoordinate();
				colours[x][coord[0]][coord[1]] = DrawingUtils.createComponentColour(c.getDecodedData(), x);
			}
		}		
		
		repaint();
	}	
	


	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(colours == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// repertoire dimensions
			int w = (int) Math.floor((double)getWidth() / colours.length);
			int h = getHeight();
			
			for (int i = 0; i < colours.length; i++)
			{
				drawRepertoire(g, i*w, 0, w, h, colours[i]);
			}			
		}
	}	
	
	public void drawRepertoire(Graphics g, int x, int y, int width, int height, Color [][] rep)
	{
		// calculate things
		int w = (int) Math.floor((double)width / rep.length);
		int h = (int) Math.floor((double)height / rep[0].length); 
		int s = Math.min(w, h);
		
		for (int i = 0; i < rep.length; i++)
		{
			for (int j = 0; j < rep[i].length; j++)
			{
				DrawingUtils.drawColor(g, rep[i][j], x+(i*s), y+(j*s), s, s);
			}
		}
	}
}
