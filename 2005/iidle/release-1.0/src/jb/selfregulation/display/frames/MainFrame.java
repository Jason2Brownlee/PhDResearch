package jb.selfregulation.display.frames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.display.control.MasterControlPanel;
import jb.selfregulation.display.control.SimulationControlPanel;

/**
 * Type: MainFrame<br/>
 * Date: 21/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MainFrame extends JFrame
{       
    public MainFrame(IIDLEMainFrame aPanel)
    {
        getContentPane().add(aPanel, BorderLayout.CENTER);
        setSize(800, 600);
        setTitle(aPanel.getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
    
    /**
     * centre the screen
     *
     */
    public void centerScreen()
    {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        setVisible(true);
        requestFocus();
    }
    public void makeVisible()
    {
        Runnable run = new Runnable()
        {
            public void run()
            {
                centerScreen();
            }
        };
        SwingUtilities.invokeLater(run);
    }
}
