
package jb.selfregulation.impl.tsp.stimulation.progeny;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyACO;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.tsp.problem.TSPProblem;
import jb.selfregulation.impl.tsp.units.TSPUnit;
import jb.selfregulation.impl.tsp.units.TSPUnitFactory;

/**
 * Type: ProgenyTSPACO<br/>
 * Date: 16/06/2005<br/>
 * <br/>
 * Description: Conventional ACO implementation
 * <br/>
 * <pre>
 * 1) Prepare heuristic matrix (from-to)
 *    - distance
 *    - inverted
 *    - normalised for each row
 * 2) Prepare history matrix (from-to)
 *    - initialise history with constant (so there are no zeros)
 *    - contribution = best score in tail / score (all in tour length) 
 *    - normalised for each row
 * 3) Combine
 *    - Use a set to keep track of cities that are impossible to visit
 *    - combine each element using: = heurist^exp X/+ history^exp
 * 4) Decision
 *    - generate random number
 *    - work through normalised combined units until decision is made
 * </pre>
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyTSPACO extends ProgenyACO
{    
    protected final static double INITAL_HISTORY_CONSTANT = 1.0;
    
    protected TSPProblem problem;
    protected double [][] heuristicMatrix;
    protected double heuristicExponent;    
    
    public String getBase()
    {
        return super.getBase() + ".tsp";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        
        String b = aBase + getBase();
        heuristicExponent = Double.parseDouble(prop.getProperty(b+".heuristic.exp"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        problem = (TSPProblem) aState.problem;        
        // prepare heuristic matrix
        prepareLocalHeuristicMatrix();
    }
    
    
    
    
    protected double getLowestTourLength(LinkedList<Unit> units)
    {
        double best = Double.MAX_VALUE;
        
        for(Unit u : units)
        {
            TSPUnit tspUnit = (TSPUnit) u;
            if(tspUnit.isHasTourLength())
            {
                if(tspUnit.getTourLength() < best)
                {
                    best = tspUnit.getTourLength();
                }
            }
        }
        
        return best;
    }
    
    protected double [][] prepareHistoryMatrix(
            LinkedList<Unit> units, 
            StimulationStrategy aStimulationStrategy)
    {
        double [][] history = new double[heuristicMatrix.length][heuristicMatrix[0].length];
        // set a default history
        for (int i = 0; i < history.length; i++)
        {
            Arrays.fill(history[i], INITAL_HISTORY_CONSTANT);
        }
        // determine the best score
        double lowestTourLength = getLowestTourLength(units);
        // accumulate history
        for(Unit u : units)
        {
            TSPUnit tspUnit = (TSPUnit) u;
            if(tspUnit.isHasTourLength())
            {
                // determine contribution
                double quality = (lowestTourLength / tspUnit.getTourLength());
                // make contribution
                addUnitContribution(tspUnit, history, quality);
            }
        }
        
        // normalise each positions city column
        for (int x = 0; x < history.length; x++)
        {
            normaliseColumn(history[x]);
        }
        
        return history;
    }
    
    protected void addUnitContribution(
            TSPUnit aUnit, 
            double [][] aHistory, 
            double contribution)
    {
        int [] tour = aUnit.getData();
        
        // process forwards
        for (int i = 0; i < tour.length; i++)
        {
            int from = tour[i];
            int to = -1;
            if(i == tour.length-1)
            {
                to = tour[0];
            }
            else
            {
                to = tour[i+1];
            }
            
            aHistory[from][to] += contribution;
        }
        
        // process backwards
        for (int i = tour.length-1; i >=0; i--)
        {
            int from = tour[i];
            int to = -1;
            if(i == 0)
            {
                to = tour[tour.length-1];
            }
            else
            {
                to = tour[i-1];
            }
            
            aHistory[from][to] += contribution;
        }
    }

    @Override
    protected Unit generateChild(double[][] aHistory)
    {
//        System.out.println(totalProgeny);
        
        int [] data = new int[aHistory.length];
        HashSet<Integer> set = new HashSet<Integer>(data.length);
        
        // select a randoms starting point
        data[0] = rand.nextInt(aHistory.length);
        set.add(data[0]);
        
        // process each position
        for (int i = 1; i < data.length; i++)
        {            
            data[i] = makeDecision(data[i-1], aHistory, set);
            // remember the decision
            set.add(data[i]);
        }
        
        // verify the permutation
//        problem.checkSafety(data);
        // create unit
        Unit unit = ((TSPUnitFactory)unitFactory).generateNewUnit(data);
        return unit;
    }
    
    
    protected int makeDecision(
            int currentCity,
            double[][] aHistory, 
            HashSet<Integer> set)
    {
        // get the combined decision
        double [] combinedColumn = getCombinedColumn(currentCity, aHistory, set);        
        // calculate the sum
        double sum = 0.0;
        for (int i = 0; i < combinedColumn.length; i++)
        {
            sum += combinedColumn[i];
        }
        // make random selection
        double r = rand.nextDouble();
        // process the column until we discover the decision
        int decision = -1;
        double accumulated = 0.0;
        int index = 0;
        while(accumulated < r)
        {
            decision = index; // current city
            accumulated += (combinedColumn[index++] / sum);            
        } 
        
        return decision;
    }

    protected double [] getCombinedColumn(
            int currentCity, 
            double [][] history, 
            HashSet<Integer> set)
    {
        double [] column = new double[history.length];
        
        // step down column through all citites
        for (int i = 0; i < column.length; i++)
        {
            // check if already used
            if(set.contains(i))
            {
                column[i] = IMPOSSIBLE;
            }
            else
            {
                double hist = Math.pow(history[currentCity][i], historyExponent);
                double heur = Math.pow(heuristicMatrix[currentCity][i], heuristicExponent);
                
                if(multiply)
                {
                    column[i] = (hist * heur);
                }
                else
                {
                    column[i] = (hist + heur);
                }
            }
        }
        
        // normalise the column
        normaliseColumn(column);
        
        return column;
    }
    
    
    protected void prepareLocalHeuristicMatrix()
    {
        int totalCitites = problem.getCities().length;
        heuristicMatrix = new double[totalCitites][totalCitites];
        double [][] distanceMatrix = problem.getDistanceMatrix();
        
        // process columns
        for (int x = 0; x < heuristicMatrix.length; x++)
        {               
            // process this column
            for (int y = 0; y < heuristicMatrix[x].length; y++)
            {
                // check for connection to self - impossible
                if(x == y)
                {
                    heuristicMatrix[x][y] = IMPOSSIBLE;
                }
                else
                {                        
                    // inverted distance
                    heuristicMatrix[x][y] = (1.0 / distanceMatrix[x][y]);
                }
            }
                
            // normalise the row
            normaliseColumn(heuristicMatrix[x]);
        }
    }

    public double getHeuristicExponent()
    {
        return heuristicExponent;
    }

    public void setHeuristicExponent(double heuristicExponent)
    {
        this.heuristicExponent = heuristicExponent;
    }
}
