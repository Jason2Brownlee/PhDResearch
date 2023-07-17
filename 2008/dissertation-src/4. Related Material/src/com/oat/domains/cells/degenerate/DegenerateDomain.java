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
package com.oat.domains.cells.degenerate;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.cells.degenerate.algorithms.component.DCCCSA_BottomUp;
import com.oat.domains.cells.degenerate.algorithms.component.DCCCSA_Explicit;
import com.oat.domains.cells.degenerate.algorithms.component.DCCCSA_PreCommitted;
import com.oat.domains.cells.degenerate.algorithms.substring.DSCCSA_Deterministic;
import com.oat.domains.cells.degenerate.algorithms.substring.DSCCSA_Probabilistic;
import com.oat.domains.cells.degenerate.gui.DegenerateMasterPanel;
import com.oat.domains.cells.degenerate.probes.AverageBitsDegenerateSubstring;
import com.oat.domains.cells.degenerate.probes.AveragePolyclonalResponseError;
import com.oat.domains.cells.degenerate.probes.AveragePolyclonalResponseFootprintSize;
import com.oat.domains.cells.degenerate.problems.DAEP_1;
import com.oat.domains.cells.degenerate.problems.DAEP_10;
import com.oat.domains.cells.degenerate.problems.DAEP_2;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigen;
import com.oat.domains.cells.degenerate.problems.DegenerateAntigenColourSpaceProblem;
import com.oat.domains.cells.probes.AbsoluteCellError;
import com.oat.domains.cells.probes.AverageCellDiversity;
import com.oat.domains.cells.probes.AverageCellDiversityRatio;
import com.oat.domains.cells.probes.AverageCellError;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Description: Degenerate Domain 
 *  
 * Date: 04/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DegenerateDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new DegenerateMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Cellular Degenerate";
	}

	@Override
	public String getShortName()
	{
		return "degenerate";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return new Algorithm[]		                  
		                     {
				// component
				new DCCCSA_Explicit(),
				new DCCCSA_PreCommitted(),
				new DCCCSA_BottomUp(),
				
				new DSCCSA_Probabilistic(),
				new DSCCSA_Deterministic(),
		                     };
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
		return new Problem[]
		                   {
				new DegenerateAntigenColourSpaceProblem(),
				new DegenerateAntigen(),
				new DAEP_1(),
				new DAEP_2(),
				new DAEP_10(),
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
		
		// degenerate
		list.add(new AverageBitsDegenerateSubstring());
		list.add(new AveragePolyclonalResponseError());
		list.add(new AveragePolyclonalResponseFootprintSize());
		
		Collections.sort(list);		
		return list;
	}
}
