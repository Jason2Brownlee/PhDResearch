
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
 * Type: MaxMinAntSystem<br/>
 * Date: 28/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class MaxMinAntSystem extends Algorithm
{
    public final static double DEFAULT_ALPHA = 1.0; 
    public final static double DEFAULT_BETA = 2.5;
    public final static double DEFAULT_RHO = 0.02;
    public final static int DEFAULT_TOTAL_ANTS = 100;
    public final static double DEFAULT_UPDATE_FACTOR = 0.5;
    
    protected JTextField seedField;
    protected JTextField alphaField;
    protected JTextField betaField;
    protected JTextField rhoField;
    protected JTextField totalAntsField;
    protected JTextField updateFactorField;
    
    protected Random r;
    protected long seed;
    
    protected double alpha; // history
    protected double beta; // heuristic
    protected double rho; // decay
    protected int totalAnts;    
    
    protected double updateSelectionFactor;
    protected double tmax;
    protected double tmin;
    
    protected double [][] pheromoneMatrix;
    
    @Override
    protected int getNumParameters()
    {
        return 6;
    }
    
    
    
    @Override
    public void initialise(Problem p)
    {   
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
        // updateSelectionFactor
        try
        {
            updateSelectionFactor = Double.parseDouble(updateFactorField.getText());
        }
        catch (Exception e)
        {
            updateSelectionFactor = DEFAULT_UPDATE_FACTOR;
            updateFactorField.setText("" + updateSelectionFactor);
        }
        finally
        {
            if(updateSelectionFactor<0||updateSelectionFactor>1)
            {
                updateSelectionFactor = DEFAULT_UPDATE_FACTOR;
                updateFactorField.setText("" + updateSelectionFactor);
            }
        }
    }
    
    @Override
    protected JPanel getConfigurationPane()
    {        
        // defaults
        seed = System.currentTimeMillis();
        alpha = DEFAULT_ALPHA;
        beta = DEFAULT_BETA;
        rho = DEFAULT_RHO;
        totalAnts = DEFAULT_TOTAL_ANTS;   
        updateSelectionFactor = DEFAULT_UPDATE_FACTOR;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel alphaLabel = new JLabel("History exponent (alpha):");
        JLabel betaLabel = new JLabel("Heuristic exponent (beta):");
        JLabel rhoLabel = new JLabel("Decay factor (rho):");
        JLabel totalAntsLabel = new JLabel("Total ants:");
        JLabel updateFactorLabel = new JLabel("Update selection factor:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        alphaField = new JTextField(Double.toString(alpha), 10);
        betaField = new JTextField(Double.toString(beta), 10);
        rhoField = new JTextField(Double.toString(rho), 10);
        totalAntsField = new JTextField(Integer.toString(totalAnts), 10);
        updateFactorField = new JTextField(Double.toString(updateSelectionFactor), 10);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(alphaLabel);
        labelPane.add(betaLabel);
        labelPane.add(rhoLabel);
        labelPane.add(totalAntsLabel);
        labelPane.add(updateFactorLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(alphaField);
        fieldPane.add(betaField);
        fieldPane.add(rhoField);
        fieldPane.add(totalAntsField);
        fieldPane.add(updateFactorField);

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
        updatePheromone(ants, r);
        
        // main loop
        while(p.isReamainingEvaluations())
        {
            LinkedList<Solution> tmp = new LinkedList<Solution>(); 
            
            for (int i = 0; i < ants.size(); i++)
            {
                Integer start = ((Ant)ants.get(i)).getFirstCity();
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
                updatePheromone(ants, r);
            }
        }
        
        return bestEver;
    }    
    
    protected void updatePheromone(LinkedList<Solution> ants, Random r)
    {
        // decay
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            for (int j = 0; j < pheromoneMatrix[i].length; j++)
            {
                pheromoneMatrix[i][j] = (1.0 - rho) * pheromoneMatrix[i][j];
            }
        }
        
        // determine which type of update to do
        if(r.nextDouble() < updateSelectionFactor)
        {
            // update best ever
            ((Ant)bestEver).updatePheromone();
        }
        else
        {
            // update the iteration best
            // assumed already sorted
            ((Ant)ants.getFirst()).updatePheromone();
        }
    }
    
    protected void buildTours(LinkedList<Solution> ants, Problem p, Random r)
    {
        // built tours
        for(Solution s : ants)
        {
            Ant a = (Ant) s;
            while(!a.advance(p, r));
        }        
    }    

    protected void initialisePheromoneMatrix(Problem p, Solution nnSolution)
    {
        int totalCities = p.getTotalCities();
        pheromoneMatrix = new double[totalCities][totalCities];
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            for (int j = 0; j < pheromoneMatrix[i].length; j++)
            {
                pheromoneMatrix[i][j] = (1.0 / (rho*nnSolution.getScore()));
            }
        }
        
        // prepare pheromone bounds
        tmax = (1.0 / (rho*nnSolution.getScore()));                
        double root = Math.pow(0.05, 1/totalCities);
        double avg = (totalCities-1.0) / 2.0;
        tmin = tmax * (1.0-root) / ((avg-1.0) * root);
        
        // safety
//        if(tmin>=tmax)
//        {
//            throw new RuntimeException("Invalid tmin value: tmax["+tmax+"], tmin["+tmin+"]");
//        }
    }
    
    protected void boundPheromone(int x, int y)
    {
        if(pheromoneMatrix[x][y] > tmax)
        {
            pheromoneMatrix[x][y] = tmax;
        }
        if(pheromoneMatrix[x][y] < tmin)
        {
            pheromoneMatrix[x][y] = tmin;
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
            double sum = 0.0;
            for (int i = 0; i < probabilities.length; i++)
            {
                if(!currentTour.contains(new Integer(i)))
                {
                    double history = Math.pow(pheromoneMatrix[lastCity.intValue()][i], alpha);
                    double heuristic = Math.pow(1.0/distanceMatrix[lastCity.intValue()][i], beta);
                    probabilities[i] = history * heuristic;
                    sum += probabilities[i];
                }
                else
                {
                    probabilities[i] = 0.0; // explicitly zero for readability
                }
            }
            // normalise probabilities
            for (int i = 0; i < probabilities.length; i++)
            {
                probabilities[i] /= sum;
                // being real safe
//                if(probabilities[i] < 0 || probabilities[i] > 1)
//                {
//                    throw new RuntimeException("Preapred invalid selection probability!");
//                }
            }            
            int selection = makeSelection(probabilities, r);            
            lastCity = new Integer(selection);
//            if(currentTour.contains(lastCity))
//            {
//                throw new RuntimeException("Stepwise construction prepared an invalid solution!");
//            }
            currentTour.add(lastCity);
            
            if(currentTour.size() == p.getTotalCities())
            {
                buildPermutation(p);                
                return true;
            }
            
            return false;
        }
        
        protected int makeSelection(double [] prob, Random r)
        {
            double sum = 0.0;
            for (int i = 0; i < prob.length; i++)
            {
                sum += prob[i];
            }
            int selection = -1;
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
        
        
        public void updatePheromone(double factor)
        {
            double d = factor / score;
            
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
                pheromoneMatrix[x][y] += d;
                pheromoneMatrix[y][x] += d;
                boundPheromone(x,y);                
                boundPheromone(y,x);
            }
        }
        
        public void updatePheromone()
        {
            updatePheromone(1.0); // default
        }
        
        public Integer getFirstCity()
        {
            return firstCity;
        }
    }    

    @Override
    public String getName()
    {
        return "Max-Min Ant System (MMAS)";
    }
}

