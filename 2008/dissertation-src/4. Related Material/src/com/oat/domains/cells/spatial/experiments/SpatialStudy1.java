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
package com.oat.domains.cells.spatial.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.cells.cellular.algorithms.RCCSA;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.probes.AverageBMUPerAntigen;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.AverageCellError;
import com.oat.domains.cells.spatial.SpatialDomain;
import com.oat.domains.cells.spatial.algorithms.comparison.SCCSA_HR;
import com.oat.domains.cells.spatial.algorithms.comparison.SCCSA_NR;
import com.oat.domains.cells.spatial.probes.AverageCellNeighbourhoodDiversity;
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
public class SpatialStudy1 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new SpatialStudy1().run();
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
		matrix.addAlgorithm(new RCCSA(100, 2, 5));
		matrix.addAlgorithm(new SCCSA_HR());
		matrix.addAlgorithm(new SCCSA_NR());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new SpatialDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "SCCSA Study 1.";
	}

	@Override
	public String getExperimentName()
	{
		return "SCCSAStudy1";
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
		reportStats.add(new AverageCellNeighbourhoodDiversity());
		
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
		return 3;
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
