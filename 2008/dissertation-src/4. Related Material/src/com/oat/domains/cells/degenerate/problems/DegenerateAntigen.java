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
package com.oat.domains.cells.degenerate.problems;

import com.oat.SolutionEvaluationException;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.degenerate.DegenerateCell;
import com.oat.domains.cells.degenerate.SubCell;
import com.oat.utils.AlgorithmUtils;

/**
 * Description: 
 *  
 * Date: 09/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DegenerateAntigen extends Antigen
{
	@Override
	protected void validateDiscreteCell(DiscreteCell discreteCell)
		throws SolutionEvaluationException
	{
		if(discreteCell instanceof Cell)
		{
			validateCell((Cell)discreteCell);
		}
		else if(discreteCell instanceof SubCell)
		{
			validateSubCell((SubCell)discreteCell);
		}		
		else if(discreteCell instanceof DegenerateCell)
		{
			validateDegenerateCell((DegenerateCell)discreteCell);
		}	
		else
		{
			throw new SolutionEvaluationException("Unsupported type: " + discreteCell.getClass().getName());
		}
	}
	
	@Override
	public double costDiscreteCell(DiscreteCell cell)
	{
		double score = Double.NaN;
		
		if(cell instanceof Cell)
		{
			score = costCell((Cell)cell);
		}
		else if(cell instanceof SubCell)
		{
			score = costSubCell((SubCell)cell);
		}		
		else if(cell instanceof DegenerateCell)
		{
			score = costDegenerateCell((DegenerateCell)cell);
		}	
		
		return score;
	}
	
	
	/**
	 * Cost of a sub cell (single component)
	 * @param cell
	 * @return
	 */
	public double costSubCell(SubCell cell)
	{
		return costSubCell(cell, cell.getComponent());
	}
	
	/**
	 * Cost of a sub cell (single component)
	 * @param cell
	 * @param componentNumber
	 * @return
	 */
	public double costSubCell(SubCell cell, int componentNumber)
	{
		return costSubCell(cell.getDecodedData(), componentNumber);
	}
	
	/**
	 * Cost of a sub cell (single component)
	 * @param subCellValue
	 * @param component
	 * @return
	 */
	public double costSubCell(double subCellValue, int component)
	{
		// same as below, and faster
		double diff = subCellValue - goalcoordinate[component];
		return Math.abs(diff);
	}
	
	
	/**
	 * Validate a cell that represents a single component 
	 * @param cell
	 * @throws SolutionEvaluationException
	 */
	public void validateSubCell(SubCell cell) throws SolutionEvaluationException
	{
		int component = cell.getComponent();
		if(!AlgorithmUtils.inBounds(component, 0, 2))
		{
			throw new SolutionEvaluationException("Solution has invalid component "+component+", expect between 0 and 2");
		}
		if(cell.getData().length != BITS_PER_COMPONENT)
		{
			throw new SolutionEvaluationException("Solution had "+cell.getData().length+" bits, expected " + BITS_PER_COMPONENT);
		}
		if(!AlgorithmUtils.inBounds(cell.getDecodedData(), MINMAX[0][0], MINMAX[0][1]))
		{
			throw new SolutionEvaluationException("Solution has component out of bounds "+cell.getDecodedData()+", expect between "+MINMAX[component][0]+" and "+MINMAX[component][1]+".");
		}
	}
	
	/**
	 * Calculate the Hamming distance for a degenerate cell
	 * @param cell
	 * @return
	 */
	public double costDegenerateCell(DegenerateCell cell)
	{
		boolean [] data = cell.getData();
		boolean [] mask = cell.getMask();
		
		// number of differences
		int count = 0;		
		for (int i = 0; i < mask.length; i++)
		{
			if(mask[i])
			{
				// check for match
				if(data[i] != bitstring[i])
				{
					count++;
				}
			}
		}
		
		return count;
	}	
	
	/**
	 * Validate a degenerate cell
	 * @param cell
	 * @throws SolutionEvaluationException
	 */
	public void validateDegenerateCell(DegenerateCell cell) throws SolutionEvaluationException
	{
		if(cell.getData().length != numGoalStateBits())
		{
			throw new SolutionEvaluationException("Solution had "+cell.getData().length+" bits, expected " + numGoalStateBits());
		}
		if(cell.getMask().length != numGoalStateBits())
		{
			throw new SolutionEvaluationException("Solution had a mask of "+cell.getMask().length+" bits, expected " + numGoalStateBits());
		}
	}
	
	@Override
	public String getName()
	{
		return "Degenerate Antigen Problem";
	}
}
