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
package com.oat.domains.cells.cellular.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.cells.cellular.CellularDomain;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANN;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANNGrouped;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANNSample;
import com.oat.domains.cells.cellular.algorithms.replacement.RCCSA_H;
import com.oat.domains.cells.cellular.algorithms.replacement.RCCSA_H_EP;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.probes.AverageBMUPerAntigen;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.AverageCellError;
import com.oat.experimenter.ClonalSelectionTemplateExperiment;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.stopcondition.EpochStopCondition;

/**
 * Description: Cellular Study
 *  
 * Date: 02/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ReplacementStudy2 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new ReplacementStudy2().run();
	}

	@Override
	public LinkedList<ExperimentalRun> createRunList()
	{
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		// 30 repeats
		matrix.setRepeats(30);
		// problems
		matrix.addProblem(new AEP_10());
		// algorithms
		matrix.addAlgorithm(new CCSANN());
		matrix.addAlgorithm(new CCSANNGrouped());
		matrix.addAlgorithm(new CCSANNSample());
		matrix.addAlgorithm(new RCCSA_H());
		matrix.addAlgorithm(new RCCSA_H_EP());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new CellularDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "CCSA Replacement Study (cellular study 2).";
	}

	@Override
	public String getExperimentName()
	{
		return "CCSAStudy2";
	}

	@Override
	public StopCondition getStopCondition()
	{
		// stop after 1000 epochs, consistent
		return new EpochStopCondition(1000);
	}
	
	@Override
	public LinkedList<RunProbe> getReportingStatistics()
	{
		// select statistics to report on
		LinkedList<RunProbe> reportStats = new LinkedList<RunProbe>();

		// repertoire state
		reportStats.add(new AverageCellDiversity());
		reportStats.add(new AverageCellError());
		reportStats.add(new AverageBMUPerAntigen());		
		
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
