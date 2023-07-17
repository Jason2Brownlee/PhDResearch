
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
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
 * Type: ClonalSelectionAlgorithm<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ClonalSelectionAlgorithm extends Algorithm implements ActionListener
{
    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 50;
    public final static double DEFAULT_CLONEFACTOR = 0.1;
    public final static double DEFAULT_MUTATIONFACTOR = 2.5;
    public final static int DEFAULT_RANDOMS = 1;
    public final static BitStringUtils.DECODE_MODE DEFAULT_DECODE_MODE = BitStringUtils.DECODE_MODE.Binary;
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField cloneFactorField;
    protected JTextField mutateFactorField;
    protected JTextField randomField;
    protected JComboBox decodeField;
    
    protected Random r;
    
    protected long seed;
    protected int popsize; // N    
    protected double cloneFactor; // Beta
    protected double mutateFactor; // rho
    protected int randomReplacements; // d
    public BitStringUtils.DECODE_MODE decodeMode;
    
    
    protected static class BinarySolution extends Solution
    {
        boolean [] bitString;
        double adjustedFitness;
        
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
    protected Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < popsize)
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
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // clone and mutate
            LinkedList<Solution> children = generateChildren(pop, p);
            // evaluate
            p.cost(children);
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver);
            // union the populations
            pop.addAll(children);
            // select the N best
            Collections.sort(pop);
            while(pop.size() > popsize)
            {
                if(p.isMinimise())
                {
                    pop.removeLast();
                }
                else
                {
                    pop.removeFirst();
                }
            }
            if(randomReplacements > 0 && p.isReamainingEvaluations())
            {
                LinkedList<Solution> randoms = new LinkedList<Solution>();                
                for (int i = 0; i < randomReplacements; i++)
                {
                    boolean [] b = RandomUtils.randomBitString(r, p);
                    BinarySolution s = new BinarySolution();
                    s.setBitString(b);
                    decode(p, s);
                    randoms.add(s);
                }
                p.cost(randoms);
                bestEver = PopulationAlgorithmUtils.getBest(randoms, p, bestEver);
                // make room - remember pop is still sorted
                for (int i = 0; i < randomReplacements; i++)
                {
                    if(p.isMinimise())
                    {
                        pop.removeLast();
                    }
                    else
                    {
                        pop.removeFirst();
                    }
                }
                pop.addAll(randoms);
                notifyListeners(p,pop);
            }
        }
        
        return bestEver;
    }
    
    /**
     * Zero offset maximising fitness - relative to pop at time T 
     * 
     * @param pop
     * @param p
     */
    protected double calculateAdjustedFitness(LinkedList<Solution> pop, Problem p)
    {
        double min = Double.POSITIVE_INFINITY;
        
        // locate lowest score
        for(Solution s : pop)
        {
            double f = s.getScore();
            if(p.isMinimise())
            {
                f = f * -1.0;
            }            
            if(f < min)
            {
                min = f;
            }
        }
        // adjust all fitness
        double max = Double.NEGATIVE_INFINITY;
        for(Solution s : pop)
        {            
            double f = s.getScore();            
            if(p.isMinimise())
            {
                f = f * -1.0;
            }            
            f = f + Math.abs(min) + 1.0;            
            ((BinarySolution)s).adjustedFitness = f;
            if(f > max)
            {
                max = f;
            }
        }
        
        return max;
    }
    
    protected void decode(Problem p, BinarySolution bs)
    {
        boolean [] b = bs.getBitString();
        double [] coord = BitStringUtils.decode(p, decodeMode, b);
        bs.setCoordinate(coord);
    }
    
    protected LinkedList<Solution> generateChildren(LinkedList<Solution> pop, Problem p)
    {
        LinkedList<Solution> np = new LinkedList<Solution>();
        
        // adjust fitness scores
        double max = calculateAdjustedFitness(pop, p);
        // determine clone size
        int cloneSize = (int) Math.round(cloneFactor * popsize);
        
        // clone and mutate in one step
        for (Solution s : pop)
        {
            double mutation = mutationAmount(((BinarySolution)s).adjustedFitness, max);
            
            for (int i = 0; i < cloneSize; i++)
            {
                BinarySolution c = cloneAndMutate((BinarySolution)s, mutation, p);
                np.add(c);
            }
        }
        
        return np;
    }
    
    protected BinarySolution cloneAndMutate(BinarySolution parent, double m, Problem p)
    {
        // clone and mutate
        boolean [] par = parent.bitString;
        boolean [] c = new boolean[par.length];
        System.arraycopy(par, 0, c, 0, par.length);        
        // mutate
        for (int i = 0; i < c.length; i++)
        {
            if(r.nextDouble() < m)
            {
                c[i] = !c[i];
            }
        }
        BinarySolution s = new BinarySolution();
        s.setBitString(c);
        decode(p, s);
        return s;
    }
    
    
    protected double mutationAmount(double f, double max)
    {        
        // inverse fitness proportionate, the factor just adjusts the distribution
        double d = Math.exp(-mutateFactor * f / max);
        return d;
    }
    

    @Override
    public String getName()
    {
        return "Clonal Selection Algorithm (CLONALG)";
    }


    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        popsize = DEFAULT_POPSIZE;
        cloneFactor = DEFAULT_CLONEFACTOR;
        mutateFactor = DEFAULT_MUTATIONFACTOR;
        randomReplacements = DEFAULT_RANDOMS;
        decodeMode = DEFAULT_DECODE_MODE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel cloneFactorLabel = new JLabel("Clone Factor (beta):");
        JLabel mutateFactorLabel = new JLabel("Mutation Factor (rho):");
        JLabel randomsLabel = new JLabel("Random Replacements (d):");
        JLabel decodeModeLabel = new JLabel("Bit decode mode:");
        
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        cloneFactorField = new JTextField(Double.toString(cloneFactor), 10);
        mutateFactorField = new JTextField(Double.toString(mutateFactor), 10);
        randomField = new JTextField(Integer.toString(randomReplacements), 10);
        decodeField = new JComboBox(BitStringUtils.DECODE_MODE.values());
        decodeField.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(cloneFactorLabel);
        labelPane.add(mutateFactorLabel);
        labelPane.add(randomsLabel);
        labelPane.add(decodeModeLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(cloneFactorField);
        fieldPane.add(mutateFactorField);
        fieldPane.add(randomField);
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
        return 6;
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
            seed = Long.parseLong(seedField.getText());
        }
        catch (Exception e)
        {
            seed = DEFAULT_SEED;
            seedField.setText(Long.toString(seed));
        }
        r = new Random(seed);
        // popsize
        try
        {
            popsize = Integer.parseInt(popsizeField.getText());
        }
        catch (Exception e)
        {
            popsize = DEFAULT_POPSIZE;
            popsizeField.setText("" + popsize);
        }
        finally
        {
            if(popsize>p.getMaxEvaluations()||popsize<0)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
            }
        }
        // cloneFactor
        try
        {
            cloneFactor = Double.parseDouble(cloneFactorField.getText());
        }
        catch (Exception e)
        {
            cloneFactor = DEFAULT_CLONEFACTOR;
            cloneFactorField.setText("" + cloneFactor);
        }
        finally
        {
            if(cloneFactor<=0)
            {
                cloneFactor = DEFAULT_CLONEFACTOR;
                cloneFactorField.setText("" + cloneFactor);
            }
        }
        // mutateFactor
        try
        {
            mutateFactor = Double.parseDouble(mutateFactorField.getText());
        }
        catch (Exception e)
        {
            mutateFactor = DEFAULT_MUTATIONFACTOR;
            mutateFactorField.setText("" + mutateFactor);
        }
        finally
        {
            if(mutateFactor<=0)
            {
                mutateFactor = DEFAULT_MUTATIONFACTOR;
                mutateFactorField.setText("" + mutateFactor);
            }
        }
        // randomReplacements
        try
        {
            randomReplacements = Integer.parseInt(randomField.getText());
        }
        catch (Exception e)
        {
            randomReplacements = DEFAULT_RANDOMS;
            randomField.setText("" + randomReplacements);
        }
        finally
        {
            if(randomReplacements>popsize||popsize<0)
            {
                randomReplacements = DEFAULT_RANDOMS;
                randomField.setText("" + randomReplacements);
            }
        }
    }
}
