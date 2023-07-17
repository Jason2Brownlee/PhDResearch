
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
 * Type: ParticleSwarmOptimization<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class ParticleSwarmOptimization extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    public final static int DEFAULT_POPSIZE = 10;
    public final static double DEFAULT_VMAX = 1.0;
    public final static double DEFAULT_C1 = 2.0;
    public final static double DEFAULT_C2 = 2.0;
    public final static double DEFAULT_MOMENTUM = 0.5;
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField vMaxField;
    protected JTextField c1Field;
    protected JTextField c2Field;
    protected JTextField momentumField;
    
    protected Random r;   
    
    protected long seed;
    protected int popsize;
    protected double vMax;
    protected double c1;
    protected double c2;    
    protected double momentum;
    
    
    protected class PSOSolution extends Solution
    {
        public double [] velocity;        
        public double [] pbestcoord;
        public double pbestScore;
        
        public PSOSolution(double [] aCoord)
        {
            super(aCoord);
            velocity = new double[aCoord.length];
            pbestcoord = new double[aCoord.length];
            pbestScore = Double.NaN;
        }
        
        /**
         * only needed for initial random pop
         * @param p
         */
        protected void prepare(Problem p)
        {            
            if(!isEvaluated)
            {
                throw new RuntimeException("Cannot prepare new particle without it being evaluated first!");
            }            
            
            double [][] minmax = p.getMinmax();
            for (int i = 0; i < velocity.length; i++)
            {
                // randomised initial velocity (until replaced)
                double v = (minmax[i][1] - minmax[i][0]) * vMax;
                velocity[i] = (r.nextDouble() * (v*0.5));
                velocity[i] *= (r.nextBoolean() ? -1 : 1);
            }
            // assume evaluated
            pbestScore = score;
            System.arraycopy(coordinate, 0, pbestcoord, 0, coordinate.length);
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
            PSOSolution s = new PSOSolution(c);            
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p);
        // set best positions and initial velocities
        for(Solution ss : pop)
        {
            PSOSolution s = (PSOSolution) ss;
            s.prepare(p);
        }        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // reproduce
            LinkedList<Solution> children = generateChildren(pop, p, bestEver);
            // evaluate
            p.cost(children);
            // update pbest positions
            updatePersonalBestPositions(children, pop, p);
            // replace
            pop = children;
            // test
            bestEver = PopulationAlgorithmUtils.getBest(pop, p); 
        }
        
        return bestEver;
    }
    
    protected void updatePersonalBestPositions(LinkedList<Solution> children, LinkedList<Solution> pop, Problem p)
    {
        for (int i = 0; i < pop.size(); i++)
        {
            PSOSolution parent = (PSOSolution) pop.get(i);
            PSOSolution child = (PSOSolution) children.get(i);
            // copy velocity
            System.arraycopy(parent.velocity, 0, child.velocity, 0, parent.velocity.length);
            // update personal best
            if (p.isBetter(child, parent))
            {
                // take new position as personal best
                child.pbestScore = child.getScore();
                System.arraycopy(child.getCoordinate(), 0, child.pbestcoord, 0, child.pbestcoord.length);
            }
            else
            {
                // take parents p-best coord and p-best score
                System.arraycopy(parent.pbestcoord, 0, child.pbestcoord, 0, parent.pbestcoord.length);
                child.pbestScore = parent.pbestScore;
            }
        }
    }
    
    protected LinkedList<Solution> generateChildren(LinkedList<Solution> pop, Problem p, Solution best)
    {        
        double [][] minmax = p.getMinmax();
        int T = pop.size();
        LinkedList<Solution> children = new LinkedList<Solution>();
        
        // create children one at a time
        // basically copy parents, thne update position and velocity        
        for (int k = 0; k < T; k++)
        {
            PSOSolution s = (PSOSolution) pop.get(k);
            double [] position = s.getCoordinate();
            double [] velocity = s.velocity;
            double [] pBestPos = s.pbestcoord;
            double [] bestPos = best.getCoordinate();
            
            // child's position is changed
            double [] childCoord = new double[position.length];
            System.arraycopy(position, 0, childCoord, 0, position.length);
            PSOSolution child = new PSOSolution(childCoord);
            children.add(child);
            
            // update velocity
            for (int i = 0; i < velocity.length; i++)
            {
                // update velocity
                velocity[i] = 
                  (momentum*velocity[i]) + // how much of the previous velocity
                  (1-momentum)* 
                  (
                  + (c1 * r.nextDouble() * (pBestPos[i] - position[i])) 
                  + (c2 * r.nextDouble() * (bestPos[i] - position[i]))
                  );
                
                // bound velocity
                double v = (minmax[i][1] - minmax[i][0]) * vMax;
                if(velocity[i] > v)
                {
                    velocity[i] = v;
                }
                else if(velocity[i] < -v)
                {
                    velocity[i] = -v;
                }                
            }
            
            // update position
            for (int i = 0; i < childCoord.length; i++)
            {                
                // update position
                childCoord[i] = childCoord[i] + velocity[i];
                
                // wrap so at least velocity is still meaningful...hack
                PopulationAlgorithmUtils.wrapCoord(childCoord, p);
                
                //PopulationAlgorithmUtils.bounceCoord(childCoord, p);
                // TODO - bounce velocity as well!!!
            }
        }
        
        return children;
    }
    
    

    @Override
    public String getName()
    {
        return "Particle Swarm Optimization (PSO)";
    }
    


    @Override
    protected JPanel getConfigurationPane()
    {
        seed = DEFAULT_SEED;
        popsize = DEFAULT_POPSIZE;
        vMax = DEFAULT_VMAX;
        c1 = DEFAULT_C1;
        c2 = DEFAULT_C2;
        momentum = DEFAULT_MOMENTUM;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel vmaxLabel = new JLabel("Maximum Velocity:");
        JLabel c1Label = new JLabel("c2:");
        JLabel c2Label = new JLabel("c1:");
        JLabel momentumLabel = new JLabel("Momentum:");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        vMaxField = new JTextField(Double.toString(vMax), 10);
        c1Field = new JTextField(Double.toString(c1), 10);
        c2Field = new JTextField(Double.toString(c2), 10);
        momentumField = new JTextField(Double.toString(momentum), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(vmaxLabel);
        labelPane.add(c1Label);
        labelPane.add(c2Label);
        labelPane.add(momentumLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(vMaxField);
        fieldPane.add(c1Field);
        fieldPane.add(c2Field);
        fieldPane.add(momentumField);

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
        // vmax
        try
        {
            vMax = Double.parseDouble(vMaxField.getText());
        }
        catch (Exception e)
        {
            vMax = DEFAULT_VMAX;
            vMaxField.setText("" + vMax);
        }
        finally
        {
            if(vMax>1 || vMax<0)
            {
                vMax = DEFAULT_VMAX;
                vMaxField.setText("" + vMax);
            }
        }
        // momentum
        try
        {
            momentum = Double.parseDouble(momentumField.getText());
        }
        catch (Exception e)
        {
            momentum = DEFAULT_MOMENTUM;
            momentumField.setText("" + momentum);
        }
        finally
        {
            if(momentum>1 || momentum<0)
            {
                momentum = DEFAULT_MOMENTUM;
                momentumField.setText("" + momentum);
            }
        }
        
        // c1
        try
        {
            c1 = Double.parseDouble(c1Field.getText());
        }
        catch (Exception e)
        {
            c1 = DEFAULT_C1;
            c1Field.setText("" + c1);
        }
        // c2
        try
        {
            c2 = Double.parseDouble(c2Field.getText());
        }
        catch (Exception e)
        {
            c2 = DEFAULT_C2;
            c2Field.setText("" + c2);
        }
    }
}
