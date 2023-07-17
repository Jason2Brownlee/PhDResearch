
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.GAUtils;
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
    public final static int DEFAULT_POPSIZE = 100;
    public final static double DEFAULT_VMAX = 1.0;
    public final static double DEFAULT_C1 = 2.0;
    public final static double DEFAULT_C2 = 2.0;
    public final static double DEFAULT_MOMENTUM = 0.5;
    public final static int DEFAULT_NEIGHBOURHOOD_SIZE = 20;
    
    protected JTextField seedField;
    protected JTextField popsizeField;
    protected JTextField vMaxField;
    protected JTextField c1Field;
    protected JTextField c2Field;
    protected JTextField momentumField;
    protected JTextField neighbourhoodField;
    
    protected Random r;   
    protected DistanceComparator comparator;
    
    protected long seed;
    protected int popsize;
    protected double vMax;
    protected double c1;
    protected double c2;    
    protected double momentum;
    protected int neighbourhoodSize;
    
    
    public ParticleSwarmOptimization()
    {
        comparator = new DistanceComparator();
    }
    
    
    protected class PSOSolution extends Solution
    {
        public double [] velocity;        
        public double [] pbestcoord;
        public double pbestScore;
        public double distance;
        
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
    public Solution executeAlgorithm(Problem p)
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
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
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
            // at the end because it sorts and pop order is important
            bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
            notifyListeners(p,pop,bestEver);
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
    
    protected LinkedList<Solution> getNeighbours(
            LinkedList<Solution> realpop, Solution self)
    {
        LinkedList<Solution> neighbours = new LinkedList<Solution>();
        // do not want to mess up order        
        LinkedList<Solution> pop = (LinkedList<Solution>) realpop.clone(); 
        
        // calculate distances
        for(Solution s : pop)
        {
            if(s != self)
            {
                ((PSOSolution)s).distance = GAUtils.euclideanDistance(s, self);                
            }
        }
        // sort by distance asc
        Collections.sort(pop, comparator);
        // get the best n
        int i = 0;
        do
        {
            neighbours.add(pop.get(i++));
        }
        while(neighbours.size() < neighbourhoodSize);
        
        return neighbours;
    }
    
    protected class DistanceComparator implements Comparator<Solution>
    {
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         */
        public int compare(Solution o1, Solution o2)
        {
            PSOSolution p1 = (PSOSolution) o1;
            PSOSolution p2 = (PSOSolution) o2;
            
            if(p1.distance < p2.distance)
            {
                return -1;
            }
            else if(p1.distance > p2.distance)
            {
                return +1;
            }
            return 0; // same
        }
        
    }
    
    protected double [] getBestCoord(
            LinkedList<Solution> pop,
            Solution best, 
            PSOSolution self, 
            Problem p)
    {
        double [] bestPos = null;
        
        // check for gbest
        if(neighbourhoodSize == popsize)
        {
            bestPos = best.getCoordinate();
        }
        // best
        else
        {
            // get neighbours
            LinkedList<Solution> neighbours = getNeighbours(pop, self);
            // locate the best neighbourhood pbestpos
            // has to be better than own pbestscore
            double score = self.pbestScore;
            bestPos = self.pbestcoord;
            for(Solution s : neighbours)
            {
                PSOSolution p1 = (PSOSolution) s;                
                if(p.isBetter(p1.pbestScore, score))
                {
                    score = p1.pbestScore;
                    bestPos = p1.pbestcoord;
                }
            }
        }
        
        return bestPos;
    }
    
    protected LinkedList<Solution> generateChildren(
            LinkedList<Solution> pop, Problem p, Solution best)
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
            double [] bestPos = getBestCoord(pop, best, s, p);
            
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
            }            
            // ensure particle is within the problem bounds
            boundPosition(childCoord, velocity, minmax, p.isToroidal());            
        }
        
        return children;
    }
    
    
    protected void boundPosition(
            double [] childCoord, 
            double [] velocity, 
            double [][] minmax, 
            boolean isToroidal)    
    {
        // bounce off problem bounds
        // doing it here instead of in PopulationAlgorithmUtils
        // because of velocity reflection!!!!
        for (int i = 0; i < childCoord.length; i++)
        {            
            // a bounce could bounce beyond the opposite end of the domain
            while(childCoord[i] > minmax[i][1] || childCoord[i] < minmax[i][0])
            {                
                // too large
                while(childCoord[i] > minmax[i][1])
                {
                    if(isToroidal)
                    {
                        childCoord[i] -= minmax[i][1]; // wrap
                    }
                    else
                    {
                        // subtract the difference
                        double diff = Math.abs(childCoord[i] - minmax[i][1]);
                        // always smaller
                        childCoord[i] = (minmax[i][1] - diff);
                    }
                    // invert velocity            
                    velocity[i] *= -1.0;
                }
                // too small
                while(childCoord[i] < minmax[i][0])
                {  
                    if(isToroidal)
                    {
                        childCoord[i] += minmax[i][1]; // wrap
                    }
                    else
                    {
                        double diff = Math.abs(childCoord[i] - minmax[i][0]);
                        // always larger
                        childCoord[i] = (minmax[i][0] + diff);
                    }
                    // invert velocity            
                    velocity[i] *= -1.0;
                } 
            }
        }
    }
    
    

    @Override
    public String getName()
    {
        return "Particle Swarm Optimization (PSO)";
    }
    


    @Override
    protected JPanel getConfigurationPane()
    {
        seed = System.currentTimeMillis();
        popsize = DEFAULT_POPSIZE;
        vMax = DEFAULT_VMAX;
        c1 = DEFAULT_C1;
        c2 = DEFAULT_C2;
        momentum = DEFAULT_MOMENTUM;
        neighbourhoodSize = DEFAULT_NEIGHBOURHOOD_SIZE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel vmaxLabel = new JLabel("Maximum Velocity:");
        JLabel c1Label = new JLabel("c2:");
        JLabel c2Label = new JLabel("c1:");
        JLabel momentumLabel = new JLabel("Momentum:");
        JLabel neighbourhoodmLabel = new JLabel("Neighbourhood size:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        vMaxField = new JTextField(Double.toString(vMax), 10);
        c1Field = new JTextField(Double.toString(c1), 10);
        c2Field = new JTextField(Double.toString(c2), 10);
        momentumField = new JTextField(Double.toString(momentum), 10);
        neighbourhoodField = new JTextField(Integer.toString(neighbourhoodSize), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(vmaxLabel);
        labelPane.add(c1Label);
        labelPane.add(c2Label);
        labelPane.add(momentumLabel);
        labelPane.add(neighbourhoodmLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(popsizeField);
        fieldPane.add(vMaxField);
        fieldPane.add(c1Field);
        fieldPane.add(c2Field);
        fieldPane.add(momentumField);
        fieldPane.add(neighbourhoodField);

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
            if(popsize>p.getMaxEvaluations() || popsize<=0)
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
        
        // neighbourhoodSize
        try
        {
            neighbourhoodSize = Integer.parseInt(neighbourhoodField.getText());
        }
        catch (Exception e)
        {
            neighbourhoodSize = DEFAULT_NEIGHBOURHOOD_SIZE;
            neighbourhoodField.setText("" + neighbourhoodSize);
        }
        finally
        {
            // must be less than or equal to the popsize and non zero
            if(neighbourhoodSize>popsize || neighbourhoodSize<=0)
            {
                neighbourhoodSize = DEFAULT_NEIGHBOURHOOD_SIZE;
                neighbourhoodField.setText("" + neighbourhoodSize);
            }
        }
    }
}
