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
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.spatial.SpatialCell;
import com.oat.domains.cells.spatial.SpatialCellularAlgorithm;
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
public class SpatialPlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener
{	
	protected AntigenProblem problem;	
	protected SpatialCellularAlgorithm algorithm;
	
	protected Color [][] popColours;
	protected LinkedList<int []> bmusets;
	
	
	public SpatialPlot()
	{
		setName("Spatial");
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
				algorithm = (SpatialCellularAlgorithm) a;
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
			popColours = null;
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
			
			Antigen [] antigen = problem.getAntigen();
			bmusets = new LinkedList<int[]>();
			
			SpatialCell [][] repertoire = (SpatialCell [][]) algorithm.getSpatialRepertoire();
			popColours = new Color[repertoire.length][repertoire[0].length];
			for (int i = 0; i < repertoire.length; i++)
			{
				for (int j = 0; j < repertoire[i].length; j++)
				{
					popColours[i][j] = DrawingUtils.vectorToColor(((Cell)repertoire[i][j]).getDecodedData());
				}
			}
			
			if(algorithm.isCellBased())
			{
				LinkedList<SpatialCell> flatRep = algorithm.getRepertoire();
				
				// process each problem and locate bmu sets
				for (int i = 0; i < antigen.length; i++)
				{
					// assess
					CellUtils.assessRepertoireAgainstAntigen(antigen[i], flatRep);
					// get the bmu set
					LinkedList<SpatialCell> bmus = CellUtils.getRepertoireBMUSet(flatRep, algorithm.getRandom()); 
					for(SpatialCell b : bmus)
					{
						bmusets.add(b.getCoordinate());
					}
				}
			}
		}		
		
		repaint();
	}	
	

	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(popColours == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / popColours.length);
			int height = (int) Math.floor((double)getHeight() / popColours[0].length); 
			int s = Math.min(width, height);
			
			for (int i = 0; i < popColours.length; i++)
			{
				for (int j = 0; j < popColours[i].length; j++)
				{
					DrawingUtils.drawColor(g, popColours[i][j], i*s, j*s, s, s);
				}
			}	
			
			// bmu's
			for(int [] o : bmusets)
			{
				g.setColor(Color.BLACK);
				g.drawOval(o[0]*s, o[1]*s, s, s);
			}
		}
	}
}
