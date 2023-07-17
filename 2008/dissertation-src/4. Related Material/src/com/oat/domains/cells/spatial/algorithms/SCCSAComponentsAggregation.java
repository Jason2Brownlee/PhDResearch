/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2008  Jason Brownlee

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
package com.oat.domains.cells.spatial.algorithms;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.GenericCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.spatial.SpatialCell;
import com.oat.domains.cells.spatial.SpatialCellularAlgorithm;
import com.oat.domains.cells.spatial.SpatialSubCell;
import com.oat.domains.cells.spatial.SpatialUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Spatial component repertoirse, where the locations for cloning and replacement of components
 * is determined by a top-down minimised intersection of all three repertoires.
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
public class SCCSAComponentsAggregation extends GenericCellularClonalSelectionAlgorithm<SpatialSubCell> 
	implements SpatialCellularAlgorithm<SpatialSubCell>
{
	// config
	protected long seed = 1;
	protected int numCells = 10; 
	protected int numClones = 5;
	protected int numSelected = 2;
	
	// data
	protected Random rand;
	
	protected SpatialSubCell [][][] repertoire;
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);				
		
		// build initial repertoire
		repertoire = new SpatialSubCell[Antigen.NUM_COMPONENTS][][];
		for (int i = 0; i < repertoire.length; i++)
		{
			repertoire[i] = SpatialUtils.createNewSubCellRepertoire(numCells, i, rand);
		}	
		
		// no initial population
		return null;
	}	
	
	protected class DummySolution extends Solution
	{
		public int x;
		public int y;		

		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}		
	}
	
	public double getAggregateScore(int x, int y)
	{
		double sum = 0;
		for (int i = 0; i < repertoire.length; i++)
		{
			sum += repertoire[i][x][y].getScore();
		}
		return sum;
	}
	
	
	public LinkedList<DummySolution> selectCoordinates()
	{
		LinkedList<DummySolution> list = new LinkedList<DummySolution>();
		
		for (int i = 0; i < numCells; i++)
		{
			for (int j = 0; j < numCells; j++)
			{
				DummySolution d = new DummySolution();
				d.x = i;
				d.y = j;
				d.evaluated(getAggregateScore(i,j));
				list.add(d);
			}
		}
		
		// minimise summed component error
		Collections.sort(list);
		while(list.size() > numSelected)
		{
			list.removeLast();
		}
		
		return list;
		
	}
	
	
	@Override
	public Cell exposure(Antigen antigen)
	{				
		// assess each component repertoire
		for (int i = 0; i < repertoire.length; i++)
		{
			assessComponentRepertoire(repertoire[i], antigen);
		}		
		// select candidate locations for work
		LinkedList<DummySolution> selectedSet = selectCoordinates();		
		
		// build a bmu
		int x = selectedSet.getFirst().x;
		int y = selectedSet.getFirst().y;
		Cell bmu = DegenerateUtils.cellFromSubCells(new SubCell[]{repertoire[0][x][y],repertoire[1][x][y],repertoire[2][x][y]});
		
		// do cloning and mutation at minimised integration sites
		for(DummySolution ds : selectedSet)
		{
			cloningMutationIntegration(antigen, ds.x, ds.y);
		}
		
		// return the bmu
		return bmu;
	}
	
	protected void cloningMutationIntegration(Antigen antigen, int x, int y)
	{			
		// process each component in turn
		for (int i = 0; i < repertoire.length; i++)
		{
			SpatialSubCell selectedCell = repertoire[i][x][y];
			// cloning and mutation
			LinkedList<SpatialSubCell> clones = SpatialUtils.cloningAndMutationSpatialSubCell(selectedCell, numClones, rand);
			// assess the clones against the antigen
			CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
			// replace the response into the repertoire
			replaceIntoSpatialRepertoire(selectedCell, clones, repertoire[i]);
		}
	}
	
	protected void assessComponentRepertoire(SpatialSubCell [][] rep, Antigen antigen)
	{
		for (int i = 0; i < rep.length; i++)
		{
			for (int j = 0; j < rep[i].length; j++)
			{
				CellUtils.assessCellAgainstAntigen(antigen, rep[i][j]);
			}
		}
	}
	
	
	/**
	 * Neighbourhood replacement
	 */
	protected void replaceIntoSpatialRepertoire(
			SpatialSubCell parentCell, 
			LinkedList<SpatialSubCell> progenySet, 
			SpatialSubCell [][] rep)
	{
		// locate the parental neighbourhood
		LinkedList<SpatialSubCell> neighbourhood = SpatialUtils.getNeighbours(parentCell, rep);
		
		// process the progeny
		for(SpatialSubCell childCell : progenySet)
		{
			// locate a cell to replace
			SpatialSubCell similar = CellUtils.getMostSimilarWithExclusion(childCell, neighbourhood, progenySet, rand);
			// fitness tournament for resources
			if(childCell.getScore() <= similar.getScore())
			{
				int [] coord = similar.getCoordinate();
				rep[coord[0]][coord[1]] = childCell;
				childCell.setCoordinate(coord);
				// also do it in the fake repertoire
				neighbourhood.remove(similar);
				neighbourhood.add(childCell);
			}
		}
	}
	
	
	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}

	@Override
	public SpatialCell[][] getSpatialRepertoire()
	{
		SpatialCell [][] rep = new SpatialCell[numCells][numCells];
		for (int i = 0; i < rep.length; i++)
		{
			for (int j = 0; j < rep[i].length; j++)
			{
				SpatialSubCell [] set = new SpatialSubCell[]{repertoire[0][i][j], repertoire[1][i][j], repertoire[2][i][j]};
				rep[i][j] = SpatialUtils.spatialCellFromSpatialSubCells(set, new int[]{i,j});
			}
		}
		
		return rep;
	}

	@Override
	public Random getRandom()
	{
		return rand;
	}
	
	protected LinkedList<SpatialSubCell> compressComponentRepertoire(SpatialSubCell [][] rep)
	{
		LinkedList<SpatialSubCell> tmp = new LinkedList<SpatialSubCell>();
		
		for (int i = 0; i < rep.length; i++)
		{
			for (int j = 0; j < rep[i].length; j++)
			{
				tmp.add(rep[i][j]);
			}
		}
		
		return tmp;
	}
	
	
	@Override
	public LinkedList<SpatialSubCell> getRepertoire()
	{
		LinkedList<SpatialSubCell> tmp = new LinkedList<SpatialSubCell>();
		
		for (int i = 0; i < repertoire.length; i++)
		{
			tmp.addAll(compressComponentRepertoire(repertoire[i]));
		}
		
		return tmp;
	}

	@Override
	public boolean isCellBased()
	{
		return false;
	}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		// num cells
		if(numCells<=0)
		{
			throw new InvalidConfigurationException("numCells must be > 0.");
		}
		// selection size
		if(!AlgorithmUtils.inBounds(numSelected, 1, numCells))
		{
			throw new InvalidConfigurationException("numSelected must be between 1 and numCells ("+numCells+"): " + numSelected);
		}
		// cloning size
		if(numClones<=0)
		{
			throw new InvalidConfigurationException("numClones must be > 0.");
		}

	}

	@Override
	public String getName()
	{
		return "SCCSA-Component (TopDown)";
	}

	public int getNumClones()
	{
		return numClones;
	}

	public void setNumClones(int numClones)
	{
		this.numClones = numClones;
	}

	public int getNumCells()
	{
		return numCells;
	}

	public void setNumCells(int numCells)
	{
		this.numCells = numCells;
	}

	public int getNumSelected()
	{
		return numSelected;
	}

	public void setNumSelected(int numSelected)
	{
		this.numSelected = numSelected;
	}
	
	public SpatialSubCell [][][] getSpatialSubCells()
	{
		return repertoire;
	}
}
