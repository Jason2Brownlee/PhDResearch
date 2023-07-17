
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.BitStringCommonUtils;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: FitnessSharingGeneticAlgorithm<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class FitnessSharingGeneticAlgorithm extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    public final static double DEFAULT_CROSSOVER = 0.95;
    public final static double DEFAULT_MUTATION = 0.005;
    public final static int DEFAULT_POPSIZE = 100;
    public final static int DEFAULT_BOUTSIZE = 2;
    public final static int DEFAULT_ELITES = 1;
    public final static double DEFAULT_SHARING_RADIUS = 0.2;
    public final static double DEFAULT_SHAPE = 1.0;    
    
    protected Random r;
    
    protected long seed;
    protected double crossover;
    protected double mutation;
    protected int popsize;
    protected int boutSize;
    protected int elitism;
    protected double shareRadius;
    protected double alpha;
    
    protected JTextField seedField;
    protected JTextField crossoverField;
    protected JTextField mutationField;
    protected JTextField popsizeField;
    protected JTextField boutsizeField;
    protected JTextField elitismField;
    protected JTextField shareRadiusField;
    protected JTextField alphaField;
    
    
    protected static class BinarySolution extends Solution
    {
        boolean [] bitString;
        double deratedFitness;
        
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
            s.setCoordinate(BitStringCommonUtils.bitsToCoord(b,p));
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        // derate fitness
        derateFitness(pop, p);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // select
            LinkedList<Solution> selected = stochasticUniversalSampling(pop, popsize, p);            
            // reproduce
            LinkedList<Solution> children = reproduce(selected, popsize, p);            
            // evaluate
            p.cost(children);
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver);
            // derate fitness
            derateFitness(children, p);            
            // elitism
            elitism(pop, children, p);
            pop = children;
        }
        
        return bestEver;
    }
    
    
    /**
     * Ensures de-rated fitness is always positive, 1.0 offset, and
     * given the same fitness distribution (linear mapping)
     * 
     * @param pop
     * @param p
     */
    protected void derateFitness(LinkedList<Solution> pop, Problem p)
    {
        double min = Double.MAX_VALUE;
        
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
        // derate all fitness
        for(Solution s : pop)
        {
            double sum = 0.0;
            for (int i = 0; i < pop.size(); i++)
            {
                BinarySolution ss = (BinarySolution) pop.get(i);
                sum += shareFunction((BinarySolution)s, ss);
            }
            
            double f = s.getScore();            
            if(p.isMinimise())
            {
                f = f * -1.0;
            }            
            f = f + Math.abs(min) + 1.0;
            
            ((BinarySolution)s).deratedFitness = (f / sum);
        }
    }
    
    
    protected double shareFunction(BinarySolution s1, BinarySolution s2)
    {
        double d = hammingRatioDistance(s1, s2);
        
        if(d >= shareRadius)
        {
            return 0.0;
        }
        
        double sv = 1.0 - Math.pow((d/shareRadius), alpha);
        return sv;
    }
    
    protected double hammingRatioDistance(BinarySolution s1, BinarySolution s2)
    {
        boolean [] b1 = s1.bitString;
        boolean [] b2 = s2.bitString;
        
        int diff = 0;        
        for (int i = 0; i < b1.length; i++)
        {
            if(b1[i] != b2[i])
            {
                diff++;
            }
        }
        
        return ((double)diff / (double)b1.length);
    }
    
    
    /**
     * Assumed to be sorted
     * @param pop
     * @param children
     * @param p
     */
    protected void elitism(
            LinkedList<Solution> pop, 
            LinkedList<Solution> children, 
            Problem p)
    {   
        for (int i = 0; i < elitism; i++)
        {
            children.removeFirst();
            if(p.isMinimise())
            {
                children.addLast(pop.get(i));
            }
            else
            {
                children.addLast(pop.get(pop.size()-1-i));
            }
        }
    }
    
    

    
    public LinkedList<Solution> reproduce(LinkedList<Solution> pop, int totalChildren, Problem p)
    {
        LinkedList<Solution> children = new LinkedList<Solution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            Solution p1 = pop.get(i);
            Solution p2 = pop.get(i+1);            
            Solution [] c = crossover((BinarySolution)p1, (BinarySolution)p2);
            for (int j = 0; children.size()<totalChildren && j < c.length; j++)
            {
                mutate((BinarySolution)c[j]);
                c[j].setCoordinate(BitStringCommonUtils.bitsToCoord(((BinarySolution)c[j]).getBitString(),p));
                children.add(c[j]);
            }
            
        }
        return children;
    }
    
    protected void mutate(BinarySolution s)
    {
        boolean [] string = s.getBitString();
        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }
    
    protected Solution [] crossover(BinarySolution p1, BinarySolution p2)
    {
        Solution [] children = new Solution[2];        
        // make a cut - or no cut as it were
        int cutPoint = (r.nextDouble()<crossover) ? r.nextInt(p1.getBitString().length) : 0;
        // create vectors
        boolean [] v1 = new boolean[p1.getBitString().length];
        boolean [] v2 = new boolean[p1.getBitString().length];
        // prepare vectors
        prepareVector(v1, p1.getBitString(), p2.getBitString(), cutPoint); // normal
        prepareVector(v2, p2.getBitString(), p1.getBitString(), cutPoint); // reversed
        // store children
        children[0] = new BinarySolution(v1);        
        children[1] = new BinarySolution(v2);
        return children;
    }
    
    protected void prepareVector(boolean [] v, boolean [] p1, boolean [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }
    
    
    /**
     * Uses *derated* fitness
     * 
     * @param pop
     * @param numToSelection
     * @param p
     * @return
     */
    public LinkedList<Solution> stochasticUniversalSampling(
            LinkedList<Solution> pop, 
            int numToSelection,
            Problem p)
    {
        // shuffle 
        Collections.shuffle(pop, r);
        
        LinkedList<Solution> selected = new LinkedList<Solution>();
        
        if((numToSelection%2) != 0)
        {
            numToSelection++; // need one more for luck (even)
        }
        
        double summedFitness = 0.0;
        for (int i = 0; i < pop.size(); i++)
        {
            summedFitness += ((BinarySolution)pop.get(i)).deratedFitness;
        }

        double sum = 0.0;
        double singleMarker = 1.0 / numToSelection;
        double positionOfMarker = singleMarker * r.nextDouble();

        for (int i = 0; selected.size()<numToSelection && i < pop.size(); i++)
        {
            // calculate this individuals slice of pie
            double slice = (((BinarySolution)pop.get(i)).deratedFitness / summedFitness);
            // sum the fitness searching for marker locations
            sum += slice;
            // process the marker on current summed fitness
            // can be 0 - n copies of this individual depending on size of
            // fitness
            while (selected.size()<numToSelection && positionOfMarker < sum)
            {
                // add the current individual
                selected.add(pop.get(i));
                // increment the marker
                positionOfMarker += singleMarker;
            }
        }
        
        return selected;
    }  
    
    
    

    @Override
    public String getName()
    {
        return "Fitness Sharing Genetic Algorithm (FSGA)";
    }


    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        crossover = DEFAULT_CROSSOVER;
        mutation = DEFAULT_MUTATION;
        popsize = DEFAULT_POPSIZE;
        boutSize = DEFAULT_BOUTSIZE;
        elitism = DEFAULT_ELITES;
        shareRadius = DEFAULT_SHARING_RADIUS;
        alpha = DEFAULT_SHAPE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel crossoverLabel = new JLabel("Crossover percentage:");
        JLabel mutationLabel = new JLabel("Mutation percentage:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel boutSizeLabel = new JLabel("Tournament bout size:");
        JLabel elitismLabel = new JLabel("Total elites:");
        JLabel shareRadiusLabel = new JLabel("Sharing Radius:");
        JLabel alphaLabel = new JLabel("Sharing Shape:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        crossoverField = new JTextField(Double.toString(crossover), 10);
        mutationField = new JTextField(Double.toString(mutation), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        boutsizeField = new JTextField(Integer.toString(boutSize), 10);
        elitismField = new JTextField(Integer.toString(elitism), 10);
        shareRadiusField = new JTextField(Double.toString(shareRadius), 10);
        alphaField = new JTextField(Double.toString(alpha), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(crossoverLabel);
        labelPane.add(mutationLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(boutSizeLabel);
        labelPane.add(elitismLabel);
        labelPane.add(shareRadiusLabel);
        labelPane.add(alphaLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(crossoverField);
        fieldPane.add(mutationField);
        fieldPane.add(popsizeField);
        fieldPane.add(boutsizeField);
        fieldPane.add(elitismField);
        fieldPane.add(shareRadiusField);
        fieldPane.add(alphaField);

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
        // crossover
        try
        {
            crossover = Double.parseDouble(crossoverField.getText());
        }
        catch (Exception e)
        {
            crossover = DEFAULT_CROSSOVER;
            crossoverField.setText("" + crossover);
        }
        finally
        {
            if(crossover>1||crossover<0)
            {
                crossover = DEFAULT_CROSSOVER;
                crossoverField.setText("" + crossover);
            }
        }
        // mutation
        try
        {
            mutation = Double.parseDouble(mutationField.getText());
        }
        catch (Exception e)
        {
            mutation = DEFAULT_MUTATION;
            mutationField.setText("" + mutation);
        }
        finally
        {
            if(mutation>1||mutation<0)
            {
                mutation = DEFAULT_MUTATION;
                mutationField.setText("" + mutation);
            }
        }
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
        // boutsize
        try
        {
            boutSize = Integer.parseInt(boutsizeField.getText());
        }
        catch (Exception e)
        {
            boutSize = DEFAULT_BOUTSIZE;
            boutsizeField.setText("" + boutSize);
        }
        finally
        {
            if(boutSize>popsize||popsize<0)
            {
                boutSize = DEFAULT_BOUTSIZE;
                boutsizeField.setText("" + boutSize);
            }
        }
        // elitism
        try
        {
            elitism = Integer.parseInt(elitismField.getText());
        }
        catch (Exception e)
        {
            elitism = DEFAULT_ELITES;
            elitismField.setText("" + elitism);
        }
        finally
        {
            if(elitism>popsize||elitism<0)
            {
                elitism = DEFAULT_ELITES;
                elitismField.setText("" + elitism);
            }
        }
        // shareRadius
        try
        {
            shareRadius = Double.parseDouble(shareRadiusField.getText());
        }
        catch (Exception e)
        {
            shareRadius = DEFAULT_SHARING_RADIUS;
            shareRadiusField.setText("" + shareRadius);
        }
        finally
        {
            if(shareRadius>1||shareRadius<0)
            {
                shareRadius = DEFAULT_SHARING_RADIUS;
                shareRadiusField.setText("" + shareRadius);
            }
        }
        // alpha
        try
        {
            alpha = Double.parseDouble(alphaField.getText());
        }
        catch (Exception e)
        {
            alpha = DEFAULT_SHAPE;
            alphaField.setText("" + alpha);
        }
        finally
        {
            if(alpha<0)
            {
                alpha = DEFAULT_SHAPE;
                alphaField.setText("" + alpha);
            }
        }
    }

    @Override
    protected int getNumParameters()
    {
        return 8;
    }
}

