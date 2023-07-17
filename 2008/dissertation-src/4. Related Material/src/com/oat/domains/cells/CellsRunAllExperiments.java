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
package com.oat.domains.cells;

import com.oat.domains.cells.cellular.experiments.CellularStudy0;
import com.oat.domains.cells.cellular.experiments.CellularStudy1;
import com.oat.domains.cells.cellular.experiments.ReplacementStudy2;
import com.oat.domains.cells.degenerate.experiments.DegenerateStudy1;
import com.oat.domains.cells.mediated.experiment.MediatedStudy0;
import com.oat.domains.cells.mediated.experiment.MediatedStudy1;
import com.oat.domains.cells.network.experiments.NetworkStudy1;
import com.oat.domains.cells.network.experiments.NetworkStudy2;
import com.oat.domains.cells.network.experiments.NetworkStudy3;
import com.oat.domains.cells.spatial.experiments.SpatialStudy1;
import com.oat.experimenter.TemplateExperimentRunner;

/**
 * Description: Execute all known experiments for the cellular domain 
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
public class CellsRunAllExperiments extends TemplateExperimentRunner
{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		new CellsRunAllExperiments().executeExperiments();
	}

	@Override
	public void loadExperimentList()
	{
		// cellular level
		experiments.add(new CellularStudy0());
		experiments.add(new CellularStudy1());
		// replacement
		experiments.add(new ReplacementStudy2());
		// degenerate level
		experiments.add(new DegenerateStudy1());
		//experiments.add(new DegenerateStudy2()); // degenerate substring
		// spatial level
		experiments.add(new SpatialStudy1());
		//experiments.add(new SpatialStudy2()); // degenerate spatial
		// mediated level
		experiments.add(new MediatedStudy0());
		experiments.add(new MediatedStudy1());
		//experiments.add(new MediatedStudy2()); // degenerate mediated		
		// network level
		experiments.add(new NetworkStudy1());
		experiments.add(new NetworkStudy2());
		//experiments.add(new NetworkStudy3());	// proxy responses
	}

}
