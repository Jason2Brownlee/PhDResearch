
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
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: EvolutionStrategies<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class EvolutionStrategies extends Algorithm
{
    // HACK!
    public final static double DEFAULT_DIMENSIONS = 2;
    
    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 10;
    public final static double DEFAULT_TAU = Math.pow(2.0*DEFAULT_DIMENSIONS, (-1.0/2.0));
    public final static double DEFAULT_ETA = Math.pow(4.0*DEFAULT_DIMENSIONS, (-1.0/4.0));
    public final static double DEFAULT_RHO = ((5*Math.PI)/180.0);
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField tauField;
    protected JTextField etaField;
    protected JTextField rhoField;
    
    protected Random r;   
    
    protected long seed;
    protected int popsize;
    protected double tau;
    protected double eta;
    protected double rho;    
    
    protected class ESSolution extends Solution
    {
        protected double [] stdevs;
        protected double [] directions;
        
        public ESSolution(double [] aCoord)
        {
            super(aCoord);
            stdevs = new double[aCoord.length];
            directions = new double[aCoord.length];
        }
        
        /**
         * only needed for initial random pop
         * @param p
         */
        protected void prepare(Problem p)
        {
            double [][] minmax = p.getMinmax();
            
            for (int i = 0; i < p.getDimensions(); i++)
            {
                stdevs[i] = (minmax[i][1]-minmax[i][0]) * r.nextDouble();
                directions[i] = (2*Math.PI) * r.nextDouble();
            }
        }

        public double[] getDirections()
        {
            return directions;
        }

        public double[] getStdevs()
        {
            return stdevs;
        }
    }
    
    
    

    @Override
    protected Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            double [] c = RandomUtils.randomPointInRange(p, r);
            ESSolution s = new ESSolution(c);
            s.prepare(p);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // reproduce
            LinkedList<Solution> children = generateChildren(pop, p);
            // evaluate
            p.cost(children);
            // replace
            children.addAll(pop);
            Collections.sort(children);
            while(children.size() > popsize)
            {
                if(p.isMinimise())
                {
                    children.removeLast(); // remove worst
                }
                else
                {
                    children.removeFirst(); // remove worst
                }
            }
            pop = children;
            // test
            bestEver = PopulationAlgorithmUtils.getBest(pop, p); 
        }
        
        return bestEver;
    }
    public LinkedList<Solution> generateChildren(LinkedList<Solution> pop, Problem p)
    {        
        LinkedList<Solution> cs = new LinkedList<Solution>();
        // randomise parents
        Collections.shuffle(pop, r);
        // recombine        
        for (int i = 0; i < pop.size(); i+=2)
        {
            ESSolution p1 = (ESSolution)pop.get(i);
            ESSolution p2 = (ESSolution)pop.get(i+1);            
            // recombination
            ESSolution s1 = recombine(p1, p2);
            ESSolution s2 = recombine(p1, p2);            
            // mutation            
            mutate(s1, p);
            mutate(s2, p);
            // add
            cs.add(s1);
            cs.add(s2);
        }
        
        return cs;
    }
    
    protected void mutate(ESSolution s, Problem p)
    {        
        // mutate angles
        for (int i = 0; i < s.directions.length; i++)
        {
            s.directions[i] = (s.directions[i] + (rho*r.nextGaussian())) % (2.0*Math.PI);
        }
        
        // mutate stdev's
        double ztau = r.nextGaussian();
        for (int i = 0; i < s.stdevs.length; i++)
        {
            s.stdevs[i] = s.stdevs[i] * Math.exp((tau * ztau) + (eta*r.nextGaussian()));
        }        
        
        // mutate coords        
        double [] coord = s.getCoordinate();
        for (int i = 0; i < coord.length; i++)
        {
            coord[i] = coord[i] + s.directions[i] * s.stdevs[i] * r.nextGaussian();                        
        }
        PopulationAlgorithmUtils.bounceCoord(coord, p);
    }
    
    
    /**
     * Uniform crossover
     * @param p1
     * @param p2
     * @return
     */
    protected ESSolution recombine(ESSolution p1, ESSolution p2)
    {
        ESSolution s = new ESSolution(new double[p1.getCoordinate().length]);
        
        // coord
        for (int i = 0; i < p1.getCoordinate().length; i++)
        {
            s.getCoordinate()[i] = (r.nextBoolean()) ? p1.getCoordinate()[i] : p2.getCoordinate()[i];
        }
        // stdev
        for (int i = 0; i < p1.stdevs.length; i++)
        {
            s.stdevs[i] = (r.nextBoolean()) ? p1.stdevs[i] : p2.stdevs[i];
        }
        // angles
        for (int i = 0; i < p1.directions.length; i++)
        {
            s.directions[i] = (r.nextBoolean()) ? p1.directions[i] : p2.directions[i];
        }
        
        return s;
    }

    @Override
    public String getName()
    {
        return "Evolution Strategies (ES)";
    }
    


    @Override
    protected JPanel getConfigurationPane()
    {
        tau = DEFAULT_TAU;
        eta = DEFAULT_ETA;
        rho = DEFAULT_RHO;
        seed = DEFAULT_SEED;
        popsize = DEFAULT_POPSIZE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel tauLabel = new JLabel("tau coefficient:");
        JLabel etaLabel = new JLabel("eta coefficient");
        JLabel rhoLabel = new JLabel("rho coefficient:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        tauField = new JTextField(Double.toString(tau), 10);
        etaField = new JTextField(Double.toString(eta), 10);
        rhoField = new JTextField(Double.toString(rho), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(tauLabel);
        labelPane.add(etaLabel);
        labelPane.add(rhoLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(tauField);
        fieldPane.add(etaField);
        fieldPane.add(rhoField);

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
        return 5;
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
            if((popsize%2)!=0)
            {
                popsize++;
            }
            if(popsize>p.getMaxEvaluations()||popsize<0)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
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
        
        // eta
        try
        {
            eta = Double.parseDouble(etaField.getText());
        }
        catch (Exception e)
        {
            eta = DEFAULT_ETA;
            etaField.setText("" + eta);
        }
        
        // rho
        try
        {
            rho = Double.parseDouble(rhoField.getText());
        }
        catch (Exception e)
        {
            rho = DEFAULT_RHO;
            rhoField.setText("" + rho);
        }
    }

}
