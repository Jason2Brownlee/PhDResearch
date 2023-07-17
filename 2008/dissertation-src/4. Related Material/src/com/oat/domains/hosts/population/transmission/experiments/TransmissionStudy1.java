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
package com.oat.domains.hosts.population.transmission.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.hosts.population.transmission.TransmissionDomain;
import com.oat.domains.hosts.population.transmission.algorithms.AHER.MPHCSA_AHER;
import com.oat.domains.hosts.population.transmission.algorithms.AHER.PT_PHCSA_RP_AHER;
import com.oat.domains.hosts.population.transmission.algorithms.AHER.PT_PHCSA_SP_AHER;
import com.oat.domains.hosts.population.transmission.algorithms.AHER.VT_PHCSA_LRG_AHER;
import com.oat.domains.hosts.population.transmission.algorithms.AHER.VT_PHCSA_SML_AHER;
import com.oat.domains.hosts.population.transmission.algorithms.OHER.MPHCSA_OHER;
import com.oat.domains.hosts.population.transmission.algorithms.OHER.PT_PHCSA_RP_OHER;
import com.oat.domains.hosts.population.transmission.algorithms.OHER.PT_PHCSA_SP_OHER;
import com.oat.domains.hosts.population.transmission.algorithms.OHER.VT_PHCSA_LRG_OHER;
import com.oat.domains.hosts.population.transmission.algorithms.OHER.VT_PHCSA_SML_OHER;
import com.oat.domains.hosts.population.transmission.algorithms.PHER.MPHCSA_PHER;
import com.oat.domains.hosts.population.transmission.algorithms.PHER.PT_PHCSA_RP_PHER;
import com.oat.domains.hosts.population.transmission.algorithms.PHER.PT_PHCSA_SP_PHER;
import com.oat.domains.hosts.population.transmission.algorithms.PHER.VT_PHCSA_LRG_PHER;
import com.oat.domains.hosts.population.transmission.algorithms.PHER.VT_PHCSA_SML_PHER;
import com.oat.domains.hosts.population.transmission.algorithms.RHER.MPHCSA_RHER;
import com.oat.domains.hosts.population.transmission.algorithms.RHER.PT_PHCSA_RP_RHER;
import com.oat.domains.hosts.population.transmission.algorithms.RHER.PT_PHCSA_SP_RHER;
import com.oat.domains.hosts.population.transmission.algorithms.RHER.VT_PHCSA_LRG_RHER;
import com.oat.domains.hosts.population.transmission.algorithms.RHER.VT_PHCSA_SML_RHER;
import com.oat.domains.hosts.population.transmission.algorithms.SHER.MPHCSA_SHER;
import com.oat.domains.hosts.population.transmission.algorithms.SHER.PT_PHCSA_RP_SHER;
import com.oat.domains.hosts.population.transmission.algorithms.SHER.PT_PHCSA_SP_SHER;
import com.oat.domains.hosts.population.transmission.algorithms.SHER.VT_PHCSA_LRG_SHER;
import com.oat.domains.hosts.population.transmission.algorithms.SHER.VT_PHCSA_SML_SHER;
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
 * Date: 20/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class TransmissionStudy1 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new TransmissionStudy1().run();
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
		matrix.addAlgorithm(new MPHCSA_AHER());
		matrix.addAlgorithm(new PT_PHCSA_RP_AHER());
		matrix.addAlgorithm(new PT_PHCSA_SP_AHER());
		matrix.addAlgorithm(new VT_PHCSA_LRG_AHER());
		matrix.addAlgorithm(new VT_PHCSA_SML_AHER());
		// OHER algorithms
		matrix.addAlgorithm(new MPHCSA_OHER());
		matrix.addAlgorithm(new PT_PHCSA_RP_OHER());
		matrix.addAlgorithm(new PT_PHCSA_SP_OHER());
		matrix.addAlgorithm(new VT_PHCSA_LRG_OHER());
		matrix.addAlgorithm(new VT_PHCSA_SML_OHER());
		// PHER algorithms
		matrix.addAlgorithm(new MPHCSA_PHER());
		matrix.addAlgorithm(new PT_PHCSA_RP_PHER());
		matrix.addAlgorithm(new PT_PHCSA_SP_PHER());
		matrix.addAlgorithm(new VT_PHCSA_LRG_PHER());
		matrix.addAlgorithm(new VT_PHCSA_SML_PHER());
		// RHER algorithms
		matrix.addAlgorithm(new MPHCSA_RHER());
		matrix.addAlgorithm(new PT_PHCSA_RP_RHER());
		matrix.addAlgorithm(new PT_PHCSA_SP_RHER());
		matrix.addAlgorithm(new VT_PHCSA_LRG_RHER());
		matrix.addAlgorithm(new VT_PHCSA_SML_RHER());
		// SHER algorithms
		matrix.addAlgorithm(new MPHCSA_SHER());
		matrix.addAlgorithm(new PT_PHCSA_RP_SHER());
		matrix.addAlgorithm(new PT_PHCSA_SP_SHER());
		matrix.addAlgorithm(new VT_PHCSA_LRG_SHER());
		matrix.addAlgorithm(new VT_PHCSA_SML_SHER());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new TransmissionDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "TPHCSA Study 1.";
	}

	@Override
	public String getExperimentName()
	{
		return "TPHCSAStudy1";
	}

	@Override
	public StopCondition getStopCondition()
	{
		return new FoundOptimaOrMaxEpochs(1000);
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
	protected int algorithmStackSize()
	{
		return 5;
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
