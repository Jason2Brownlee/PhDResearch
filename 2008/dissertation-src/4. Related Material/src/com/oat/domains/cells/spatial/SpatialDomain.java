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
package com.oat.domains.cells.spatial;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.cells.cellular.algorithms.RCCSA;
import com.oat.domains.cells.cellular.problems.AEP_1;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
import com.oat.domains.cells.probes.AbsoluteCellError;
import com.oat.domains.cells.probes.AverageBMUPerAntigen;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.AverageCellDiversityRatio;
import com.oat.domains.cells.probes.AverageCellError;
import com.oat.domains.cells.probes.ResponseError;
import com.oat.domains.cells.spatial.algorithms.SCCSAComponentsAggregation;
import com.oat.domains.cells.spatial.algorithms.SpatialCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.spatial.algorithms.comparison.SCCSA_HR;
import com.oat.domains.cells.spatial.algorithms.comparison.SCCSA_NR;
import com.oat.domains.cells.spatial.algorithms.components.SCCSA_BottomUp;
import com.oat.domains.cells.spatial.algorithms.components.SCCSA_Component;
import com.oat.domains.cells.spatial.algorithms.components.SCCSA_TopDown;
import com.oat.domains.cells.spatial.gui.SpatialMasterPanel;
import com.oat.domains.cells.spatial.probes.AverageCellNeighbourhoodDiversity;
import com.oat.domains.cells.spatial.probes.AverageSubCellNeighbourhoodDiversity;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 01/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SpatialDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new SpatialMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Cellular Spatial";
	}

	@Override
	public String getShortName()
	{
		return "spatial";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return new Algorithm[]
		                     {
				// base
				new RCCSA(100, 2, 5),
				new SpatialCellularClonalSelectionAlgorithm(),
				// experiments
				new SCCSA_HR(),
				new SCCSA_NR(),
				
				// spatial
				new SCCSAComponentsAggregation(),
				new SCCSA_TopDown(),
				new SCCSA_Component(),
				new SCCSA_BottomUp(),
				
		                     };
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
		return new Problem[]
		                   {
				// base
				new Antigen(),
				new AntigenColourSpaceProblem(),
				// for experiments
				new AEP_1(),
				new AEP_10()
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
		// spatial probes
		list.add(new AverageCellNeighbourhoodDiversity());
		list.add(new AverageSubCellNeighbourhoodDiversity());
		
		Collections.sort(list);		
		return list;
	}
}
