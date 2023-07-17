/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2008  Jason Brownlee

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
package com.oat.domains;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.Domain;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.cells.probes.AntigenAverageDiversity;
import com.oat.domains.cells.probes.CellSetAbsoluteEuclideanError;
import com.oat.domains.cells.probes.CellSetAbsoluteHammingError;
import com.oat.domains.cells.probes.CellSetAverageComponentEuclideanError;
import com.oat.domains.cells.probes.CellSetAverageDiversity;
import com.oat.domains.cells.probes.CellSetAverageEuclideanError;
import com.oat.domains.cells.probes.CellSetAverageHammingError;
import com.oat.domains.cells.probes.ResponseError;
import com.oat.stopcondition.FoundOptima;
import com.oat.stopcondition.FoundOptimaOrMaxEpochs;

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
public abstract class ClonalSelectionDomain extends Domain
{
	@Override
	public LinkedList<StopCondition> loadDomainStopConditions()
	{
		LinkedList<StopCondition> list = super.loadDomainStopConditions();
		list.add(new FoundOptima());		
		list.add(new FoundOptimaOrMaxEpochs());
		Collections.sort(list);		
		return list;
	}
	
	@Override
	public LinkedList<RunProbe> loadDomainRunProbes()
	{
		LinkedList<RunProbe> list = super.loadDomainRunProbes();		
		
		// problem probes
		list.add(new AntigenAverageDiversity());
		// cell set (solution) based probes
		list.add(new CellSetAverageHammingError());
		list.add(new CellSetAverageEuclideanError());
		list.add(new CellSetAbsoluteEuclideanError());
		list.add(new CellSetAbsoluteHammingError());
		list.add(new CellSetAverageDiversity());		
		list.add(new CellSetAverageComponentEuclideanError());
		// generic response error
		list.add(new ResponseError());
		
		Collections.sort(list);		
		return list;
	}
}
