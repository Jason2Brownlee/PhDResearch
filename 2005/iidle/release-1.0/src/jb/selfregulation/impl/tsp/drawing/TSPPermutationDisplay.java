package jb.selfregulation.impl.tsp.drawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jb.selfregulation.Cell;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;

/**
 * Type: TSPPermutationDisplay<br/>
 * Date: 17/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPPermutationDisplay extends JPanel 
    implements LatticeStatusListener
{
    protected TSPProblem problem;
    protected Lattice lattice;
    protected final Comparator<double[]> comparator;
    
    protected double[][] tourMatrix;
    
    protected int squareWidth;
    protected int squareHeight;
    protected boolean orderByFrequency;

    
    
    public TSPPermutationDisplay()
    {
        comparator = new MyComparable();
        orderByFrequency = true;
        setName("TSP.Permutation");
    }
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        repaint();
    }
    public String getBase()
    {
        return ".tsp.permutation";
    }    
    public void loadConfig(String aBase, Properties prop)
    {}    
    public void setup(SystemState aState)
    {
        problem = (TSPProblem) aState.problem;
        lattice = aState.lattice;
        tourMatrix = new double[problem.getCities().length][problem.getCities().length];
        // add to problem panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_PROBLEM_PANELS)).add(this);
    }
    
    

    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        // anti-alias
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // clear everything
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        // draw the TSP's
        drawTourMatrix(g2d);
    }

    protected void drawTourMatrix(Graphics2D g)
    {
        // prepare data
        prepareTourData();
        // prepare drawing things
        prepareSquareSize();
        // draw data
        drawTourData(g);
    }

    protected void drawTourData(Graphics2D g)
    {
        // check for a reordering
        if (orderByFrequency)
        {
            orderMatrix();
        }
        // go through all cities (rows)
        for (int y = 0, yOffset = 0; y < tourMatrix.length; y++, yOffset += squareHeight)
        {
            // go through all columns in this row
            for (int x = 0, xOffset = 0; x < tourMatrix[y].length; x++, xOffset += squareWidth)
            {
                drawSquare(g, xOffset, yOffset, tourMatrix[y][x]);                
            }
        }
    }

    protected void drawSquare(Graphics2D g, int x, int y, double v)
    {
        float shade = (1.0f - (float) v);
        g.setColor(new Color(1.0f, shade, shade)); // red
        g.fillRect(x, y, squareWidth, squareHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, squareWidth, squareHeight);
    }

    protected void prepareSquareSize()
    {
        double total = tourMatrix.length;
        double width = getWidth();
        double height = getHeight();
        // prepare square size
        squareWidth = (int) Math.floor(width / total);
        squareHeight = (int) Math.floor(height / total);
    }

    protected void orderMatrix()
    {
        LinkedList<double[]> list = new LinkedList<double[]>();
        for (int i = 0; i < tourMatrix.length; i++)
        {
            list.add(tourMatrix[i]);
        }
        // sort
        Collections.sort(list, comparator);
        tourMatrix = list.toArray(new double[list.size()][]);
    }

    protected class MyComparable implements Comparator<double[]>
    {
        /**
         * Compares its two arguments for order. Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.
         * <p>
         * 
         * @param o1
         * @param o2
         * @return
         */
        public int compare(double[] o1, double[] o2)
        {
            double [] v1 = max(o1);
            double [] v2 = max(o2);
            
            if(v1[1] < v2[1])
            {
                return -1;
            }
            else if(v1[1] > v2[1])
            {
                return +1;
            }
            
            // same position
            if(v1[0] < v2[0])
            {
                return -1;
            }
            else if(v1[0] > v2[0])
            {
                return +1;
            }

            return 0; // same
        }

        protected double []  max(double[] d)
        {
            double [] best = new double[2]; 
            best[0] = Double.MIN_VALUE;
            best[1] = -1;

            for (int i = 0; i < d.length; i++)
            {
                if (d[i] > best[0])
                {
                    best[0] = d[i];
                    best[1] = i;
                }
            }

            return best;
        }
    }

    protected void prepareTourData()
    {
        // zero the entire matrix
        for (int i = 0; i < tourMatrix.length; i++)
        {
            Arrays.fill(tourMatrix[i], 0.0);
        }
        // process all cells
        LinkedList<Cell> allCells = lattice.getDuplicateCellList();
        for (Cell c : allCells)
        {
            // process all units in the cell
            LinkedList<Unit> allUnits = c.getDuplicateTailList(Constants.LOCK_WAIT_TIME);
            for (Unit u : allUnits)
            {
                int[] tour = ((TSPUnit) u).getData();
                for (int position = 0; position < tour.length; position++)
                {
                    tourMatrix[tour[position]][position]++;
                }
            }
        }
        // normalis the matrix
        normaliseMatrix(tourMatrix);
    }

    protected void normaliseMatrix(double[][] v)
    {
        // calculate max for column
        double max = Double.MIN_VALUE;
        for (int i = 0; i < v.length; i++)
        {
            for (int j = 0; j < v[i].length; j++)
            {
                if (v[i][j] > max)
                {
                    max = v[i][j];
                }
            }
        }

        // normalise the column
        for (int i = 0; i < v.length; i++)
        {
            for (int j = 0; j < v[i].length; j++)
            {
                v[i][j] /= max;
            }
        }
    }



    public boolean isOrderByFrequency()
    {
        return orderByFrequency;
    }

    public void setOrderByFrequency(boolean orderByFrequency)
    {
        this.orderByFrequency = orderByFrequency;
    }

}
