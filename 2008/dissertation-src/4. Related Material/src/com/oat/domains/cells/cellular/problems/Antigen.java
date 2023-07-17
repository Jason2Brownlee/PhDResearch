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
package com.oat.domains.cells.cellular.problems;

import java.awt.Color;
import java.util.Random;

import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;
import com.oat.domains.cells.cellular.AntigenProblem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.gui.DrawingUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BinaryDecodeMode;
import com.oat.utils.BitStringUtils;
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
public class Antigen extends Problem 
	implements AntigenProblem
{
	/**
	 * Bounds for the colour coordinates
	 */
	public final static double [][] MINMAX = new double [][]{{0,1},{0,1},{0,1}};
	/**
	 * Number of bits used per colour component
	 */
	public final static int BITS_PER_COMPONENT = 64;
	/**
	 * Number of components per colour (3 = red, green blue)
	 */
	public final static int NUM_COMPONENTS = 3;
	
	// config
	protected long seed = 99;
		
	// data
	protected double [] goalcoordinate;
	protected boolean [] bitstring;
	protected Antigen [] self;
	
	
	
	public Antigen()
	{
		self = new Antigen[]{this};
	}
	
	
	@Override
	public void initialiseBeforeRun() throws InitialisationException
	{
		Random rand = new Random(seed);
		// generate the bitstring
		boolean [] b = RandomUtils.randomBitString(rand, numGoalStateBits());
		initialiseManually(b);
	}
	
	/**
	 * Initialise manually, allows the problem to be seeded by something else
	 * @param aBitstring
	 */
	public void initialiseManually(boolean [] aBitstring)
	{
		bitstring = aBitstring;
		goalcoordinate = BitStringUtils.decode(BinaryDecodeMode.GrayCode, bitstring, MINMAX);
	}
	
	/**
	 * Number of bits needed to represent this antigen
	 * @return
	 */
	public static int numGoalStateBits()
	{
		return BITS_PER_COMPONENT * NUM_COMPONENTS;
	}
	
	@Override
	public int getNumBitsPerAntigen()
	{
		return numGoalStateBits();
	}
	
	public int getNumComponents()
	{
		return NUM_COMPONENTS;
	}

	@Override
	public void checkSolutionForSafety(Solution solution)
			throws SolutionEvaluationException
	{		
		if(solution instanceof CellSet)
		{
			validateCellSet((CellSet)solution);
		}
		else if(solution instanceof DiscreteCell)
		{			
			validateDiscreteCell((DiscreteCell)solution);
		}
		else
		{
			throw new SolutionEvaluationException("Unsupported type: " + solution.getClass().getName());
		}
	}
	
	
	/**
	 * Validate discrete cell
	 * @param discreteCell
	 * @throws SolutionEvaluationException
	 */
	protected void validateDiscreteCell(DiscreteCell discreteCell)
		throws SolutionEvaluationException
	{
		if(discreteCell instanceof Cell)
		{
			validateCell((Cell)discreteCell);
		}
		else
		{
			throw new SolutionEvaluationException("Unsupported type: " + discreteCell.getClass().getName());
		}
	}
	
	
	/**
	 * Validate a cell set
	 * @param cellSet
	 * @throws SolutionEvaluationException
	 */
	public void validateCellSet(CellSet cellSet)
		throws SolutionEvaluationException
	{
		Cell [] cells  = cellSet.getCells();
		if(cells.length != getNumAntigen())
		{
			throw new SolutionEvaluationException("Number of cells "+cells.length+" does not match the expected number " + getNumAntigen());
		}
		// check safety on each cell
		for (int i = 0; i < getNumAntigen(); i++)
		{
			getAntigen(i).checkSolutionForSafety(cells[i]);
		}
	}
	

	@Override
	public void cleanupAfterRun() throws InitialisationException
	{}	

	@Override
	public boolean isMinimization()
	{
		return true;
	}
	
	@Override
	public int getNumAntigen()
	{
		return 1;
	}
	
	@Override
	public Antigen[] getAntigen()
	{
		return self;
	}	
	

	@Override
	public Antigen getAntigen(int antigenNumber)
	{
		return self[antigenNumber];
	}


	@Override
	protected double problemSpecificCost(Solution solution)
			throws SolutionEvaluationException
	{
		double score = Double.NaN;
		
		if(solution instanceof CellSet)
		{
			CellSet set = (CellSet) solution;
			score = costCell(set.getCells()[0]);
		}		
		else if(solution instanceof DiscreteCell)
		{
			score = costDiscreteCell((DiscreteCell)solution);
		}		
		
		return score;
	}
	
	/**
	 * Assess the cost of any of the known ancestors of discrete cell
	 * @param cell
	 * @return
	 */
	public double costDiscreteCell(DiscreteCell cell)
	{
		double score = Double.NaN;
		
		if(cell instanceof Cell)
		{
			score = costCell((Cell)cell);
		}
		
		return score;
	}
	
	/**
	 * Complete cell, expect all three components
	 * @param cell
	 * @return
	 */
	public double costCell(Cell cell)
	{
		return AlgorithmUtils.euclideanDistance(cell.getDecodedData(), goalcoordinate);
	}
	
	/**
	 * Cost of a cells data
	 * @param cellData
	 * @return
	 */
	public double costCell(double [] cellData)
	{
		return AlgorithmUtils.euclideanDistance(cellData, goalcoordinate);
	}
	
	/**
	 * Calculate the Hamming distance of the cell
	 * @param cell
	 * @return
	 */
	public double costCellHamming(Cell cell)
	{
		return BitStringUtils.hammingDistance(cell.getData(), bitstring);
	}
	
	/**
	 * Calculate the Hamming distance of another antigen
	 * @param antigen
	 * @return
	 */
	public double costAntigenHamming(Antigen antigen)
	{
		if(antigen == this)
		{
			// save some calculation
			return 0;
		}
		
		return BitStringUtils.hammingDistance(antigen.bitstring, bitstring);
	}
	
	/**
	 * Validate the state of the cell
	 * @param cell
	 * @throws SolutionEvaluationException
	 */
	public void validateCell(Cell cell) throws SolutionEvaluationException
	{
		if(cell.getData().length != numGoalStateBits())
		{
			throw new SolutionEvaluationException("Solution had "+cell.getData().length+" bits, expected " + numGoalStateBits());
		}				
		double [] decoded = cell.getDecodedData();
		if(decoded.length != NUM_COMPONENTS)
		{
			throw new SolutionEvaluationException("Solution had "+decoded.length+" vector components, expected " + NUM_COMPONENTS);
		}		
		for (int i = 0; i < decoded.length; i++)
		{
			if(!AlgorithmUtils.inBounds(decoded[i], MINMAX[i][0], MINMAX[i][1]))
			{
				throw new SolutionEvaluationException("Solution has component out of bounds "+decoded[i]+", expect between "+MINMAX[i][0]+" and "+MINMAX[i][1]+".");
			}
		}	
	}
	

	

	@Override
	protected void validateConfigurationInternal()
			throws InvalidConfigurationException
	{}

	@Override
	public String getName()
	{
		return "Antigen Problem";
	}
	
	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}
	
	@Override
    public boolean isUserConfigurable()
    {
    	return true;
    }
	
	/**
	 * Returns the goal state as a colour coordinate
	 * @return
	 */
	public Color getGoalCoordinateColour()
	{
		return DrawingUtils.vectorToColor(goalcoordinate);
	}
}
