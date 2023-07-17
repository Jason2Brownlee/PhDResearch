
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
    implements ActionListener
{
    // HACK!
    public final static double DEFAULT_DIMENSIONS = 2;
    
    public enum Mode {
        MU_PLUS_LAMBDA, 
        MU_COMMA_LAMBDA}
    
    public final static int DEFAULT_POPSIZE = 20;
    public final static double DEFAULT_TAU = Math.pow(2.0*DEFAULT_DIMENSIONS, (-1.0/2.0));
    public final static double DEFAULT_ETA = Math.pow(4.0*DEFAULT_DIMENSIONS, (-1.0/4.0));
    public final static double DEFAULT_RHO = ((5*Math.PI)/180.0);
    public final static Mode DEFAULT_MODE = Mode.MU_PLUS_LAMBDA;
    public final static double DEFAULT_CROSSOVER = 0.7;
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField tauField;
    protected JTextField etaField;
    protected JTextField rhoField;
    protected JComboBox modeField;
    protected JTextField crossoverField;
    
    protected Random r;   
    
    protected long seed;
    protected int popsize;
    protected double tau;
    protected double eta;
    protected double rho;    
    protected Mode mode;
    protected double crossover;
    
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
    public Solution executeAlgorithm(Problem p)
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
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        return executeAlgorithm2(p, pop);
    }
    

    public Solution executeAlgorithm2(Problem p, LinkedList<Solution> pop)
    {        
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // reproduce
            LinkedList<Solution> children = generateChildren(pop, p);
            // evaluate
            p.cost(children);
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver); 
            // finalise the population            
            pop = finalisePopulation(pop, children, p);  
            // end of iteration
            notifyListeners(p,pop,bestEver);
        }
        
        return bestEver;
    }
    
    protected LinkedList<Solution> finalisePopulation(LinkedList<Solution> pop, LinkedList<Solution> children, Problem p)
    {
        LinkedList<Solution> finalPop = new LinkedList<Solution>();
        
        switch(mode)
        {
            case MU_PLUS_LAMBDA:
            {
                finalPop.addAll(pop);
                finalPop.addAll(children);                
                Collections.sort(finalPop);
                
                while(finalPop.size() > popsize)
                {
                    if(p.isMinimise())
                    {
                        finalPop.removeLast(); // remove worst
                    }
                    else
                    {
                        finalPop.removeFirst(); // remove worst
                    }
                }
                break;
            }
            case MU_COMMA_LAMBDA:
            {
                finalPop.addAll(children);
                break;
            }
            default:
            {
                throw new RuntimeException("Invalid mode");
            }
        }
        
        return finalPop;
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
            ESSolution s2 = recombine(p2, p1);            
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
        PopulationAlgorithmUtils.fixCoordBounds(coord, p);
    }
    
    
    /**
     * Uniform crossover
     * 
     * @param p1
     * @param p2
     * @return
     */
    protected ESSolution recombine(ESSolution p1, ESSolution p2)
    {
        ESSolution s = new ESSolution(new double[p1.getCoordinate().length]);
        
        if(r.nextDouble() <= crossover)
        {
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
        }
        // copy
        else
        {
            // copy the first parent
            System.arraycopy(p1.getCoordinate(), 0, s.getCoordinate(), 0, p1.getCoordinate().length);
            System.arraycopy(p1.stdevs, 0, s.stdevs, 0, p1.stdevs.length);
            System.arraycopy(p1.directions, 0, s.directions, 0, p1.directions.length);
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
        seed = System.currentTimeMillis();
        popsize = DEFAULT_POPSIZE;
        mode = DEFAULT_MODE;
        crossover = DEFAULT_CROSSOVER;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel tauLabel = new JLabel("tau coefficient:");
        JLabel etaLabel = new JLabel("eta coefficient");
        JLabel rhoLabel = new JLabel("rho coefficient:");
        JLabel modeLabel = new JLabel("ES Mode:");
        JLabel crossoverLabel = new JLabel("Crossover probability:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        tauField = new JTextField(Double.toString(tau), 10);
        etaField = new JTextField(Double.toString(eta), 10);
        rhoField = new JTextField(Double.toString(rho), 10);
        modeField = new JComboBox(Mode.values());
        modeField.addActionListener(this);
        crossoverField = new JTextField(Double.toString(crossover), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(tauLabel);
        labelPane.add(etaLabel);
        labelPane.add(rhoLabel);
        labelPane.add(crossoverLabel);
        labelPane.add(modeLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(tauField);
        fieldPane.add(etaField);
        fieldPane.add(rhoField);
        fieldPane.add(crossoverField);
        fieldPane.add(modeField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == modeField)
        {
            mode = (Mode) modeField.getSelectedItem();
        }
    }
    
    @Override
    protected int getNumParameters()
    {
        return 7;
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
            if(popsize>p.getMaxEvaluations()||popsize<=0)
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
    }

}
