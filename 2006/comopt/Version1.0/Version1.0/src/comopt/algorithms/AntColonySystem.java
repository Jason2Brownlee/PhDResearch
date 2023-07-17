
package comopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import comopt.Algorithm;
import comopt.Problem;
import comopt.Solution;
import comopt.algorithms.utils.AlgorithmUtils;

/**
 * Type: AntSystem<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AntColonySystem extends Algorithm
{
    public final static long DEFAULT_SEED = 1;
    public final static double DEFAULT_ALPHA = 1.0; 
    public final static double DEFAULT_BETA = 2;
    public final static double DEFAULT_RHO = 0.1;
    public final static int DEFAULT_TOTAL_ANTS = 10;
    public final static double DEFAULT_SIGMA = 0.1;
    public final static double DEFAULT_Q0 = 0.9;    
    
    protected JTextField seedField;
    protected JTextField alphaField;
    protected JTextField betaField;
    protected JTextField rhoField;
    protected JTextField totalAntsField;
    protected JTextField sigmaField;
    protected JTextField q0Field;
    
    protected Random r;
    
    protected long seed;    
    protected double alpha; // history
    protected double beta; // heuristic
    protected double rho; // decay
    protected int totalAnts; // param
    protected double sigma; // param
    protected double q0; // param
    protected double tau0;
    
    protected double [][] pheromoneMatrix;
    
    @Override
    protected int getNumParameters()
    {
        return 7;
    }
    
    
    
    @Override
    public void initialise(Problem p)
    {   
        try
        {
            seed = Long.parseLong(seedField.getText());
        }
        catch(Exception e)
        {
            seed = DEFAULT_SEED;
            seedField.setText(Long.toString(seed));
        }        
        r = new Random(seed);
        
        // alpha
        try
        {
            alpha = Double.parseDouble(alphaField.getText());
        }
        catch (Exception e)
        {
            alpha = DEFAULT_ALPHA;
            alphaField.setText("" + alpha);
        }
        // beta
        try
        {
            beta = Double.parseDouble(betaField.getText());
        }
        catch (Exception e)
        {
            beta = DEFAULT_BETA;
            betaField.setText("" + beta);
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
        // totalAnts
        try
        {
            totalAnts = Integer.parseInt(totalAntsField.getText());
        }
        catch (Exception e)
        {
            totalAnts = DEFAULT_TOTAL_ANTS;
            totalAntsField.setText("" + totalAnts);
        }
        finally
        {
            if(totalAnts>p.getMaxEvaluations()-1 || totalAnts<=0)
            {
                totalAnts = DEFAULT_TOTAL_ANTS;
                totalAntsField.setText("" + totalAnts);
            }
        }
        // sigma
        try
        {
            sigma = Double.parseDouble(sigmaField.getText());
        }
        catch (Exception e)
        {
            sigma = DEFAULT_SIGMA;
            sigmaField.setText("" + sigma);
        }
        finally
        {
            if(sigma<0||sigma>1)
            {
                sigma = DEFAULT_SIGMA;
                sigmaField.setText("" + sigma);
            }
        }
        // q0
        try
        {
            q0 = Double.parseDouble(q0Field.getText());
        }
        catch (Exception e)
        {
            q0 = DEFAULT_Q0;
            q0Field.setText("" + q0);
        }
        finally
        {
            if(q0<0||q0>1)
            {
                q0 = DEFAULT_Q0;
                q0Field.setText("" + q0);
            }
        }
    }
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = DEFAULT_SEED;
        alpha = DEFAULT_ALPHA;
        beta = DEFAULT_BETA;
        rho = DEFAULT_RHO;
        totalAnts = DEFAULT_TOTAL_ANTS; 
        sigma = DEFAULT_SIGMA;
        q0 = DEFAULT_Q0;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel alphaLabel = new JLabel("History exponent (alpha):");
        JLabel betaLabel = new JLabel("Heuristic exponent (beta):");
        JLabel rhoLabel = new JLabel("Global decay (rho):");
        JLabel totalAntsLabel = new JLabel("Total ants:");
        JLabel sigmaLabel = new JLabel("Local decay (sigma):");
        JLabel q0Label = new JLabel("Greediness (q0):");
        
        // fields
        seedField = new JTextField(Long.toString(seed), 10);
        alphaField = new JTextField(Double.toString(alpha), 10);
        betaField = new JTextField(Double.toString(beta), 10);
        rhoField = new JTextField(Double.toString(rho), 10);
        totalAntsField = new JTextField(Integer.toString(totalAnts), 10);
        sigmaField = new JTextField(Double.toString(sigma), 10);
        q0Field = new JTextField(Double.toString(q0), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(alphaLabel);
        labelPane.add(betaLabel);
        labelPane.add(rhoLabel);
        labelPane.add(totalAntsLabel);
        labelPane.add(sigmaLabel);
        labelPane.add(q0Label);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(alphaField);
        fieldPane.add(betaField);
        fieldPane.add(rhoField);
        fieldPane.add(totalAntsField);
        fieldPane.add(sigmaField);
        fieldPane.add(q0Field);

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
    protected Solution executeAlgorithm(Problem p)
    {        
        int totalCitites = p.getTotalCities();
        Solution nnSolution = AlgorithmUtils.generateNearestNeighbourSolution(p, r);
        p.cost(nnSolution);        
        // prepare the pheromone matrix
        initialisePheromoneMatrix(p, nnSolution);
        
        LinkedList<Solution> ants = new LinkedList<Solution>(); 
        while(ants.size() < totalAnts)
        {
            Integer start = new Integer(r.nextInt(totalCitites));
            Ant a = new Ant(start);
            ants.add(a);
        }
        // let the ant's build their tour's
        buildTours(ants, p, r);
        // evaluate
        p.cost(ants);
        bestEver = AlgorithmUtils.getBest(ants, p, bestEver);
        notifyListeners(p,ants,bestEver);
        // update pheromone
        updateGlobalPheromone(ants);
        
        // main loop
        while(p.isReamainingEvaluations())
        {
            LinkedList<Solution> tmp = new LinkedList<Solution>(); 
            
            for (int i = 0; i < ants.size(); i++)
            {
                Integer start = new Integer(r.nextInt(totalCitites));
                Ant a = new Ant(start);
                tmp.add(a);
            }
            ants = tmp;
            // let the ant's build their tour's
            buildTours(ants, p, r);
            // evaluate
            p.cost(ants);
            bestEver = AlgorithmUtils.getBest(ants, p, bestEver);
            notifyListeners(p,ants,bestEver);
            // only want to update pheromone if there is more tours to create
            if(p.isReamainingEvaluations())
            {
                // update pheromone
                updateGlobalPheromone(ants);
            }
        }
        
        return bestEver;
    }    
    
    protected void updateGlobalPheromone(LinkedList<Solution> ants)
    {
        // update pheromone for best solution found thus far
        ((Ant)bestEver).updateGlobalPheromone();
    }
    
    protected void buildTours(LinkedList<Solution> ants, Problem p, Random r)
    {
        // built tours - all ants will finish at the same time
        boolean canRun = true;
        do
        {
            // each ant gets a single advance
            for(Solution s : ants)
            {
                canRun = !((Ant)s).advance(p, r);
            }
        }
        while(canRun);
        
        // now update each ant's final arc - finish to start of the permutation
        for(Solution s : ants)
        {
            ((Ant)s).updateLocalPheromoneFinalArc();
        }
    }    

    protected void initialisePheromoneMatrix(Problem p, Solution nnSolution)
    {
        int totalCities = p.getTotalCities();
        pheromoneMatrix = new double[totalCities][totalCities];
        tau0 = (1.0 / totalCities*nnSolution.getScore());
        
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            for (int j = 0; j < pheromoneMatrix[i].length; j++)
            {
                pheromoneMatrix[i][j] = tau0;               
            }
        }
        
        
    }
    
    protected class Ant extends Solution
    {
        protected LinkedHashSet<Integer> currentTour;
        protected Integer firstCity;
        protected Integer lastCity;
        
        public Ant(Integer startingCity)
        {
            lastCity = firstCity = startingCity;
            currentTour = new LinkedHashSet<Integer>();
            currentTour.add(startingCity);
        }
        
        public boolean advance(Problem p, Random r)
        {
            // prepare probabilities
            double [][] distanceMatrix = p.getDistanceMatrix();
            double [] probabilities = new double[p.getTotalCities()];
            boolean greedy = (r.nextDouble() <= q0);
            double sum = 0.0;
            for (int i = 0; i < probabilities.length; i++)
            {
                if(!currentTour.contains(new Integer(i)))
                {
                    if(greedy)
                    {
                        double history = pheromoneMatrix[lastCity.intValue()][i];
                        double heuristic = Math.pow(1.0/distanceMatrix[lastCity.intValue()][i], beta);
                        probabilities[i] = history * heuristic;
                    }
                    else
                    {
                        double history = Math.pow(pheromoneMatrix[lastCity.intValue()][i], alpha);
                        double heuristic = Math.pow(1.0/distanceMatrix[lastCity.intValue()][i], beta);
                        probabilities[i] = history * heuristic;
                        sum += probabilities[i];
                    }
                }
                else
                {
                    probabilities[i] = 0.0; // explicitly zero for readability
                }
            }
            if(!greedy)
            {
                // normalise probabilities
                for (int i = 0; i < probabilities.length; i++)
                {
                    probabilities[i] /= sum;
                }        
            }
            
            int selection = makeSelection(probabilities, r, greedy);
            // update local pheromone
            int x = lastCity.intValue();
            int y = selection;
            updateLocalPheromone(x,y);
            
            lastCity = new Integer(selection);
            currentTour.add(lastCity);
            
            if(currentTour.size() == p.getTotalCities())
            {
                buildPermutation(p);
                return true;
            }
            
            return false;
        }
        

        
        protected int makeSelection(double [] prob, Random r, boolean greedy)
        {
            int selection = -1;  
        
            // check for a greedy decision
            if(greedy)
            {
                double max = Double.NEGATIVE_INFINITY;                
                for (int i = 0; i < prob.length; i++)
                {
                    if(prob[i] > max)
                    {
                        selection = i;
                        max = prob[i];
                    }
                }
            }
            else
            {
                double sum = 0.0;
                for (int i = 0; i < prob.length; i++)
                {
                    sum += prob[i];
                }
                
                double v = r.nextDouble();
                for (int i = 0; i < prob.length; i++)
                {
                    if(i == prob.length-1)
                    {
                        selection = i;
                        break;
                    }
                    
                    v -= (prob[i]/sum);
                    if(v <= 0)
                    {
                        selection = i;
                        break;
                    }
                }
            }
            return selection;
        }
        
        
        protected void buildPermutation(Problem p)
        {
            permutation = new int[p.getTotalCities()];
            int offset = 0;
            for (Iterator<Integer> iter = currentTour.iterator(); iter.hasNext();)
            {
                permutation[offset++] = iter.next().intValue();                
            }
        }
        
        public void updateLocalPheromoneFinalArc()        
        {            
            int x = permutation[0];
            int y = permutation[permutation.length-1];
            updateLocalPheromone(x,y);
        }
        
        public void updateLocalPheromone(int x, int y)
        {
            pheromoneMatrix[x][y] = (1.0-sigma)*pheromoneMatrix[x][y] + sigma*tau0;
            pheromoneMatrix[y][x] = (1.0-sigma)*pheromoneMatrix[y][x] + sigma*tau0;
        }
        
        public void updateGlobalPheromone()
        {
            double d = 1.0 / score; //delta
            
            for (int i = 0; i < permutation.length; i++)
            {
                int x = permutation[i];
                int y = -1;
                if(i == permutation.length-1)
                {
                    y = permutation[0]; // wrap
                }
                else
                {
                    y = permutation[i + 1];
                }
                
                pheromoneMatrix[x][y] = (1.0-rho)*pheromoneMatrix[x][y] + rho*d;
                pheromoneMatrix[y][x] = (1.0-rho)*pheromoneMatrix[y][x] + rho*d;
            }
        }
        
        public Integer getFirstCity()
        {
            return firstCity;
        }
    }
    

    @Override
    public String getName()
    {
        return "Ant Colony System (ACS)";
    }
}

