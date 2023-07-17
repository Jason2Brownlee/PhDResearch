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
package com.oat.domains.cells.cellular.problems;

import java.util.Random;

import com.oat.HasKnownGlobalOptima;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.degenerate.DegenerateCell;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.MeasureUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
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
public class AntigenColourSpaceProblem extends Problem 
	implements AntigenProblem, HasKnownGlobalOptima
{
	/**
	 * Minimum distance between antigen
	 */
	public final static int MIN_ANTIGEN_DISTANCE = 64;
	
	// config
	protected int numAntigen = 10;
	protected long seed = 5;
	
	// state
	protected Antigen [] antigen;
	

	@Override
	public void checkSolutionForSafety(Solution solution)
			throws SolutionEvaluationException
	{
		if(!(solution instanceof CellSet))
		{
			throw new SolutionEvaluationException("Unsupported type: " + solution.getClass().getName());
		}		
		
		CellSet c = (CellSet) solution;
		Cell [] cells  = c.getCells();
		if(cells.length != numAntigen)
		{
			throw new SolutionEvaluationException("Number of cells "+cells.length+" does not match the expected number " +numAntigen);
		}
		// check safety on each cell
		for (int i = 0; i < numAntigen; i++)
		{
			antigen[i].checkSolutionForSafety(cells[i]);
		}
	}

	@Override
	public int getNumBitsPerAntigen()
	{
		return Antigen.numGoalStateBits();
	}
	
	@Override
	public void cleanupAfterRun() throws InitialisationException
	{
		// cleanup if required
		for (int i = 0; i < numAntigen; i++)
		{
			antigen[i].cleanupAfterRun();
		}
	}

	@Override
	public void initialiseBeforeRun() throws InitialisationException
	{
		Random rand = new Random(seed);				
		// create the random set
		boolean [][] set = RandomUtils.randomBitStringSet(rand, Antigen.numGoalStateBits(), numAntigen, MIN_ANTIGEN_DISTANCE);				
		// create the problems
		antigen = new Antigen[numAntigen];
		for (int i = 0; i < numAntigen; i++)
		{
			// create
			antigen[i] = new Antigen();
			// initialise
			antigen[i].initialiseManually(set[i]);
		}
	}

	@Override
	public boolean isMinimization()
	{
		return true;
	}

	@Override
	protected double problemSpecificCost(Solution solution)
			throws SolutionEvaluationException
	{
		CellSet c = (CellSet) solution;	
		Cell [] cells = c.getCells();
		double [] errors = new double[numAntigen];
		// process all patterns, expect cells are provided in order of the patterns
		for (int i = 0; i < numAntigen; i++)
		{
			errors[i] = costCell(cells[i], i);
		}
		
		// average euclidean error (AEE)
		return MeasureUtils.calculateAverageError(errors);
	}
	
	/**
	 * Assess the cost of the provided cell on a known antigen number
	 * @param solution
	 * @param antigenNumber
	 * @return
	 */
	public double costCell(Cell solution, int antigenNumber)		
	{
		return antigen[antigenNumber].costCell(solution);		
	}


	@Override
	protected void validateConfigurationInternal()
			throws InvalidConfigurationException
	{
		if(!AlgorithmUtils.inBounds(numAntigen, 1, Integer.MAX_VALUE))
		{
			throw new InvalidConfigurationException("numAntigen must be > 0");
		}
	}
	
	@Override
    public boolean isUserConfigurable()
    {
    	return true;
    }

	@Override
	public Antigen[] getAntigen()
	{
		return antigen;
	}

	@Override
	public int getNumAntigen()
	{
		return numAntigen;
	}
	
	@Override
	public Antigen getAntigen(int antigenNumber)
	{
		return antigen[antigenNumber];
	}

	@Override
	public String getName()
	{
		return "Antigen Colour Space Problem (ACSP)";
	}

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}

	public void setNumAntigen(int numAntigen)
	{
		this.numAntigen = numAntigen;
	}

	@Override
	public Solution[] getKnownGlobalOptima()
	{
		if(antigen==null)
		{
			return null;
		}
		
		Cell [] cells = new Cell[antigen.length];
		for (int i = 0; i < antigen.length; i++)
		{
			cells[i] = new Cell(antigen[i].bitstring);
		}
		CellSet set = new CellSet(cells);
		
		// assess the scoring
		double cost = problemSpecificCost(set);
		set.evaluated(cost);
		
		return new Solution[]{set};
	}	
}
