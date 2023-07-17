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
package com.oat.domains.cells.network;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.cells.cellular.algorithms.RCCSA;
import com.oat.domains.cells.cellular.problems.AEP_1;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.cellular.problems.AEP_2;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_PPSS;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_PSSP;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_SPPS;
import com.oat.domains.cells.network.algorithms.dualexposure.DE_NCCSA_SSPP;
import com.oat.domains.cells.network.algorithms.proxy.antigen.PNCCSA_A_PP;
import com.oat.domains.cells.network.algorithms.proxy.antigen.PNCCSA_A_PS;
import com.oat.domains.cells.network.algorithms.proxy.antigen.PNCCSA_A_SP;
import com.oat.domains.cells.network.algorithms.proxy.antigen.PNCCSA_A_SS;
import com.oat.domains.cells.network.algorithms.proxy.bmu2.PNCCSA_C_SP;
import com.oat.domains.cells.network.algorithms.proxy.bmu2.PNCCSA_C_SS;
import com.oat.domains.cells.network.algorithms.recurrent.RNCCSA_NM;
import com.oat.domains.cells.network.algorithms.recurrent.RNCCSA_PP;
import com.oat.domains.cells.network.algorithms.recurrent.RNCCSA_PS;
import com.oat.domains.cells.network.algorithms.recurrent.RNCCSA_SP;
import com.oat.domains.cells.network.algorithms.recurrent.RNCCSA_SS;
import com.oat.domains.cells.network.gui.NetworkMasterPanel;
import com.oat.domains.cells.network.probes.AverageCellMappedDiversity;
import com.oat.domains.cells.network.probes.AverageMappedBMUEuclideanError;
import com.oat.domains.cells.network.probes.AverageMappedBMUHammingError;
import com.oat.domains.cells.network.probes.AverageNetworkCellDiversity;
import com.oat.domains.cells.probes.AbsoluteCellError;
import com.oat.domains.cells.probes.AverageBMUPerAntigen;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.AverageCellDiversityRatio;
import com.oat.domains.cells.probes.AverageCellError;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

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
public class NetworkDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new NetworkMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Cellular Network";
	}

	@Override
	public String getShortName()
	{
		return "network";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		Algorithm [] a = new Algorithm[]
		                     {	
				// recurrent
				new RCCSA(),
				new RNCCSA_NM(),
				new RNCCSA_PP(),
				new RNCCSA_PS(),
				new RNCCSA_SS(),
				new RNCCSA_SP(),
				// dual exposure				
				new DE_NCCSA_PPSS(),
				new DE_NCCSA_PSSP(),				
				new DE_NCCSA_SPPS(),
				new DE_NCCSA_SSPP(),
				// proxy
				new PNCCSA_A_PP(),
				new PNCCSA_A_PS(),
				new PNCCSA_A_SP(),
				new PNCCSA_A_SS(),
				new PNCCSA_C_SP(),
				new PNCCSA_C_SS(),
				
		                     };
		return a;
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
		return new Problem[]
		                   {
				new AEP_1(),
				new AEP_2(),
				new AEP_10(),
		                   };
	}

	@Override
	public GenericProblemPlot prepareProblemPlot()
	{
		return null;
	}
	
	
	@Override
	public LinkedList<RunProbe> loadDomainRunProbes()
	{
		LinkedList<RunProbe> list = super.loadDomainRunProbes();		
		
		// cell probes		
		list.add(new AverageCellDiversity());
		list.add(new AverageCellError());
		list.add(new AverageCellDiversityRatio());
		list.add(new AbsoluteCellError());
		list.add(new AverageBMUPerAntigen());
		// network probes
		list.add(new AverageCellMappedDiversity());
		list.add(new AverageNetworkCellDiversity());
		// cell mappings
		list.add(new AverageMappedBMUEuclideanError());
		list.add(new AverageMappedBMUHammingError());
		
		Collections.sort(list);		
		return list;
	}
}
