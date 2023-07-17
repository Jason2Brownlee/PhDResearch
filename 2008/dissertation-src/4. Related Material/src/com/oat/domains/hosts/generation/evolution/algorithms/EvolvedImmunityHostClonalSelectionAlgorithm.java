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
package com.oat.domains.hosts.generation.evolution.algorithms;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Problem;
import com.oat.domains.cells.cellular.Cell;
import com.oat.domains.cells.cellular.CellSet;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.hosts.HabitatProblem;
import com.oat.domains.hosts.Host;
import com.oat.domains.hosts.generation.GenerationalHostClonalSelectionAlgorithm;
import com.oat.domains.hosts.generation.evolution.EvolvedUtils;
import com.oat.domains.hosts.probes.AverageHostError;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;

/**
 * Description: 
 *  
 * Date: 25/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class EvolvedImmunityHostClonalSelectionAlgorithm extends
		GenerationalHostClonalSelectionAlgorithm
{
	// repertoire wide crossover
	protected double crossoverProbability = 0.90;
	// mutation probability per cell (50 in 9600 bits)
	// 1 per pattern (3*64)
	protected double mutationProbability = 1.0 / Antigen.numGoalStateBits();
	
	// state 
	protected boolean [][][] genetics;

	

	@Override
	protected Host[] createNextGeneration(Host[] pop, HabitatProblem p)
	{
		// assess current parents
		double [] scores = new double[numHosts];
		for (int i = 0; i < scores.length; i++)
		{
			scores[i] = AverageHostError.calculateHostBMUError(pop[i], p.getHabitats());
		}		
		
		// select parents
		int [] parents = new int[numHosts];
		for (int i = 0; i < numHosts; i++)
		{
			// make selection
			parents[i] = EvolutionUtils.biasedRouletteWheelSelection(scores, rand);
		}
		
		boolean [][][] newGenetics = new boolean[numHosts][][];
		Host [] nextGeneration = new Host[numHosts];
		
		// create children
		for (int i = 0; i < numHosts; i+=2)
		{
			int p1 = parents[i];
			int p2 = parents[i+1];
			// create
			boolean [][][] twoChildren = createChildren(genetics[p1], genetics[p2]);
			for (int j = 0; j < twoChildren.length; j++)
			{
				// save genetics
				newGenetics[i+j] = twoChildren[j];
				// create a the host 
				nextGeneration[i+j] = createHost(twoChildren[j]);				 
			}
		}
		
		// replace genetics
		genetics = newGenetics;
		// return next generation
		return nextGeneration;
	}
	
	protected boolean [][][] createChildren(boolean [][] p1, boolean [][] p2)
	{
		boolean [][][] children = new boolean [2][Host.NUM_CELLS][];		
		// default no cross point
		int crossPoint = 0;
		
		if(rand.nextDouble() < crossoverProbability)
		{
			do
			{
				crossPoint = rand.nextInt(Host.NUM_CELLS); 
			}
			// force a cross point
			while(crossPoint==0 && crossPoint==Host.NUM_CELLS-1);
		}
		
		// replicate all cells
		for (int i = 0; i < Host.NUM_CELLS; i++)
		{			 			
			if(i < crossPoint)
			{
				// straight copy
				children[0][i] = ArrayUtils.copyArray(p1[i]);
				children[1][i] = ArrayUtils.copyArray(p2[i]);
			}
			else
			{
				// reverse
				children[1][i] = ArrayUtils.copyArray(p1[i]);
				children[0][i] = ArrayUtils.copyArray(p2[i]);
			}
			
			// mutation
			EvolutionUtils.binaryMutate(children[1][i], rand, mutationProbability);
			EvolutionUtils.binaryMutate(children[0][i], rand, mutationProbability);
		}
		
		
		return children;
	}
	
	
	@Override
	protected LinkedList<CellSet> internalInitialiseBeforeRun(Problem problem)
	{		
		epochCount = 0;
		rand = new Random(seed);
		
		// prepare exposure things
		HabitatProblem p = (HabitatProblem) problem;
		initialiseProbabilisticExposures(p.getNumHabitats());
		// prepare genetics
		prepareGenetics(Antigen.numGoalStateBits());
		// prepare hosts
		hosts = prepareHosts(problem);		
		return null;
	}	
	
	protected void prepareGenetics(int bitsPerProblem)
	{
		genetics = new boolean[numHosts][][];
		for (int i = 0; i < genetics.length; i++)
		{
			genetics[i] = EvolvedUtils.getRandomBinaryRepertoireArray(rand, Host.NUM_CELLS, bitsPerProblem);
		}
	}
	
	protected Host[] prepareHosts(Problem problem)
	{
		EvolvedHost [] ccsas = new EvolvedHost[numHosts];
		for (int i = 0; i < ccsas.length; i++)
		{
			ccsas[i] = createHost(genetics[i]);
		}
		
		return ccsas;
	}
	
	protected EvolvedHost createHost(boolean [][] cellData)
	{
		EvolvedHost host = new EvolvedHost();
		
		host.setSeed(rand.nextLong());

		
		// create repertoire
		LinkedList<Cell> cells = new LinkedList<Cell>();
		for (int j = 0; j < Host.NUM_CELLS; j++)
		{
			cells.add(new Cell(cellData[j]));
		}		
		// initialize
		host.evolveInternalInitialiseBeforeRun(cells);
		
		return host;
	}
	
	
	@Override
	public String getName()
	{
		return "Evolved Immunity Host Clonal Selection Algorithm (EI-HCSA)";
	}
}
