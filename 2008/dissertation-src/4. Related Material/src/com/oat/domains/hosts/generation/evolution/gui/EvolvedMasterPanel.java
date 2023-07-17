/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006  Jason Brownlee

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.domains.hosts.generation.evolution.gui;

import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.cells.cellular.gui.AntigenSolutionPlot;
import com.oat.domains.hosts.population.transmission.gui.HostExposurePlot;
import com.oat.domains.hosts.population.transmission.gui.HostPlot;
import com.oat.explorer.gui.panels.MasterPanel;

/**
 * Description: 
 *  
 * Date: 15/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class EvolvedMasterPanel extends MasterPanel
{	
	protected AntigenSolutionPlot solutionPlot;
	protected HostPlot hostPlot;
	protected HostExposurePlot exposurePlot;
	
    public EvolvedMasterPanel(Domain domain)
	{
		super(domain);
	}

	@Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
		solutionPlot = new AntigenSolutionPlot();		
		hostPlot = new HostPlot();
		exposurePlot = new HostExposurePlot();
		
		return new JPanel[]{solutionPlot, hostPlot, exposurePlot};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // solutionPlot
        problemPanel.registerProblemChangedListener(solutionPlot); // problem changes
        controlPanel.registerClearableListener(solutionPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(solutionPlot); // algorithm epochs
        
        // tissue plot
        problemPanel.registerProblemChangedListener(hostPlot); // problem changes
        controlPanel.registerClearableListener(hostPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(hostPlot); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(hostPlot);
        
        // exposure plot
        problemPanel.registerProblemChangedListener(exposurePlot); // problem changes
        controlPanel.registerClearableListener(exposurePlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(exposurePlot); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(exposurePlot);        
    }   
}
