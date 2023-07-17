
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.BitStringUtils;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;


/**
 * Type: GeneralizedExtremalOptimization<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GeneralizedExtremalOptimization extends Algorithm implements ActionListener
{
    public final static int DEFAULT_INITAL_POPSIZE = 100;
    public final static double DEFAULT_TAU = 1.25;
    public final static BitStringUtils.DECODE_MODE DEFAULT_DECODE_MODE = BitStringUtils.DECODE_MODE.Binary;
    
    protected JTextField seedField;
    protected JTextField initialPopulationSizeField;
    protected JTextField tauField;
    protected JComboBox decodeField;
    
    protected Random r;
    protected FitnessDeltaComparator comparator;
    
    protected long seed;
    protected int initialPopulationSize;
    protected double tau;
    public BitStringUtils.DECODE_MODE decodeMode;
        

    public GeneralizedExtremalOptimization()
    {
        comparator = new FitnessDeltaComparator();
    }    
    
    protected static class BinarySolution extends Solution
    {
        boolean [] bitString;
        double fitnessDelta;
        
        public BinarySolution(){}
        public BinarySolution(boolean[] abitString)
        {
            setBitString(abitString);
        }

        public boolean[] getBitString()
        {
            return bitString;
        }

        public void setBitString(boolean[] bitString)
        {
            this.bitString = bitString;
        }
    }
    
    
    @Override
    public Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < initialPopulationSize)
        {
            boolean [] b = RandomUtils.randomBitString(r, p);
            BinarySolution s = new BinarySolution();
            s.setBitString(b);
            decode(p, s);
            pop.add(s);
        }         
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        Solution current = bestEver;
        //pop = null; // no longer needed
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // reproduce
            LinkedList<Solution> children = generateChildren(p, (BinarySolution)current);
            // evaluate
            p.cost(children);
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver);
            if(p.isReamainingEvaluations())
            {
                // set fitness deltas
                for (Solution s : children)
                {
                    BinarySolution b = (BinarySolution) s;
                    b.fitnessDelta = (b.getScore() - current.getScore()); 
                }
                // sort by fitness delta (asc)
                Collections.sort(children, comparator);
                // ensure that it worst from best
                if(!p.isMinimise())
                {
                    Collections.reverse(children);
                }
                // select a potential new solution
                Solution newCurrent = null;
                do
                {
                    int selection = r.nextInt(children.size());
                    double prob = Math.pow(selection+1, -tau);
                    if(prob >= r.nextDouble())
                    {
                        // accepted
                        newCurrent = children.get(selection);
                    }
                }
                while(newCurrent == null);
                if(p.isBetter(newCurrent, current))
                {
                    current = newCurrent;
                }
                pop.clear();
                pop.add(current);
                notifyListeners(p,pop,bestEver);
            }
        }        
        
        return bestEver;
    }    
    
    protected class FitnessDeltaComparator implements Comparator<Solution>
    {        
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         * @param o1
         * @param o2
         * @return
         */
        public int compare(Solution o1, Solution o2)
        {
            BinarySolution b1 = (BinarySolution) o1;
            BinarySolution b2 = (BinarySolution) o2;
            
            if(b1.fitnessDelta < b2.fitnessDelta)
            {
                return -1;
            }
            else if(b1.fitnessDelta > b2.fitnessDelta)
            {
                return +1;
            }
            return 0;
        }
        
    }
    
    protected void decode(Problem p, BinarySolution bs)
    {
        boolean [] b = bs.getBitString();
        double [] coord = BitStringUtils.decode(p, decodeMode, b);
        bs.setCoordinate(coord);
    }
    
    
    protected LinkedList<Solution> generateChildren(Problem p, BinarySolution current)
    {
        LinkedList<Solution> c = new LinkedList<Solution>();        
        boolean [] parent = current.bitString;
        
        for (int i = 0; i < parent.length; i++)
        {
            boolean [] child = new boolean[parent.length];
            System.arraycopy(parent, 0, child, 0, parent.length);
            // mutation
            child[i] = !child[i];
            BinarySolution s = new BinarySolution();
            s.setBitString(child);
            decode(p, s);
            c.add(s);
        }        
        
        return c;
    }
    
    

    @Override
    public String getName()
    {
        return "Generalized Extremal Optimization (GEO)";
    }

    

    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = System.currentTimeMillis();
        initialPopulationSize = DEFAULT_INITAL_POPSIZE;
        tau = DEFAULT_TAU;
        decodeMode = DEFAULT_DECODE_MODE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel initialPopulationSizeLabel = new JLabel("Initial population size:");
        JLabel tauFactorLabel = new JLabel("Tau:");
        JLabel decodeModeLabel = new JLabel("Bit decode mode:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        initialPopulationSizeField = new JTextField(Integer.toString(initialPopulationSize), 10);
        tauField = new JTextField(Double.toString(tau), 10);
        decodeField = new JComboBox(BitStringUtils.DECODE_MODE.values());
        decodeField.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(initialPopulationSizeLabel);
        labelPane.add(tauFactorLabel);
        labelPane.add(decodeModeLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(initialPopulationSizeField);
        fieldPane.add(tauField);
        fieldPane.add(decodeField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);

        return contentPane;
    }

    @Override
    protected int getNumParameters()
    {
        return 4;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == decodeField)
        {
            decodeMode = (BitStringUtils.DECODE_MODE) decodeField.getSelectedItem();
        }        
    }

    @Override
    public void initialise(Problem p)
    {
        // seed
        try
        {
            if(seedField.getText().equalsIgnoreCase("SYSTIME"))
            {
                seed = System.currentTimeMillis();
            }
            else
            {
                seed = Long.parseLong(seedField.getText());
            }
        }
        catch(Exception e)
        {
            seed = System.currentTimeMillis();
            seedField.setText("SYSTIME");
        }
        r = new Random(seed);
        // initialPopulationSizeField
        try
        {
            initialPopulationSize = Integer.parseInt(initialPopulationSizeField.getText());
        }
        catch (Exception e)
        {
            initialPopulationSize = DEFAULT_INITAL_POPSIZE;
            initialPopulationSizeField.setText("" + initialPopulationSize);
        }
        finally
        {
            if(initialPopulationSize>p.getMaxEvaluations()||initialPopulationSize<=0)
            {
                initialPopulationSize = DEFAULT_INITAL_POPSIZE;
                initialPopulationSizeField.setText("" + initialPopulationSize);  
            }
        }
        // tau
        try
        {
            tau = Double.parseDouble(tauField.getText());
        }
        catch (Exception e)
        {
            tau = DEFAULT_TAU;
            tauField.setText("" + tau);
        }
        finally
        {
            if(tau<=0)
            {
                tau = DEFAULT_TAU;
                tauField.setText("" + tau);
            }
        }
    }

}
