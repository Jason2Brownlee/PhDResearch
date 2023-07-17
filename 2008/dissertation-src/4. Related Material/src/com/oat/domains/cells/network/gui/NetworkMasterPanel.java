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
package com.oat.domains.cells.network.gui;

import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.cells.cellular.gui.AntigenSolutionPlot;
import com.oat.explorer.gui.panels.MasterPanel;

/**
 * Description: 
 *  
 * Date: 13/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class NetworkMasterPanel extends MasterPanel
{	
	protected AntigenSolutionPlot solutionPlot;
	protected MappedRepertoire mappedViewer;
	protected PairView pairView;
	
    public NetworkMasterPanel(Domain domain)
	{
		super(domain);
	}

	@Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
		solutionPlot = new AntigenSolutionPlot();		
		mappedViewer = new MappedRepertoire();
		pairView = new PairView();
		return new JPanel[]{solutionPlot, mappedViewer, pairView};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // solution Plot
        problemPanel.registerProblemChangedListener(solutionPlot); // problem changes
        controlPanel.registerClearableListener(solutionPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(solutionPlot); // algorithm epochs
        
        // mapped repertoire
        problemPanel.registerProblemChangedListener(mappedViewer); // problem changes
        controlPanel.registerClearableListener(mappedViewer); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(mappedViewer); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(mappedViewer);
        
        // pair view
        problemPanel.registerProblemChangedListener(pairView); // problem changes
        controlPanel.registerClearableListener(pairView); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(pairView); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(pairView);
    }    
}
