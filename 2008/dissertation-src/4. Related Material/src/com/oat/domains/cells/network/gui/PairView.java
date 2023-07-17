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
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.network.NetworkCell;
import com.oat.domains.cells.network.NetworkCellularAlgorithm;
import com.oat.domains.cells.network.NetworkInteractionListener;
import com.oat.domains.cells.network.NetworkInteractionsCellularAlgorithm;
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
public class PairView extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener, NetworkInteractionListener
{	
	protected NetworkInteractionsCellularAlgorithm algorithm;
	
	protected LinkedList<Antigen> a1Store;
	protected LinkedList<Cell> b1Store;
	protected LinkedList<Cell> a2Store;
	protected LinkedList<Cell> b2Store;
	
	protected boolean isNetworkBased;
	
	protected LinkedList<Color> a1Colours;	
	protected LinkedList<Color> b1Colours;
	protected LinkedList<Color> a2Colours;
	protected LinkedList<Color> b2Colours;
	

	public PairView()
	{
		setName("Pairs");
		// stores
		a1Store = new LinkedList<Antigen>();
		b1Store = new LinkedList<Cell>();
		a2Store = new LinkedList<Cell>();
		b2Store = new LinkedList<Cell>();
		// colours
		a1Colours = new LinkedList<Color>();
		b1Colours = new LinkedList<Color>();
		a2Colours = new LinkedList<Color>();
		b2Colours = new LinkedList<Color>();		
	}	
	
	@Override
	public <C extends Cell> void interaction(
			NetworkCellularAlgorithm<C> algorithm, 
			Antigen antigen1, 
			C bmu1,
			C antigen2, 
			C bmu2)
	{
		synchronized(this)
		{
			a1Store.add(antigen1);
			b1Store.add(bmu1);
			a2Store.add(antigen2);
			b2Store.add(bmu2);
		}
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
			if(algorithm != null)
			{
				algorithm.removeNetworkInteractionListener(this);
			}			
			algorithm = null;
			clear();
			if(a instanceof NetworkInteractionsCellularAlgorithm)
			{				
				algorithm = (NetworkInteractionsCellularAlgorithm) a;
				algorithm.registerNetworkInteractionListener(this);
				isNetworkBased = !algorithm.isCellBased();
			}
		}		
		repaint();		
	}

	@Override
	public void clear()
	{
		synchronized(this)
		{
			// stores
			clearStores();
			// colours
			clearColours();
		}
		
		repaint();
	}

	protected void clearStores()
	{
		a1Store.clear();
		b1Store.clear();
		a2Store.clear();
		b2Store.clear();
	}
	protected void clearColours()
	{
		a1Colours.clear();
		b1Colours.clear();
		a2Colours.clear();
		b2Colours.clear();
	}	
	
	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			// flush the colours
			clearColours();			
			// build all colour lists
			int numExposures = a1Store.size();
			for (int i = 0; i < numExposures; i++)
			{
				// 1
				a1Colours.add(a1Store.get(i).getGoalCoordinateColour());
				b1Colours.add(DrawingUtils.cellToColour(b1Store.get(i)));
				// 2
				if(isNetworkBased)
				{	
					a2Colours.add(DrawingUtils.vectorToColor(((NetworkCell)a2Store.get(i)).getDecodedData2()));
				}
				else
				{
					a2Colours.add(DrawingUtils.cellToColour(a2Store.get(i)));
				}
				b2Colours.add(DrawingUtils.cellToColour(b2Store.get(i)));
			}						
			// flush the cells
			clearStores();
		}		
		
		repaint();
	}	
	

	@Override
	protected void paintComponent(Graphics g)
	{
		synchronized(this)
		{
			if(algorithm == null || a1Colours.isEmpty())
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int numExposures =a1Colours.size();
			int width = (int) Math.floor((double)getWidth() / 4.0);
			int height = (int) Math.floor((double)getHeight() / numExposures);			

			// process all exposures
			for (int i = 0; i < numExposures; i++)
			{			
				int xOff = 0;
				DrawingUtils.drawColor(g, a1Colours.get(i), xOff, i*height, width, height);
				xOff += width;
				DrawingUtils.drawColor(g, b1Colours.get(i), xOff, i*height, width, height);
				xOff += width;
				DrawingUtils.drawColor(g, a2Colours.get(i), xOff, i*height, width, height);
				xOff += width;
				DrawingUtils.drawColor(g, b2Colours.get(i), xOff, i*height, width, height);
			}			
		}
	}
}
