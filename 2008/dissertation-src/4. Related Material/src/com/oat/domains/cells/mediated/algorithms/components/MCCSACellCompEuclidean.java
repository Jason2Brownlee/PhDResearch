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
package com.oat.domains.cells.mediated.algorithms.components;

import java.util.LinkedList;
import java.util.Random;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.algorithms.GenericCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.domains.cells.mediated.MediatedCellularAlgorithm;
import com.oat.domains.cells.mediated.MediatedUtils;
import com.oat.domains.cells.mediated.RepertoireInteractionListener;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: Mediated Cellular Clonal Selection Algorithm (MCCSA)
 *  
 * Date: 05/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class MCCSACellCompEuclidean extends GenericCellularClonalSelectionAlgorithm<Cell>
		implements MediatedCellularAlgorithm<Cell, SubCell>
{			
	// config
	protected long seed = 1;	
	// b-cell configuration
	protected int numBCells = 100;
	protected int numBCellsSelected = 1;
	protected int numBCellClones = 10;
	 	
	// t-cell configuration
	protected int numTCells = 100; 
	protected int numTCellsSelected = 3;
	protected int numTCellClones = 1; 
	
	// data
	protected Random rand;
	protected LinkedList<Cell> bCells;
	protected LinkedList<SubCell> tCells;
	protected LinkedList<RepertoireInteractionListener> listeners;
	
	
	/**
	 * Constructor
	 */
	public MCCSACellCompEuclidean()
	{
		listeners = new LinkedList<RepertoireInteractionListener>();
	}
	
	@Override
	public void registerRepertoireInteractionListener(RepertoireInteractionListener l)
	{
		listeners.add(l);
	}
	
	@Override
	public boolean removeRepertoireInteractionListener(RepertoireInteractionListener l)
	{
		return listeners.remove(l);
	}
	
	protected void triggerRepertoireInteractionEvent(Antigen antigen, LinkedList<Cell>  selectedBCells, LinkedList<SubCell> [] selectedTCells)
	{
		if(listeners.isEmpty())
		{
			return;
		}
		
		LinkedList<SubCell> tmp = new LinkedList<SubCell>();
		for (int i = 0; i < selectedTCells.length; i++)
		{
			tmp.addAll(selectedTCells[i]);
		}
		
		for(RepertoireInteractionListener l : listeners)
		{
			l.interaction(antigen, this, selectedBCells, tmp);
		}
	}
	
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		AntigenProblem ap = (AntigenProblem) problem;
		bCells = CellUtils.getRandomCellRepertoire(rand, numBCells, ap.getNumBitsPerAntigen());
		tCells = DegenerateUtils.getRandomSubCellRepertoire(rand, numTCells, Antigen.BITS_PER_COMPONENT);		
		// no initial population
		return null;
	}
	
	protected void accessComponentRepertoireAgainstRepertoire(
			LinkedList<SubCell> tCellSet, 
			LinkedList<Cell> bCellSet, 
			int component)
	{
		// euclidean distance
		MediatedUtils.assessComponentRepertoireAgainstRepertoireEuclidean(tCellSet, bCellSet, component);
	}	
	
	protected LinkedList<Cell> fowardPropagateBCells(Antigen antigen)
	{
		// assess repertoire
		CellUtils.assessRepertoireAgainstAntigen(antigen, bCells);
		// select the activated set
		LinkedList<Cell> selectedBCells = CellUtils.selectCellSet(bCells, rand, numBCellsSelected);
		return selectedBCells;
	}
	
	protected LinkedList<SubCell> [] fowardPropagateTCells(LinkedList<Cell> selectedBCells)
	{
		LinkedList<SubCell> [] selectedTCells = new LinkedList[Antigen.NUM_COMPONENTS];
						
		// process each component
		for (int i = 0; i < selectedTCells.length; i++)
		{
			// clear scores
			CellUtils.clearScores(tCells, 0);
			// assign component of interest
			DegenerateUtils.assignComponentOfInterest(i, tCells);
			// assess repertoire
			accessComponentRepertoireAgainstRepertoire(tCells, selectedBCells, i);
			// select the activated set
			selectedTCells[i] = CellUtils.selectCellSet(tCells, rand, numTCellsSelected);
		}		
		return selectedTCells;
	}
	
	protected void backPropagateBCells(Antigen antigen, LinkedList<Cell> selectedBCells)
	{
		// cloning and mutation
		LinkedList<Cell> clones = CellUtils.cloningAndMutationCell(selectedBCells, numBCellClones, rand);
		// assess the clones against the antigen
		CellUtils.assessRepertoireAgainstAntigen(antigen, clones);
		// replace the response into the repertoire
		CellUtils.replaceIntoRepertoireSimilarityScore(clones, bCells, rand);
	}

	
	protected void backPropagateTCells(LinkedList<Cell> selectedBCells, LinkedList<SubCell> [] selectedTCells)
	{
		for (int i = 0; i < selectedTCells.length; i++)
		{
			// cloning and mutation
			LinkedList<SubCell> clones = DegenerateUtils.cloningAndMutationSubCell(selectedTCells[i], numTCellClones, rand);
			// assign component of interest
			DegenerateUtils.assignComponentOfInterest(i, clones);
			// assess
			accessComponentRepertoireAgainstRepertoire(clones, selectedBCells, i);
			// replace the response into the repertoire
			CellUtils.replaceIntoRepertoireSimilarityScore(clones, tCells, rand);
		}
	}
	
	@Override
	public Cell exposure(Antigen antigen)
	{
		// foward pass
		LinkedList<Cell> selectedBCells = fowardPropagateBCells(antigen);
		LinkedList<SubCell> [] selectedTCells = fowardPropagateTCells(selectedBCells);		
		// trigger an event
		triggerRepertoireInteractionEvent(antigen, selectedBCells, selectedTCells);
		// backward pass
		backPropagateBCells(antigen, selectedBCells);
		backPropagateTCells(selectedBCells, selectedTCells);
		
		// build a bmu
		SubCell [] bmus = new SubCell[selectedTCells.length];
		for (int i = 0; i < bmus.length; i++)
		{
			bmus[i] = CellUtils.getRepertoireBMU(selectedTCells[i], rand);
		}
		return DegenerateUtils.cellFromSubCells(bmus);		
	}
	

	@Override
	public LinkedList<Cell> getRepertoire()
	{
		return getBCells();
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
		if(numBCells<=0)
		{
			throw new InvalidConfigurationException("numBCells must be > 0.");
		}
		// selection size
		if(!AlgorithmUtils.inBounds(numBCellsSelected, 1, numBCells))
		{
			throw new InvalidConfigurationException("numBCellsSelected must be between 1 and numBCells ("+numBCells+"): " + numBCellsSelected);
		}
		// cloning size
		if(numBCellClones<=0)
		{
			throw new InvalidConfigurationException("numBCellClones must be > 0.");
		}
		// num cells
		if(numTCells<=0)
		{
			throw new InvalidConfigurationException("numTCells must be > 0.");
		}
		// selection size
		if(!AlgorithmUtils.inBounds(numTCellsSelected, 1, numTCells))
		{
			throw new InvalidConfigurationException("numTCellsSelected must be between 1 and numTCells ("+numTCells+"): " + numTCellsSelected);
		}
		// cloning size
		if(numTCellClones<=0)
		{
			throw new InvalidConfigurationException("numTCellClones must be > 0.");
		}		
	}

	@Override
	public LinkedList<Cell> getBCells()
	{
		return bCells;
	}

	@Override
	public LinkedList<SubCell> getTCells()
	{
		return tCells;
	}

	@Override
	public Random getRandom()
	{
		return rand;
	}

	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<CellSet> oldPopulation, LinkedList<CellSet> newPopulation)
	{}

	@Override
	public String getName()
	{
		return "MCCSA Cell-to-Comp (Euclidean)";
	}

	public int getNumBCellClones()
	{
		return numBCellClones;
	}

	public void setNumBCellClones(int numBCellClones)
	{
		this.numBCellClones = numBCellClones;
	}

	public int getNumBCells()
	{
		return numBCells;
	}

	public void setNumBCells(int numBCells)
	{
		this.numBCells = numBCells;
	}

	public int getNumBCellsSelected()
	{
		return numBCellsSelected;
	}

	public void setNumBCellsSelected(int numBCellsSelected)
	{
		this.numBCellsSelected = numBCellsSelected;
	}

	public int getNumTCellClones()
	{
		return numTCellClones;
	}

	public void setNumTCellClones(int numTCellClones)
	{
		this.numTCellClones = numTCellClones;
	}

	public int getNumTCells()
	{
		return numTCells;
	}

	public void setNumTCells(int numTCells)
	{
		this.numTCells = numTCells;
	}

	public int getNumTCellsSelected()
	{
		return numTCellsSelected;
	}

	public void setNumTCellsSelected(int numTCellsSelected)
	{
		this.numTCellsSelected = numTCellsSelected;
	}
}
