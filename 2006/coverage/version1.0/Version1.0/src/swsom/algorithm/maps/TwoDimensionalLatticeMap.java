
package swsom.algorithm.maps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.ExemplarVector;
import swsom.algorithm.MapInitialiser;
import swsom.algorithm.MathUtils;
import swsom.algorithm.SOMMap;
import swsom.algorithm.VectorConnection;

/**
 * Type: TwoDimensionalLatticeMap<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class TwoDimensionalLatticeMap extends SOMMap
{  
    // configuration
    protected int width;
    protected int height;
    protected ExemplarVector [] vectors;    
    protected VectorConnection [] connections;
    
    protected JTextField widthField;
    protected JTextField heightField;
    
    public TwoDimensionalLatticeMap()
    {}
    
    @Override
    public ExemplarVector getBestMatchingUnit(double [] v)
    {        
        double best = MathUtils.distanceEuclidean(v, vectors[0].getData());
        ExemplarVector bestVector = vectors[0];
        for (int i = 1; i < vectors.length; i++)
        {
            double d = MathUtils.distanceEuclidean(v, vectors[i].getData());
            if(d < best)
            {
                best = d;
                bestVector = vectors[i];
            }            
        }
        
        return bestVector;
    }
    
    
    public double [] calculateCoordinate(int index)
    {
        double [] coord = new double[2];        
        coord[0] = index % width; // x
        coord[1] = index / width; // y
        
		return coord;
    }
    
    public int calculateOffset(double x, double y)
    {        
        int offset = (int) (width * y); // number of rows down
        offset += x; // number of columns across
        return offset;
    }
    
    
    public ExemplarVector [] getConnectivity(ExemplarVector aVector)
    {
        LinkedList<ExemplarVector> links = new LinkedList<ExemplarVector>();
        double [] coord = aVector.getCoord();
        
        if(coord[0] > 0)
        {
            int offset = calculateOffset(coord[0]-1, coord[1]);
            links.add(vectors[offset]);
        }
        if(coord[0] < width-1)
        {
            int offset = calculateOffset(coord[0]+1, coord[1]);
            links.add(vectors[offset]);
        }            
        if(coord[1] > 0)
        {
            int offset = calculateOffset(coord[0], coord[1]-1);
            links.add(vectors[offset]);
        }
        if(coord[1] < height-1)
        {
            int offset = calculateOffset(coord[0], coord[1]+1);
            links.add(vectors[offset]);
        }       
        
        return links.toArray(new ExemplarVector[links.size()]);
    }
    
    
	public double calculateNeighbourhoodDistance(
	        ExemplarVector aBmu, 
	        ExemplarVector aVector)	
	{
	    // quick check for self
	    if(aBmu == aVector)
	    {
	        return 0.0;
	    }	    
	    
	    return MathUtils.distanceEuclidean(aBmu.getCoord(), aVector.getCoord());	    
	}
	
	
	
    
    /**
     * @return Returns the vectors.
     */
    public ExemplarVector[] getVectors()
    {
        return vectors;
    }
    
    
    /**
     * @return Returns the height.
     */
    public int getHeight()
    {
        return height;
    }
    /**
     * @return Returns the width.
     */
    public int getWidth()
    {
        return width;
    }
    
    public int getMaxDimension()
    {
        return Math.max(width, height);
    }
    
    
    /**
     * @return Returns the connections.
     */
    public VectorConnection[] getConnections()
    {
        return connections;
    }
    /**
     * @param connections The connections to set.
     */
    public void setConnections(VectorConnection[] connections)
    {
        this.connections = connections;
    }
    
    @Override
    public String toString()
    {
        return "2D Lattice";
    }
    
    
    
    
    @Override
    public void initialise(Random r, double [][] sample)
    {
        // width
        try
        {
            width = Integer.parseInt(widthField.getText());
        }
        catch (Exception e)
        {
            width = 9;
            widthField.setText("" + width);
        }
        finally
        {
            if(width<=0)
            {
                width = 9;
                widthField.setText("" + width);
            }
        }
        // height
        try
        {
            height = Integer.parseInt(heightField.getText());
        }
        catch (Exception e)
        {
            height = 11;
            heightField.setText("" + height);
        }
        finally
        {
            if(height<=0)
            {
                height = 11;
                heightField.setText("" + height);
            }
        }
       
        vectors = new ExemplarVector[width * height];
        MapInitialiser.initialiseMapUniform(this, r, sample);
    }    
    
    @Override
    public JPanel getConfigurationPane()
    {
        // defaults
        width = 9;
        height = 11;
        
        // labels
        JLabel widthLabel = new JLabel("Lattice width:");
        JLabel heightLabel = new JLabel("Lattice height:");
        
        // fields
        widthField = new JTextField(Integer.toString(width), 10);
        heightField = new JTextField(Integer.toString(height), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(widthLabel);
        labelPane.add(heightLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(widthField);
        fieldPane.add(heightField);

        // Put the panels in another panel, labels on left,
        // text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }
    @Override
    public int getNumParameters()
    {
        return 2;
    }
}

