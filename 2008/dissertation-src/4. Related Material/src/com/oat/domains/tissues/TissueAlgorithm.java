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

import com.oat.domains.cells.cellular.DiscreteCell;

/**
 * Description: 
 *  
 * Date: 01/02/2008<br/>
 * @author Jason Brownlee
 * @param <C> 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public interface TissueAlgorithm<C extends DiscreteCell>
{
	/**
	 * Add an exposure listener
	 * @param aListener
	 */
	void registerExposureListener(InfectionExposureListener aListener);
	
	/**
	 * Remove an exposure listener
	 * @param aListener
	 * @return
	 */
	boolean removeExposureListener(InfectionExposureListener aListener);
	
	/**
	 * Number of tissues in the system
	 * @return
	 */
	int getNumTissues();
	
	/**
	 * Access all tissues
	 * @return
	 */
	Tissue [] getTissues(); 
}
