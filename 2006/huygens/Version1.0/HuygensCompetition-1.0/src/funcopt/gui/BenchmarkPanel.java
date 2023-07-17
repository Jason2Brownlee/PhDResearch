
package funcopt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Type: BenchmarkPanel<br/>
 * Date: 29/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BenchmarkPanel extends JPanel implements ActionListener
{
    protected JProgressBar testProgress;
    protected JProgressBar moonProgress;
    protected JTable table;
    protected JButton exportButton;
    
    public BenchmarkPanel()        
    {
        prepareGUI();
    }     
    
    public void addBlankRow(int moon)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int totalIterations = model.getColumnCount();
        String [] rowData = new String[totalIterations];
        rowData[0] = "Moon " + (moon+1);
        for (int i = 1; i < rowData.length; i++)
        {
            rowData[i] = "";
        }
        model.addRow(rowData);
    }
    
    public void update(double score, int moonNum, int testNum)
    {
        // update progress
        if(testProgress.getMaximum() == (testNum+1))
        {
            if(moonProgress.getMaximum() != (moonNum+1))
            {
                testProgress.setValue(0);
            }
            else
            {
                testProgress.setValue(testProgress.getValue() + 1);
            }
            moonProgress.setValue(moonProgress.getValue() + 1);            
        }
        else
        {
            testProgress.setValue(testProgress.getValue() + 1);            
        }            
        
        table.setValueAt(Double.toString(score), moonNum, testNum+1);
    }
    
    
    public void prepareSamples(int moons, int iterations)
    {        
        testProgress.setMaximum(iterations);
        testProgress.setMinimum(0);
        testProgress.setValue(0);
        
        moonProgress.setMaximum(moons);
        moonProgress.setMinimum(0);
        moonProgress.setValue(0);
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addColumn("Moons");
        for (int i = 0; i < iterations; i++)
        {
            model.addColumn("Test " + (i+1));
        }
        for (int i = 0; i < moons; i++)
        {
            addBlankRow(i);
        }
    }
    
    public void clearSamples()    
    {
        exportButton.setEnabled(false);
       
        testProgress.setMaximum(0);
        testProgress.setMinimum(0);
        testProgress.setValue(0);
        
        moonProgress.setMaximum(0);
        moonProgress.setMinimum(0);
        moonProgress.setValue(0);
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();        
        model.setColumnCount(0);
        model.setRowCount(0);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == exportButton)
        {
            String data = dataToString();
            JFileChooser fc = new JFileChooser(new File("results.csv"));
            fc.showSaveDialog(this);
            File selFile = fc.getSelectedFile();
            if(selFile!=null)
            {
                try
                {
                    writeToFile(data, selFile.toString());
                }
                catch (Exception e1)
                {
                    JOptionPane.showMessageDialog(this, "Unable to save results.\n"+e1.getMessage());
                }
            }
        }            
    }
    
    public static void writeToFile(String data, String fileout) throws Exception
    {
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(fileout);
            writer.write(data);
            writer.flush();
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
    
    protected String dataToString()
    {
        int cols = table.getModel().getColumnCount();
        int rows = table.getModel().getRowCount();
        StringBuffer buf = new StringBuffer(1024);
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                buf.append(table.getValueAt(i, j));
                if(j!=cols-1)
                {
                    buf.append(",");
                }
            }
            buf.append("\n");
        }
        return buf.toString();
    }

    protected JPanel getControlPanel()
    {
        JPanel p = new JPanel();
        exportButton = new JButton("Export");
        exportButton.addActionListener(this);
        exportButton.setEnabled(false);
        p.add(exportButton);
        
        return p;
    }
    
    protected JPanel getProgressPanel()
    {
        // labels
        JLabel testLabel = new JLabel("Test Progress:");
        JLabel moonLabel = new JLabel("Moon Progress:");
        
        // fields
        testProgress = new JProgressBar();
        testProgress.setValue(0);
        testProgress.setStringPainted(true);
        //testProgress.setPreferredSize(new Dimension(50, 10));
        
        moonProgress = new JProgressBar();
        moonProgress.setValue(0);
        moonProgress.setStringPainted(true);
        //moonProgress.setPreferredSize(new Dimension(50, 10));
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(testLabel);
        labelPane.add(moonLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(testProgress);
        fieldPane.add(moonProgress);

        // Put the panels in another panel, labels on left,
        // text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.WEST);
        contentPane.add(fieldPane, BorderLayout.CENTER);
        
        return contentPane;
    }
    
    protected DefaultTableModel getTableModel(int rows, int cols)
    {
        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        return model; 
    }
    
    
    protected void prepareGUI()
    {            
        table = new JTable(getTableModel(0,0));
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(50,50));
        
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Benchmark Panel"));
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        add(getProgressPanel(), BorderLayout.SOUTH);
        add(getControlPanel(), BorderLayout.NORTH);
    }
    
    public void finished()
    {
        exportButton.setEnabled(true);
    }
}
