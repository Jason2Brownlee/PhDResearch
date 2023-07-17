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
package com.oat.domains.tissues.recirulation.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.tissues.probes.AverageTissueDiversity;
import com.oat.domains.tissues.probes.AverageTissueError;
import com.oat.domains.tissues.probes.HostDiversity;
import com.oat.domains.tissues.probes.HostError;
import com.oat.domains.tissues.recirulation.RecirculationDomain;
import com.oat.domains.tissues.recirulation.algorithms.ATER.MTCSA_ATER;
import com.oat.domains.tissues.recirulation.algorithms.ATER.RTCSA_LRG_ATER;
import com.oat.domains.tissues.recirulation.algorithms.ATER.RTCSA_MED_ATER;
import com.oat.domains.tissues.recirulation.algorithms.ATER.RTCSA_SML_ATER;
import com.oat.domains.tissues.recirulation.algorithms.OTER.MTCSA_OTER;
import com.oat.domains.tissues.recirulation.algorithms.OTER.RTCSA_LRG_OTER;
import com.oat.domains.tissues.recirulation.algorithms.OTER.RTCSA_MED_OTER;
import com.oat.domains.tissues.recirulation.algorithms.OTER.RTCSA_SML_OTER;
import com.oat.domains.tissues.recirulation.algorithms.PTER.MTCSA_PTER;
import com.oat.domains.tissues.recirulation.algorithms.PTER.RTCSA_LRG_PTER;
import com.oat.domains.tissues.recirulation.algorithms.PTER.RTCSA_MED_PTER;
import com.oat.domains.tissues.recirulation.algorithms.PTER.RTCSA_SML_PTER;
import com.oat.domains.tissues.recirulation.algorithms.RTER.MTCSA_RTER;
import com.oat.domains.tissues.recirulation.algorithms.RTER.RTCSA_LRG_RTER;
import com.oat.domains.tissues.recirulation.algorithms.RTER.RTCSA_MED_RTER;
import com.oat.domains.tissues.recirulation.algorithms.RTER.RTCSA_SML_RTER;
import com.oat.domains.tissues.recirulation.algorithms.STER.MTCSA_STER;
import com.oat.domains.tissues.recirulation.algorithms.STER.RTCSA_LRG_STER;
import com.oat.domains.tissues.recirulation.algorithms.STER.RTCSA_MED_STER;
import com.oat.domains.tissues.recirulation.algorithms.STER.RTCSA_SML_STER;
import com.oat.domains.tissues.recirulation.problems.ICSP_10;
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
public class RecirulationStudy1 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new RecirulationStudy1().run();
	}

	@Override
	public LinkedList<ExperimentalRun> createRunList()
	{
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		// 30 repeats
		matrix.setRepeats(30);
		// problems
		matrix.addProblem(new ICSP_10());
		// ATER algorithms
		matrix.addAlgorithm(new MTCSA_ATER());
		matrix.addAlgorithm(new RTCSA_SML_ATER());
		matrix.addAlgorithm(new RTCSA_MED_ATER());
		matrix.addAlgorithm(new RTCSA_LRG_ATER());
		// OTER algorithms
		matrix.addAlgorithm(new MTCSA_OTER());
		matrix.addAlgorithm(new RTCSA_SML_OTER());
		matrix.addAlgorithm(new RTCSA_MED_OTER());
		matrix.addAlgorithm(new RTCSA_LRG_OTER());
		// PTER algorithms
		matrix.addAlgorithm(new MTCSA_PTER());
		matrix.addAlgorithm(new RTCSA_SML_PTER());
		matrix.addAlgorithm(new RTCSA_MED_PTER());
		matrix.addAlgorithm(new RTCSA_LRG_PTER());
		// RTER algorithms
		matrix.addAlgorithm(new MTCSA_RTER());
		matrix.addAlgorithm(new RTCSA_SML_RTER());
		matrix.addAlgorithm(new RTCSA_MED_RTER());
		matrix.addAlgorithm(new RTCSA_LRG_RTER());
		// STER algorithms
		matrix.addAlgorithm(new MTCSA_STER());
		matrix.addAlgorithm(new RTCSA_SML_STER());
		matrix.addAlgorithm(new RTCSA_MED_STER());
		matrix.addAlgorithm(new RTCSA_LRG_STER());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new RecirculationDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "RTCSA Study 1.";
	}

	@Override
	public String getExperimentName()
	{
		return "RTCSAStudy1";
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
		reportStats.add(new HostDiversity());
		reportStats.add(new HostError());
		// host level
		reportStats.add(new AverageTissueDiversity());
		reportStats.add(new AverageTissueError());	
		
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
		return 4;
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
