
package jb.selfregulation.processes.work;

import java.util.Arrays;
import java.util.LinkedList;

import jb.selfregulation.Cell;
import jb.selfregulation.Constants;
import jb.selfregulation.Lattice;
import jb.selfregulation.application.SystemState;



/**
 * Type: AdaptiveExpansion<br/>
 * Date: 23/09/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class AdaptiveExpansion extends ProcessExpansion
{       
    protected Entry [] stimulationHistory;
    protected int totalLocalities;
    protected boolean preapred;
    protected int lastRelativeSelection;
    
    
    protected class Entry
    {
        public double score;
        public long selectTime;
    }
  

    
    
    public void setup(SystemState aState)
    {
        super.setup(aState);
        totalLocalities = aState.lattice.getInitialTotalCells();
    }
    
    protected void prepareForSelection(int totalLoc)
    {
        if(totalLoc != totalLocalities)
        {
            throw new RuntimeException("Adaptive stimulation does not support a varing number of localities.");
        }
        else if(!preapred)
        {
            preapred = true;
            if(numPartitions == 1)
            {
                stimulationHistory = new Entry[totalLoc];
            }
            else
            {
                int partitionSize = (totalLoc / numPartitions);
                stimulationHistory = new Entry[partitionSize];
            }
        }
    }
    
    protected Cell selectCell(Lattice aLattice)
    {
        LinkedList<Cell> cells = aLattice.getCells();
        // preapre datastructures
        prepareForSelection(cells.size());
        // get the probabilities for localities
        double [] selectionProbabilities = prepareSelectionProbabilities();
        // make relative selection
        int relativeSelection = makeRelativeSelection(selectionProbabilities);
        // convert relative selection into absolute selection
        int absoluteSelection = makeAbsoluteSelection(relativeSelection);
        // get the selected cell        
        Cell selectedCell = cells.get(absoluteSelection);
        // remember relative selection so that history can be updated post-stimulation
        lastRelativeSelection = relativeSelection;
        return selectedCell;
    }
    

    
    
    protected void executeProcessRun(Lattice aLattice)
    {              
        // select a cell to operate on
        Cell selectedCell = selectCell(aLattice);
        // visit the cell and do the expansion
        selectedCell.visitCell(this, Constants.LOCK_WAIT_TIME); 
        
        // create an entry for the locality
        updateSelectionHistory(lastRelativeSelection, selectedCell);
        // update count on the cell
        selectedCell.updateStimulationCount();
    }
    
    
    
    protected void updateSelectionHistory(int relativeSelection, Cell aLocality)
    {
        // update history        
        if(stimulationHistory[relativeSelection] == null)
        {
            stimulationHistory[relativeSelection] = new Entry();
        }                
        stimulationHistory[relativeSelection].selectTime = numProcessIterations;
        stimulationHistory[relativeSelection].score = stimulation.calculateMeanScore(aLocality);
    }
    
    
    protected int makeAbsoluteSelection(int relativeSelection)
    {
        int absolute = -1;
        
        if(numPartitions == 1)
        {
            absolute = relativeSelection;
        }
        else
        {
            int partitionSize = (totalLocalities / numPartitions);
            absolute = relativeSelection + (partitionAllocation * partitionSize);
        }
        
        if(absolute < 0 || absolute >= totalLocalities)
        {
            throw new RuntimeException("Invalid relative -> absolute locality selection ["+relativeSelection+"] -> ["+absolute+"]");
        }
        
        return absolute;
    }
    
    protected int makeRelativeSelection(double [] selectionProbabilities)
    {
        int selectedIndex = 0; // TODO HEY this is bad bad bad. zero because i'm lazy and i want it to always make a decision even if bad probabilities come through!
        double sum = 0.0;
        double r = rand.nextDouble(); // make probabilistic selection
        double accumulatedSum = 0.0;
        
        // calculate the sum
        for (int i = 0; i < selectionProbabilities.length; i++)
        {
            sum += selectionProbabilities[i];
        }
        // make the selection
        for (int i = 0; i < selectionProbabilities.length; i++)
        {
            accumulatedSum += (selectionProbabilities[i] / sum);
            // check if a selection has been made
            if(accumulatedSum > r)
            {
                selectedIndex = i;
                break;
            }
        }
        
        if(selectedIndex < 0)
        {
            throw new RuntimeException("Selected index was -1. "+ Arrays.toString(selectionProbabilities)+", sum["+sum+"].");
        }
        
        return selectedIndex;
    }
    
    
    /**
     * Same as prepareSelectionProbabilities(), except it returns
     * probabilities for all localities (if partitioned). useful
     * for visualisation 
     * 
     * @return
     */
    public double [] getAbsoluteSelectionProbabilities()
    {
        double [] relativeProbabilities = prepareSelectionProbabilities();
        
        // check for un partitioned case
        if(numPartitions == 1)
        {
            return relativeProbabilities;
        }
        
        // copy the probabilities into a new absolute locality map
        double [] probabilities = new double[totalLocalities];
        int partitionSize = (totalLocalities / numPartitions);
        int offset = (partitionSize * partitionAllocation);
        for (int i = offset, j=0; j < relativeProbabilities.length; i++, j++)
        {
            probabilities[i] = relativeProbabilities[j];
        }
        
        return probabilities;
    }
    
    
    /**
     * Calculate the probabilities of each locality
     * 
     * Simple rank based approach, probability is 1.0 if the locality 
     * has not been selected before.
     * All locality scores are made positive before calculating rank
     * based probability.
     * 
     * @return
     */
    protected double [] prepareSelectionProbabilities()
    {
        double [] selectionList = new double[stimulationHistory.length];        
        
        // locate the best entry
        Entry highest = locateHighestScoringLocality();
        // locate lowest scoring
        Entry lowest = locateLowestScoringLocality();
        // preapre the offset (if needed)
        double offset = (lowest!=null && lowest.score<0) ? Math.abs(lowest.score) : 0.0;

        // process list
        for (int i = 0; i < selectionList.length; i++)
        {
            // check for the case where no units were selected
            // or the locality has not been selected before
            if(highest == null || Double.isNaN(highest.score) || stimulationHistory[i] == null)
            {
                selectionList[i] = 1.0; // always full probability
            }
            // check for the case where there was nothing in the the locality 
            // that could be evaluated (last time)
            else if(Double.isNaN(stimulationHistory[i].score))
            {
                selectionList[i] = 1.0; // always full probability
            }
            // check for a zero best
            else if(/*highest.meanScore == 0.0 && offset == 0 ||*/ (highest.score+offset==0))                
            {
                // in this case, the best is negative and is currently being evaluated
                // thus a 0/0 will be attempted
                selectionList[i] = 1.0; // always full probability
            }
            // check for minimisation
            else if(stimulation.isMinimisation())
            {
                // divide by worst (highest) score
                selectionList[i] = (stimulationHistory[i].score+offset) / (highest.score+offset);
                // invert
                selectionList[i] = 1.0 - selectionList[i];
            }
            else
            {
                // divide by best (highest) score
                selectionList[i] = (stimulationHistory[i].score+offset) / (highest.score+offset);
            }
                
            // safety
            if(selectionList[i] > 1 || selectionList[i] < 0)
            {
                throw new RuntimeException("Normalised mean quality is out of the range [0,1]: " + selectionList[i]);
            }
            // safety
            if(Double.isNaN(selectionList[i]) || Double.isInfinite(selectionList[i]))
            {
                throw new RuntimeException("Invalid probability: " + selectionList[i]);                    
            }            
        }
        
        return selectionList;
    }
    
    
    protected Entry locateLowestScoringLocality()
    {
        Entry best = null;
        
        for (int i = 0; i < stimulationHistory.length; i++)
        {
            if(stimulationHistory[i] != null)
            {
                if(best == null)
                {
                    best = stimulationHistory[i];
                }
                else if(stimulationHistory[i].score < best.score)
                {
                    best = stimulationHistory[i];
                }
            }
        }

        return best;
    }
    
    protected Entry locateHighestScoringLocality()
    {
        Entry best = null;       
       
        for (int i = 0; i < stimulationHistory.length; i++)
        {
            if(stimulationHistory[i] != null)
            {
                if(best == null)
                {
                    best = stimulationHistory[i];
                }
                else if(stimulationHistory[i].score > best.score)
                {
                    best = stimulationHistory[i];
                }
            }
        }
        
        return best;
    }    
}
