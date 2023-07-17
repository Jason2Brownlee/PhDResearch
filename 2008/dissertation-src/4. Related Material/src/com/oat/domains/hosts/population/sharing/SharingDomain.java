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
package com.oat.domains.hosts.population.sharing;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.hosts.population.sharing.algorithms.SharedImmunityHostClonalSelectionAlgorithm;
import com.oat.domains.hosts.population.sharing.gui.SharingMasterPanel;
import com.oat.domains.hosts.population.transmission.algorithms.MinimalPopulationHostClonalSelectionAlgorithm;
import com.oat.domains.hosts.population.transmission.problems.HCSP_1;
import com.oat.domains.hosts.population.transmission.problems.HCSP_10;
import com.oat.domains.hosts.population.transmission.problems.HabitatColourSpaceProblem;
import com.oat.domains.hosts.probes.AverageHostDiversity;
import com.oat.domains.hosts.probes.AverageHostError;
import com.oat.domains.hosts.probes.HostsAverageCellDiversity;
import com.oat.domains.hosts.probes.PopulationDiversity;
import com.oat.domains.hosts.probes.PopulationError;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: 
 *  
 * Date: 11/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SharingDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new SharingMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Hosts Shared Immunity";
	}

	@Override
	public String getShortName()
	{
		return "shared immunity";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return new Algorithm[]		                  
		                     {
			 new MinimalPopulationHostClonalSelectionAlgorithm(),
		     new SharedImmunityHostClonalSelectionAlgorithm(),

		                     };
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
		return new Problem[]
		                   {
				new HabitatColourSpaceProblem(),
				new HCSP_10(),
				new HCSP_1()					
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
				
		// host probes
		list.add(new PopulationDiversity());
		list.add(new PopulationError());
		list.add(new AverageHostDiversity());
		list.add(new AverageHostError());
		list.add(new HostsAverageCellDiversity());
		
		Collections.sort(list);		
		return list;
	}
}
