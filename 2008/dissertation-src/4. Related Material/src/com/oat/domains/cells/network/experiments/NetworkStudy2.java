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
package com.oat.domains.cells.network.experiments;

import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.network.NetworkDomain;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_PPSS;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_PSSP;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_SPPS;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_SSPP;
import com.oat.domains.cells.network.probes.AverageCellMappedDiversity;
import com.oat.domains.cells.network.probes.AverageNetworkCellDiversity;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.CellSetAverageEuclideanError;
import com.oat.experimenter.ClonalSelectionTemplateExperiment;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.stopcondition.EpochStopCondition;

/**
 * Description: Recurrent 
 *  
 * Date: 06/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class NetworkStudy2 extends ClonalSelectionTemplateExperiment
{
	public static void main(String[] args)
	{
		new NetworkStudy2().run();
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
		matrix.addAlgorithm(new DE_NCCSA_PPSS());
		matrix.addAlgorithm(new DE_NCCSA_PSSP());		
		matrix.addAlgorithm(new DE_NCCSA_SPPS());
		matrix.addAlgorithm(new DE_NCCSA_SSPP());
		
		return matrix.toRunList();
	}

	@Override
	public Domain getDomain()
	{
		return new NetworkDomain();
	}

	@Override
	public String getExperimentDescription()
	{
		return "NCCSA Study 2.";
	}

	@Override
	public String getExperimentName()
	{
		return "NCCSAStudy2";
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
		LinkedList<RunProbe> list = new LinkedList<RunProbe>();		
		
		// cell probes
		list.add(new AverageCellDiversity()); // normal diversity
		list.add(new CellSetAverageEuclideanError()); // response error
		// network probes
		list.add(new AverageCellMappedDiversity());
		list.add(new AverageNetworkCellDiversity());
		
			
		return list;
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
