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
package com.oat.domains.cells.degenerate.gui;

import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.cells.cellular.gui.AntigenSolutionPlot;
import com.oat.explorer.gui.panels.MasterPanel;

/**
 * Description: 
 *  
 * Date: 04/02/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DegenerateMasterPanel extends MasterPanel
{	
	protected AntigenSolutionPlot solutionPlot;
	
	
    public DegenerateMasterPanel(Domain domain)
	{
		super(domain);
	}

	@Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
		solutionPlot = new AntigenSolutionPlot();	
		
		return new JPanel[]{solutionPlot};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // solution plot
        problemPanel.registerProblemChangedListener(solutionPlot); // problem changes
        controlPanel.registerClearableListener(solutionPlot); //clearing
        algorithmPanel.registerAlgorithmIterationCompleteListener(solutionPlot); // algorithm epochs
        
    }    
}
