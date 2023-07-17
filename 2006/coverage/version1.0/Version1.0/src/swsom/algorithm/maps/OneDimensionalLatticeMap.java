package swsom.algorithm.maps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swsom.algorithm.Edge;
import swsom.algorithm.ExemplarVector;
import swsom.algorithm.MathUtils;
import swsom.algorithm.SOMMap;
import swsom.algorithm.VectorConnection;

/**
 * Type: OneDimensionalLatticeMap<br/>
 * Date: 22/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class OneDimensionalLatticeMap extends SOMMap
{
    // map elements    
    protected VectorConnection[] connections;

    // configuration
    protected int numVerticies; // n
    protected int kNearestNeighbours; // k (lattice connectivity)
    protected double rewireProbability; // rp (re-wiring)
    protected double shortcutProbability; // sp (short cuts)
    protected ExemplarVector[] vectors;
    
    
    public OneDimensionalLatticeMap()
    {}

    /**
     * 
     * @param aSample
     * @param rand
     * @param aProblem
     */
    protected void prepareConnections(double[][] aSample, Random rand)
    {
        // create verticies
        for (int i = 0; i < vectors.length; i++)
        {
            // select an index
            int index = rand.nextInt(aSample.length);
            double[] data = new double[aSample[index].length];
            System.arraycopy(aSample[index], 0, data, 0, data.length);
            vectors[i] = new ExemplarVector(i, data);
        }
        // construct a k-connected lattice - lazy I know
        LinkedList<VectorConnection> c = new LinkedList<VectorConnection>();
        for (int offset = 1; offset <= kNearestNeighbours; offset++)
        {
            for (int j = 0; j < vectors.length; j++)
            {
                ExemplarVector current = vectors[j];
                ExemplarVector other = vectors[getRightNeighbourIndex(j, offset)];
                VectorConnection edge = new VectorConnection(current, other);
                // check if it can be added to the first
                if (!current.containsEdge(edge))
                {
                    // add to both
                    current.addConnection(edge);
                    other.addConnection(edge);
                    // add to collection
                    c.add(edge);
                }
            }
        }
        connections = c.toArray(new VectorConnection[c.size()]);
        // rewire if necessary
        if (kNearestNeighbours> 0 && rewireProbability > 0)
        {
            // process each connection and rewire with the probability p
            // connections to self and double connections are not allowed
            for (int i = 0; i < connections.length; i++)
            {
                VectorConnection current = connections[i];

                if (canRewire(rand))
                {
                    // remove the connection
                    ((ExemplarVector) current.getVertex1()).deleteEdge(current);
                    ((ExemplarVector) current.getVertex2()).deleteEdge(current);
                    ExemplarVector selected = selectRandomNode(rand);

                    // select an vertex to rewire
                    if (rand.nextBoolean()) // node 1
                    {
                        // check for self connection
                        if (selected != current.getVertex1())
                        {
                            VectorConnection newEdge = makeRewiredConnection((ExemplarVector) current.getVertex1(), selected);
                            // check for double connection
                            if (!doesConnectionExist(newEdge))
                            {
                                // add the connection
                                connections[i] = newEdge;
                            }
                        }
                    }
                    else
                    // node 2
                    {
                        // check for self connection
                        if (selected != current.getVertex2())
                        {
                            VectorConnection newEdge = makeRewiredConnection((ExemplarVector) current.getVertex2(), selected);
                            // check for double connection
                            if (!doesConnectionExist(newEdge))
                            {
                                // add the connection
                                connections[i] = newEdge;
                            }
                        }
                    }
                }
            }
        }
        // add short-cut connections if possible
        if (kNearestNeighbours>0 && shortcutProbability > 0)
        {
            // simply add additional connections with probability p
            LinkedList<VectorConnection> newConnections = new LinkedList<VectorConnection>();
            for (int i = 0; i < vectors.length; i++)
            {
                ExemplarVector current = vectors[i];

                if (canMakeConnection(rand))
                {
                    // remove the connection
                    ExemplarVector selected = selectRandomNode(rand);

                    // check for self connection
                    if (selected != current)
                    {
                        VectorConnection newEdge = makeNewConnection(current, selected);
                        // check for double connection
                        if (!doesConnectionExist(newEdge))
                        {
                            // add the connection
                            newConnections.add(newEdge);
                        }
                    }
                }
            }
            if (!newConnections.isEmpty())
            {
                // add all old connections
                for (int i = 0; i < connections.length; i++)
                {
                    newConnections.add(connections[i]);
                }
                // we have new connections
                connections = newConnections.toArray(new VectorConnection[newConnections.size()]);
            }
        }
    }

    protected int getRightNeighbourIndex(int currentIndex, int offset)
    {
        int calculated = currentIndex + offset;
        if (calculated >= (vectors.length))
        {
            // back to zero
            calculated -= vectors.length;
        }
        return calculated;
    }

    @Override
    public ExemplarVector getBestMatchingUnit(double[] v)
    {
        double best = MathUtils.distanceEuclidean(v, vectors[0].getData());
        ExemplarVector bestVector = vectors[0];
        for (int i = 1; i < vectors.length; i++)
        {
            double d = MathUtils.distanceEuclidean(v, vectors[i].getData());
            if (d < best)
            {
                best = d;
                bestVector = vectors[i];
            }
        }

        return bestVector;
    }

    /**
     * (non-Javadoc)
     * 
     * @see swsom.algorithm.FeatureMap#getVectors()
     */
    public ExemplarVector[] getVectors()
    {
        return vectors;
    }

    /**
     * (non-Javadoc)
     * 
     * @see swsom.algorithm.FeatureMap#getConnections()
     */
    public VectorConnection[] getConnections()
    {
        return connections;
    }

    protected VectorConnection makeRewiredConnection(ExemplarVector self, ExemplarVector other)
    {
        VectorConnection edge = new VectorConnection(self, other);
        self.addConnection(edge);
        other.addConnection(edge);
        return edge;
    }

    protected ExemplarVector selectRandomNode(Random r)
    {
        int index = r.nextInt(vectors.length);
        return vectors[index];
    }

    protected boolean canRewire(Random r)
    {
        if (r.nextDouble() <= rewireProbability)
        {
            return true;
        }

        return false;
    }

    protected boolean doesConnectionExist(Edge edge)
    {
        for (int i = 0; i < connections.length; i++)
        {
            if (edge.equals(connections[i]))
            {
                return true;
            }
        }

        return false;
    }

    protected VectorConnection makeNewConnection(ExemplarVector self, ExemplarVector other)
    {
        VectorConnection edge = new VectorConnection(self, other);
        self.addConnection(edge);
        other.addConnection(edge);
        return edge;
    }

    protected boolean canMakeConnection(Random r)
    {
        if (r.nextDouble() <= shortcutProbability)
        {
            return true;
        }

        return false;
    }
    
    @Override
    public String toString()
    {
        return "1D Lattice";
    }
    
    
    @Override
    public void initialise(Random r, double [][] sample)
    {
        // numVerticies
        try
        {
            numVerticies = Integer.parseInt(verticiesField.getText());
        }
        catch (Exception e)
        {
            numVerticies = 100;
            verticiesField.setText("" + numVerticies);
        }
        finally
        {
            if(numVerticies<=0)
            {
                numVerticies = 100;
                verticiesField.setText("" + numVerticies);
            }
        }
        // kNearestNeighbours
        try
        {
            kNearestNeighbours = Integer.parseInt(neighboursField.getText());
        }
        catch (Exception e)
        {
            kNearestNeighbours = 3;
            neighboursField.setText("" + kNearestNeighbours);
        }
        finally
        {
            if(kNearestNeighbours<0||kNearestNeighbours>=numVerticies)
            {
                kNearestNeighbours = 3;
                neighboursField.setText("" + kNearestNeighbours);
            }
        }
        // rewireProbability
        try
        {
            rewireProbability = Double.parseDouble(rewireProbField.getText());
        }
        catch (Exception e)
        {
            rewireProbability = 0;
            rewireProbField.setText("" + rewireProbability);
        }
        finally
        {
            if(rewireProbability<0||rewireProbability>1)
            {
                rewireProbability = 0;
                rewireProbField.setText("" + rewireProbability);
            }
        }
        // rewireProbability
        try
        {
            shortcutProbability = Double.parseDouble(shortcutProbField.getText());
        }
        catch (Exception e)
        {
            shortcutProbability = 0;
            shortcutProbField.setText("" + shortcutProbability);
        }
        finally
        {
            if(shortcutProbability<0||shortcutProbability>1)
            {
                shortcutProbability = 0;
                shortcutProbField.setText("" + shortcutProbability);
            }
        }
       
        vectors = new ExemplarVector[numVerticies];
        connections = null;
        prepareConnections(sample, r);
    }    
    
    protected JTextField verticiesField;
    protected JTextField neighboursField;
    protected JTextField rewireProbField;
    protected JTextField shortcutProbField;
    
    @Override
    public JPanel getConfigurationPane()
    {
        // defaults
        numVerticies = 100;
        kNearestNeighbours = 3;
        rewireProbability = 0.0;
        shortcutProbability = 0.0;
        
        // labels
        JLabel verticiesLabel = new JLabel("Total verticies:");
        JLabel neighboursLabel = new JLabel("Neighbour connections:");
        JLabel rewireProbLabel = new JLabel("Rewire probability:");
        JLabel shortcutProbLabel = new JLabel("Shortcut probability:");
        
        // fields
        verticiesField = new JTextField(Integer.toString(numVerticies), 10);
        neighboursField = new JTextField(Integer.toString(kNearestNeighbours), 10);
        rewireProbField = new JTextField(Double.toString(rewireProbability), 10);
        shortcutProbField = new JTextField(Double.toString(shortcutProbability), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(verticiesLabel);
        labelPane.add(neighboursLabel);
        labelPane.add(rewireProbLabel);
        labelPane.add(shortcutProbLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(verticiesField);
        fieldPane.add(neighboursField);
        fieldPane.add(rewireProbField);
        fieldPane.add(shortcutProbField);

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
        return 4;
    }
}
