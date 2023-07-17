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
package com.oat.domains.cells.mediated;

import java.util.LinkedList;

import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellUtils;
import com.oat.domains.cells.cellular.DiscreteCell;
import com.oat.domains.cells.degenerate.DegenerateUtils;
import com.oat.domains.cells.degenerate.SubCell;

/**
 * Description: 
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
public class MediatedUtils
{
	
	
	
	/**
	 * Assess a component repertoire against a cellular repertoire with Hamming distance, scores are accumulated
	 * @param <C1>
	 * @param <C2>
	 * @param r1
	 * @param r2
	 * @param component
	 */
	public static <C1 extends SubCell, C2 extends Cell> void assessComponentRepertoireAgainstRepertoireHamming(
			LinkedList<C1> r1, 
			LinkedList<C2> r2,
			int component)
	{
		// for each cell of interest
		for(C1 c1 : r1)
		{
			double score = 0.0;
			for(C2 c2 : r2)
			{
				score += DegenerateUtils.hammingDistance(c2, c1, component);
			}
			
			if(c1.isEvaluated())
			{
				c1.evaluated(c1.getScore() + score);
			}
			else
			{
				c1.evaluated(score);
			}
		}
	}
	
	/**
	 * Assess a component repertoire against a cellular repertoire with Euclidean distance, scores are accumulated
	 * @param <C1>
	 * @param <C2>
	 * @param r1
	 * @param r2
	 * @param component
	 */
	public static <C1 extends SubCell, C2 extends Cell> void assessComponentRepertoireAgainstRepertoireEuclidean(
			LinkedList<C1> r1, 
			LinkedList<C2> r2,
			int component)
	{
		// for each cell of interest
		for(C1 c1 : r1)
		{
			double score = 0.0;
			for(C2 c2 : r2)
			{
				score += DegenerateUtils.euclideanDistance(c2, c1, component);
			}
			
			if(c1.isEvaluated())
			{
				c1.evaluated(c1.getScore() + score);
			}
			else
			{
				c1.evaluated(score);
			}
		}
	}
	
	
	
	/**
	 * Assess r1 against r2 using Hamming distance, where the sum of the distances of each c in r1 is calculated across
	 * all of r2 and stored in each c1's evaluated. Scores are accumulated
	 * 
	 * @param <C1>
	 * @param <C2>
	 * @param r1
	 * @param r2
	 * @param component
	 */
	public static <C1 extends Cell, C2 extends SubCell> void assessRepertoireAgainstComponentRepertoireHamming(
			LinkedList<C1> r1, 
			LinkedList<C2> r2,
			int component)
	{
		// for each cell of interest
		for(C1 c1 : r1)
		{
			double score = 0.0;
			for(C2 c2 : r2)
			{
				score += DegenerateUtils.hammingDistance(c1, c2, component);
			}
			
			if(c1.isEvaluated())
			{
				c1.evaluated(c1.getScore() + score);
			}
			else
			{
				c1.evaluated(score);
			}
		}
	}
	
	/**
	 * Assess r1 against r2 using Euclidean distance, where the sum of the distances of each c in r1 is calculated across
	 * all of r2 and stored in each c1's evaluated. Scores are accumulated
	 * 
	 * @param <C1>
	 * @param <C2>
	 * @param r1
	 * @param r2
	 * @param component
	 */
	public static <C1 extends Cell, C2 extends SubCell> void assessRepertoireAgainstComponentRepertoireEuclidean(
			LinkedList<C1> r1, 
			LinkedList<C2> r2,
			int component)
	{
		// for each cell of interest
		for(C1 c1 : r1)
		{
			double score = 0.0;
			for(C2 c2 : r2)
			{
				score += DegenerateUtils.euclideanDistance(c1, c2, component);
			}
			
			if(c1.isEvaluated())
			{
				c1.evaluated(c1.getScore() + score);
			}
			else
			{
				c1.evaluated(score);
			}
		}
	}
	
	/**
	 * Assess the given repertoire against the given cell using hamming distance, 
	 * and store the values in the cells evaluated
	 * 
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstCellHamming(LinkedList<C> r1, C c2)
	{
		for(C c1 : r1)
		{
			double score = c1.distance(c2);
			c1.evaluated(score);
		}
	}
	
	/**
	 *Assess the given repertoire against the given cell using Euclidean distance, 
	 * and store the values in the cells evaluated 
	 * @param <C>
	 * @param r1
	 * @param c2
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstCellEuclidean(LinkedList<C> r1, C c2)
	{
		for(C c1 : r1)
		{
			double score = CellUtils.euclideanDistance((Cell)c1, (Cell)c2);
			c1.evaluated(score);
		}
	}
	
	/**
	 * Assess r1 against r2 using Hamming distance, where the sum of the distances of each c in r1 is calculated across
	 * all of r2 and stored in each c1's evaluated
	 * @param <C>
	 * @param r1
	 * @param r2
	 */
	public static <C extends DiscreteCell> void assessRepertoireAgainstRepertoireHamming(LinkedList<C> r1, LinkedList<C> r2)
	{
		// for each cell of interest
		for(C c1 : r1)
		{
			double score = 0.0;
			for(C c2 : r2)
			{
				score += c1.distance(c2);
			}
			c1.evaluated(score);
		}
	}	
	
	/**
	 * Assess r1 against r2 using Euclidean distance, where the sum of the distances of each c in r1 is calculated across
	 * all of r2 and stored in each c1's evaluated
	 * @param <C>
	 * @param r1
	 * @param r2
	 */
	public static <C extends Cell> void assessRepertoireAgainstRepertoireEuclidean(LinkedList<C> r1, LinkedList<C> r2)
	{
		// for each cell of interest
		for(C c1 : r1)
		{
			double score = 0.0;
			for(C c2 : r2)
			{
				score += CellUtils.euclideanDistance((Cell)c1, (Cell)c2);
			}
			c1.evaluated(score);
		}
	}
}
