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
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.hosts.Habitat;
import com.oat.domains.hosts.HabitatExposureListener;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.HostAlgorithm;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 26/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HostExposurePlot extends GenericProblemPlot
	implements AlgorithmEpochCompleteListener, ClearEventListener, AlgorithmChangedListener, HabitatExposureListener
{	
	public static final Color NO_EXPOSURE = Color.white;
	public static final int QUEUE_SIZE = 100;	
	
	protected LinkedList<Color> repertoireExposures [];
	protected LinkedList<Color> tempRepertoireExposures [];
	protected LinkedList<Color> problemColours;
	protected HostAlgorithm algorithm;
	protected HabitatProblem problem;
	
	
	public HostExposurePlot()
	{
		setName("Exposures");
		problemColours =  new LinkedList<Color>();
	}
	
	@Override
	public void problemChangedEvent(Problem p)
	{
		synchronized(this)
		{			
			if(p instanceof HabitatProblem)
			{
				problem = (HabitatProblem) p;				
			}
			
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
				if(! algorithm.removeExposureListener(this))
				{
					throw new RuntimeException("Error de-registering listener, expected to be registered.");
				}
			}
			
			algorithm = null;
			repertoireExposures = null;
			tempRepertoireExposures = null;
			clear();
			
			if(a instanceof HostAlgorithm)
			{
				algorithm = (HostAlgorithm) a;
				algorithm.registerExposureListener(this);
				
				repertoireExposures = new LinkedList[algorithm.getNumHosts()];
				for (int i = 0; i < repertoireExposures.length; i++)
				{
					repertoireExposures[i] = new LinkedList<Color>();
				}
				
				tempRepertoireExposures = new LinkedList[algorithm.getNumHosts()];
				for (int i = 0; i < tempRepertoireExposures.length; i++)
				{
					tempRepertoireExposures[i] = new LinkedList<Color>();
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
			problemColours.clear();
		}
		
		repaint();
	}
	
	protected void prepareProblemColours()
	{
		if(problemColours.isEmpty())
		{
			Habitat [] habitats = problem.getHabitats();
			for (int i = 0; i < habitats.length; i++)
			{
				problemColours.add(habitats[i].getGoalCoordinateColour());
			}
			
			if(repertoireExposures != null && tempRepertoireExposures != null)
			{
				for (int i = 0; i < repertoireExposures.length; i++)
				{
					repertoireExposures[i].clear();
					tempRepertoireExposures[i].clear();
				}
			}
		}
	}
	
	
	

	@Override
	public void exposure(int hostNumber, Host repertoire, int habitatNumber, Habitat antigen)
	{
		synchronized(this)
		{
			// prepare problem colours as needed
			prepareProblemColours();
			// record the exposure
			Color pat = problemColours.get(habitatNumber);
			tempRepertoireExposures[hostNumber].add(pat);
		}
		
		repaint();
	}
	
	
	protected void compressExposures()
	{
		for (int i = 0; i < tempRepertoireExposures.length; i++)
		{
			// check for no exposure
			if(tempRepertoireExposures[i].isEmpty())
			{
				repertoireExposures[i].add(NO_EXPOSURE);
			}
			// check for single exposure
			else if(tempRepertoireExposures[i].size() == 1)
			{
				repertoireExposures[i].add(tempRepertoireExposures[i].getFirst());
			}
			// more than one, compress
			else
			{
				// TODO - some compression thing
				repertoireExposures[i].add(Color.BLACK);
			}
			
			// clear the temp repertoire
			tempRepertoireExposures[i].clear();
			// pop the queue as needed
			while(repertoireExposures[i].size() > QUEUE_SIZE)
			{
				repertoireExposures[i].removeFirst();
			}
		}
	}
	

	@Override
	public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
	{		
		synchronized(this)
		{
			if(algorithm == null || repertoireExposures == null || problem == null)
			{
				return;
			}
			
			compressExposures();
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
			if(algorithm == null || repertoireExposures == null || problem == null)
			{
				super.plotUnavailable(g);
				return;
			}
			
			// clear
			g.setColor(Color.WHITE);
			g.fillRect(0,0,getWidth(),getHeight());
			
			// calculate things
			int width = (int) Math.floor((double)getWidth() / repertoireExposures.length);
			int height = (int) Math.floor((double)getHeight() / QUEUE_SIZE);  
						
			// draw all repertoires
			for (int i = 0; i < repertoireExposures.length; i++)
			{
				for (int j = 0; j < repertoireExposures[i].size(); j++)
				{
					// draw in reverse so it looks like exposures are comping from the top
					int expNo = repertoireExposures[i].size() - 1 - j; 
					drawColor(g, repertoireExposures[i].get(expNo), i*width, j*height, width, height);
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
