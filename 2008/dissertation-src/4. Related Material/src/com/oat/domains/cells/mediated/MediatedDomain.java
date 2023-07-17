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
package com.oat.domains.cells.mediated;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.cells.cellular.problems.AEP_1;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
import com.oat.domains.cells.mediated.algorithms.MediatedCellularClonalSelectionAlgorithm;
import com.oat.domains.cells.mediated.algorithms.components.MCCSACellCompEuclidean;
import com.oat.domains.cells.mediated.algorithms.components.MCCSACellCompHamming;
import com.oat.domains.cells.mediated.algorithms.components.MCCSACompCellEuclidean;
import com.oat.domains.cells.mediated.algorithms.components.MCCSACompCellHamming;
import com.oat.domains.cells.mediated.algorithms.mapping.MCCSAEuclidean;
import com.oat.domains.cells.mediated.algorithms.mapping.MCCSAHamming;
import com.oat.domains.cells.mediated.algorithms.relationships.MCCSA1_1;
import com.oat.domains.cells.mediated.algorithms.relationships.MCCSA1_N;
import com.oat.domains.cells.mediated.algorithms.relationships.MCCSAN_1;
import com.oat.domains.cells.mediated.algorithms.relationships.MCCSAN_N;
import com.oat.domains.cells.mediated.gui.MediatedMasterPanel;
import com.oat.domains.cells.mediated.probes.AverageBCellBMUPerAntigen;
import com.oat.domains.cells.mediated.probes.AverageBCellDiversity;
import com.oat.domains.cells.mediated.probes.AverageBCellError;
import com.oat.domains.cells.mediated.probes.AverageTCellBMUPerAntigen;
import com.oat.domains.cells.mediated.probes.AverageTCellDiversity;
import com.oat.domains.cells.mediated.probes.AverageTCellError;
import com.oat.domains.cells.mediated.probes.AverageTCellMappingError;
import com.oat.domains.cells.mediated.probes.AverageTCellSelectedSetMappingError;
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
public class MediatedDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new MediatedMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Cellular Mediated";
	}

	@Override
	public String getShortName()
	{
		return "mediated";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		Algorithm [] a = new Algorithm[]
		                     {		
				// base
				new MediatedCellularClonalSelectionAlgorithm(),
				// mapping
				new MCCSAEuclidean(),
				new MCCSAHamming(),
				// relationships
				new MCCSA1_1(),
				new MCCSA1_N(),
				new MCCSAN_1(),
				new MCCSAN_N(),
				// components-to-cell
				new MCCSACompCellEuclidean(),
				new MCCSACompCellHamming(),
				// cell-to-component
				new MCCSACellCompHamming(),
				new MCCSACellCompEuclidean(),
		                     };
		
		Arrays.sort(a);
		return a;
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
		
		list.add(new AverageBCellDiversity());
		list.add(new AverageTCellDiversity());
		list.add(new AverageBCellError());
		list.add(new AverageTCellError());
		list.add(new AverageBCellBMUPerAntigen());
		list.add(new AverageTCellBMUPerAntigen());
		// mappings
		list.add(new AverageTCellMappingError());
		list.add(new AverageTCellSelectedSetMappingError());
		
		Collections.sort(list);		
		return list;
	}
}
