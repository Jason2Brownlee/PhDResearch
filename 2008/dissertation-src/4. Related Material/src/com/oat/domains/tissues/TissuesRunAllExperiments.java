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
package com.oat.domains.tissues;

import com.oat.domains.tissues.homing.experiments.HomingStudy1;
import com.oat.domains.tissues.inflammation.experiments.InflammationStudy1;
import com.oat.domains.tissues.recirulation.experiments.RecirulationStudy1;
import com.oat.experimenter.TemplateExperimentRunner;

/**
 * Description: Execute all known experiments for the tissue domain 
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
public class TissuesRunAllExperiments extends TemplateExperimentRunner
{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		new TissuesRunAllExperiments().executeExperiments();
	}

	@Override
	public void loadExperimentList()
	{
		// recirulation
		experiments.add(new RecirulationStudy1());
		// homing level
		experiments.add(new HomingStudy1());
		// inflammation level
		experiments.add(new InflammationStudy1());		
	}
}
