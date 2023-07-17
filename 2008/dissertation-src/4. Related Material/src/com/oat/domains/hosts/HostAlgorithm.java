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
package com.oat.domains.hosts;


/**
 * Description: Population of hosts
 *  
 * Date: 10/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public interface HostAlgorithm
{
	/**
	 * Add an exposure listener
	 * @param aListener
	 */
	void registerExposureListener(HabitatExposureListener aListener);
	
	/**
	 * Remove an exposure listener
	 * @param aListener
	 * @return
	 */
	boolean removeExposureListener(HabitatExposureListener aListener);
	
	/**
	 * The number of hosts in the system
	 * @return
	 */
	int getNumHosts();
	
	/**
	 * Access all hosts in the system
	 * @return
	 */
	Host [] getHosts(); 
}
