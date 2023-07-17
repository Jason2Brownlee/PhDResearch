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
package com.oat.domains.tissues.inflammation;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.tissues.homing.algorithms.HomingTissueClonalSelectionAlgorithm;
import com.oat.domains.tissues.inflammation.algorithms.InflammationTissueClonalSelectionAlgorithm;
import com.oat.domains.tissues.inflammation.gui.InflammationMasterPanel;
import com.oat.domains.tissues.probes.AverageTissueDiversity;
import com.oat.domains.tissues.probes.AverageTissueError;
import com.oat.domains.tissues.probes.HostDiversity;
import com.oat.domains.tissues.probes.HostError;
import com.oat.domains.tissues.probes.TissueAverageCellDiversity;
import com.oat.domains.tissues.recirulation.algorithms.MinimalTissueClonalSelectionAlgorithm;
import com.oat.domains.tissues.recirulation.algorithms.RecirculationTissueClonalSelectionAlgorithm;
import com.oat.domains.tissues.recirulation.problems.ICSP_1;
import com.oat.domains.tissues.recirulation.problems.ICSP_10;
import com.oat.domains.tissues.recirulation.problems.InfectionColourSpaceProblem;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 08/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class InflammationDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{		
		return new InflammationMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Tissues Inflammation";
	}

	@Override
	public String getShortName()
	{
		return "inflammation";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return new Algorithm[]		                  
		                     {
				// base
				new MinimalTissueClonalSelectionAlgorithm(),
				new RecirculationTissueClonalSelectionAlgorithm(),
				new HomingTissueClonalSelectionAlgorithm(),	
				new InflammationTissueClonalSelectionAlgorithm(),
		                     };
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
		return new Problem[]
		                   {
				new InfectionColourSpaceProblem(),
				new ICSP_10(),
				new ICSP_1(),
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
				
		// tissue probes
		list.add(new HostError());
		list.add(new HostDiversity());
		list.add(new AverageTissueError());
		list.add(new AverageTissueDiversity());
		list.add(new TissueAverageCellDiversity());
		
		Collections.sort(list);		
		return list;
	}
}
