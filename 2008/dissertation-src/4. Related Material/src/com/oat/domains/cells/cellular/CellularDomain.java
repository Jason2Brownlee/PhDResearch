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
package com.oat.domains.cells.cellular;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.domains.ClonalSelectionDomain;
import com.oat.domains.cells.cellular.algorithms.CellularClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.algorithms.ReplacementClonalSelectionAlgorithm;
import com.oat.domains.cells.cellular.algorithms.relationships1.AEP1_CCSA1_1;
import com.oat.domains.cells.cellular.algorithms.relationships1.AEP1_CCSA1_N;
import com.oat.domains.cells.cellular.algorithms.relationships1.AEP1_CCSAN_1;
import com.oat.domains.cells.cellular.algorithms.relationships1.AEP1_CCSAN_N;
import com.oat.domains.cells.cellular.algorithms.relationships2.CCSA1_1;
import com.oat.domains.cells.cellular.algorithms.relationships2.CCSA1_N;
import com.oat.domains.cells.cellular.algorithms.relationships2.CCSAN_1;
import com.oat.domains.cells.cellular.algorithms.relationships2.CCSAN_N;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANN;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANNGrouped;
import com.oat.domains.cells.cellular.algorithms.replacement.CCSANNSample;
import com.oat.domains.cells.cellular.algorithms.replacement.RCCSA_H;
import com.oat.domains.cells.cellular.algorithms.replacement.RCCSA_H_EP;
import com.oat.domains.cells.cellular.gui.CellularMasterPanel;
import com.oat.domains.cells.cellular.problems.AEP_1;
import com.oat.domains.cells.cellular.problems.AEP_10;
import com.oat.domains.cells.cellular.problems.Antigen;
import com.oat.domains.cells.cellular.problems.AntigenColourSpaceProblem;
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
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CellularDomain extends ClonalSelectionDomain
{
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new CellularMasterPanel(this);
	}

	@Override
	public String getHumanReadableName()
	{
		return "Cellular";
	}

	@Override
	public String getShortName()
	{
		return "cellular";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return new Algorithm[]		                  
		                     {
				// standard
				new CellularClonalSelectionAlgorithm(),
				new ReplacementClonalSelectionAlgorithm(),
				
				// AEP1				
				new AEP1_CCSA1_1(),
				new AEP1_CCSA1_N(),
				new AEP1_CCSAN_1(),
				new AEP1_CCSAN_N(),			
				
				// AEP10				
				new CCSA1_1(),
				new CCSA1_N(),
				new CCSAN_N(),
				new CCSAN_1(),
				
				// replacement
				new CCSANN(),
				new RCCSA_H_EP(),
				new RCCSA_H(),
				new CCSANNGrouped(),
				new CCSANNSample()
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
		//return new AntigenProblemPlot();
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
		
		Collections.sort(list);		
		return list;
	}
}
