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
package com.oat.domains.hosts.generation.evolution.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.hosts.generation.evolution.EvolvedDomain;
import com.oat.domains.hosts.generation.evolution.algorithms.AHER.EI_HCSA_AHER;
import com.oat.domains.hosts.generation.evolution.algorithms.OHER.EI_HCSA_OHER;
import com.oat.domains.hosts.generation.evolution.algorithms.PHER.EI_HCSA_PHER;
import com.oat.domains.hosts.generation.evolution.algorithms.RHER.EI_HCSA_RHER;
import com.oat.domains.hosts.generation.evolution.algorithms.SHER.EI_HCSA_SHER;
import com.oat.domains.hosts.generation.maternal.algorithms.AHER.MGHCSA_AHER;
import com.oat.domains.hosts.generation.maternal.algorithms.OHER.MGHCSA_OHER;
import com.oat.domains.hosts.generation.maternal.algorithms.PHER.MGHCSA_PHER;
import com.oat.domains.hosts.generation.maternal.algorithms.RHER.MGHCSA_RHER;
import com.oat.domains.hosts.generation.maternal.algorithms.SHER.MGHCSA_SHER;
import com.oat.domains.hosts.population.transmission.problems.HCSP_10;
import com.oat.domains.hosts.probes.AverageHostDiversity;
import com.oat.domains.hosts.probes.AverageHostError;
import com.oat.domains.hosts.probes.PopulationDiversity;
import com.oat.domains.hosts.probes.PopulationError;
import com.oat.experimenter.ClonalSelectionTemplateExperiment;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.stopcondition.FoundOptimaOrMaxEpochs;

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
public class EvolvedStudy1 extends ClonalSelectionTemplateExperiment
{
	public final static int NUM_EPOCHS = 9999;
	
	public static void main(String[] args)
	{
		new EvolvedStudy1().run();
	}

	@Override
	public LinkedList<ExperimentalRun> createRunList()
	{
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		// 30 repeats
		matrix.setRepeats(30);
		// problems
		matrix.addProblem(new HCSP_10());
		// AHER algorithms		
		matrix.addAlgorithm(new MGHCSA_AHER());
		matrix.addAlgorithm(new EI_HCSA_AHER());
		// OHER algorithms
		matrix.addAlgorithm(new MGHCSA_OHER());
		matrix.addAlgorithm(new EI_HCSA_OHER());
		// PHER algorithms
		matrix.addAlgorithm(new MGHCSA_PHER());
		matrix.addAlgorithm(new EI_HCSA_PHER());
		// RHER algorithms
		matrix.addAlgorithm(new MGHCSA_RHER());
		matrix.addAlgorithm(new EI_HCSA_RHER());
		// SHER algorithms
		matrix.addAlgorithm(new MGHCSA_SHER());
		matrix.addAlgorithm(new EI_HCSA_SHER());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new EvolvedDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "EIHCSA Study 1.";
	}

	@Override
	public String getExperimentName()
	{
		return "EIHCSAStudy1";
	}

	@Override
	public StopCondition getStopCondition()
	{
		return new FoundOptimaOrMaxEpochs(NUM_EPOCHS);
	}
	
	@Override
	public LinkedList<RunProbe> getReportingStatistics()
	{
		// select statistics to report on
		LinkedList<RunProbe> reportStats = new LinkedList<RunProbe>();
		// system level
		reportStats.add(new PopulationDiversity());
		reportStats.add(new PopulationError());
		// host level
		reportStats.add(new AverageHostDiversity());
		reportStats.add(new AverageHostError());
		
		return reportStats;
	}
	
	@Override
	public boolean performAnalysys()
	{
		return true;
	}
	
	@Override
	protected int algorithmStackSize()
	{
		return 2;
	}	
	public StatisticalComparisonTest getStatisticalComparisonTestInstance()
	{
		return new MannWhitneyUTest();
	}	
	public boolean useStatisticalSignificanceHack()
	{
		return true;
	}
}
