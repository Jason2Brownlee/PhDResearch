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
package com.oat.domains.cells.mediated.experiment;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.mediated.MediatedDomain;
import com.oat.domains.cells.mediated.algorithms.mapping.MCCSAEuclidean;
import com.oat.domains.cells.mediated.algorithms.mapping.MCCSAHamming;
import com.oat.domains.cells.mediated.probes.AverageBCellDiversity;
import com.oat.domains.cells.mediated.probes.AverageBCellError;
import com.oat.domains.cells.mediated.probes.AverageTCellDiversity;
import com.oat.domains.cells.mediated.probes.AverageTCellError;
import com.oat.domains.cells.mediated.probes.AverageTCellMappingError;
import com.oat.domains.cells.mediated.probes.AverageTCellSelectedSetMappingError;
import com.oat.domains.cells.probes.CellSetAverageEuclideanError;
import com.oat.experimenter.ClonalSelectionTemplateExperiment;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.stopcondition.EpochStopCondition;

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
public class MediatedStudy0 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new MediatedStudy0().run();
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
		matrix.addAlgorithm(new MCCSAEuclidean());
		matrix.addAlgorithm(new MCCSAHamming());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new MediatedDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "MCCSA Study 0.";
	}

	@Override
	public String getExperimentName()
	{
		return "MCCSAStudy0";
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

		// b and t cell things
		reportStats.add(new AverageBCellDiversity());
		reportStats.add(new AverageTCellDiversity());
		reportStats.add(new AverageBCellError());
		reportStats.add(new AverageTCellError());			
		// system response error
		reportStats.add(new CellSetAverageEuclideanError());
		
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
