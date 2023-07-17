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
package com.oat.domains.cells.spatial.gui;

import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.cells.cellular.gui.AntigenSolutionPlot;
import com.oat.domains.cells.cellular.gui.CellsPlot;
import com.oat.explorer.gui.panels.MasterPanel;


/**
 * Description: 
 *  
 * Date: 01/11/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SpatialMasterPanel extends MasterPanel
{
	protected AntigenSolutionPlot solutionPlot;
    protected SpatialPlot spatialPlot;
    protected CellsPlot cellPlot;
    protected SpatialSubCellPlot spatialSubCell;


    public SpatialMasterPanel(Domain domain)
	{
		super(domain);
	}

	@Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
		solutionPlot = new AntigenSolutionPlot();
		spatialPlot = new SpatialPlot();
		cellPlot = new CellsPlot();
		spatialSubCell = new SpatialSubCellPlot();
		
        return new JPanel[]{solutionPlot, cellPlot, spatialPlot, spatialSubCell};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // solution plot
        problemPanel.registerProblemChangedListener(solutionPlot); // problem changes
        controlPanel.registerClearableListener(solutionPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(solutionPlot); // algorithm epochs
        
        // spatial repertoire plot
        problemPanel.registerProblemChangedListener(cellPlot); // problem changes
        controlPanel.registerClearableListener(cellPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(cellPlot); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(cellPlot); // algorithm changes
        
        // spatial repertoire plot
        problemPanel.registerProblemChangedListener(spatialPlot); // problem changes
        controlPanel.registerClearableListener(spatialPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(spatialPlot); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(spatialPlot); // algorithm changes
        
        // spatial repertoire plot
        problemPanel.registerProblemChangedListener(spatialSubCell); // problem changes
        controlPanel.registerClearableListener(spatialSubCell); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(spatialSubCell); // algorithm epochs
        algorithmPanel.registerAlgorithmChangedListener(spatialSubCell); // algorithm changes
    }        
}
