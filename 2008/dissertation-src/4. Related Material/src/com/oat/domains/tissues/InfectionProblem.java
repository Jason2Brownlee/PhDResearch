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
package com.oat.domains.tissues;


/**
 * Description: 
 *  
 * Date: 26/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public interface InfectionProblem 
{		
	
	/**
	 * Number of infections in this thing
	 * @return
	 */
	int getNumInfections();
	

	/**
	 * Access to the infections in this problem
	 * @return
	 */
	Infection [] getInfections();
	
	/**
	 * Returns the specific infection number
	 * @param infectionNumber
	 * @return
	 */
	Infection getInfection(int infectionNumber);
}