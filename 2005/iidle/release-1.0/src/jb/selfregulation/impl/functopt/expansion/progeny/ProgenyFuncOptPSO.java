
package jb.selfregulation.impl.functopt.expansion.progeny;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.proliforation.ProgenyStrategy;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;
import jb.selfregulation.impl.functopt.problem.BitStringCommonUtils;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;

/**
 * 
 * Type: ProgenyFuncOptPSO<br/>
 * Date: 22/09/2005<br/>
 * <br/>
 * Description: PSO, taken from http://www.swarmintelligence.org/tutorials.php
 * 
 * <br/>
 * @author Jason Brownlee
 */
public class ProgenyFuncOptPSO extends ProgenyStrategy
{    
    protected Random rand;   
    protected Function problem;
    protected double [][] minmax; 
    
    protected double vMax;
    protected double c1;
    protected double c2;    
    protected double momentum;
    
    public String getBase()
    {
        return super.getBase() + ".pso";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".pso";
        
        vMax = Double.parseDouble(prop.getProperty(b+".vmax"));
        c1 = Double.parseDouble(prop.getProperty(b+".c1"));
        c2 = Double.parseDouble(prop.getProperty(b+".c2"));
        momentum = Double.parseDouble(prop.getProperty(b+".momentum"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
        problem = (Function) aState.problem;
        minmax = problem.getGenotypeMinMax();
    }
    
    
   
    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected,
            StimulationStrategy aStimulationStrategy)
    {
        LinkedList<Unit> progeny = new LinkedList<Unit>();        
        
        // check if there is anything to do
        if(aCell.getTail().getUnits().isEmpty())
        {
            return progeny;
        }
        
        LinkedList<Unit> all = aCell.getTail().getUnits();
        
        // update personal best's
        updatePersonalBestPositions(all);
        
        // get global bset solution (in locality)
        FuncOptUnit globalBest = getBestQuality(all);
        
        // update all units, no progey are created
//        for(Unit u : all)
        for(Unit u : selected)
        {
            FuncOptUnit fou = (FuncOptUnit) u;
            
            FuncOptUnit child = (FuncOptUnit) unitFactory.generateNewUnit(fou);
            // duplicate vector
//            System.arraycopy(fou.getVectorData(), 0, child.getVectorData(), 0, child.getVectorData().length);
            // prepare best position
            child.setPersonalBest(fou.getFunctionEvaluation());
            child.setPersonalBestPos(child.getVectorData());
            child.setHasPersonalBest(true);            
            updateProgenyPosition(globalBest, child);
            progeny.add(child);
            
            // modified parents
//            updateProgenyPosition(globalBest, fou);
        }
        
        return progeny;
    }
    

    
    
    /**
     * All the work for the PSO
     * 
     * Update Velocity:
     * v[] = v[] + c1 * rand() * (pbest[] - present[]) + c2 * rand() * (gbest[] - present[])
     * 
     * Update Position
     * present[] = present[] + v[]
     * 
     * @param globalBest
     * @param child
     */
    protected void updateProgenyPosition(FuncOptUnit globalBest, FuncOptUnit child)
    {
        double [] position = child.getVectorData();
        double [] velocity = child.getVelocity();
        double [] pBestPos = child.getPersonalBestPos();
        double [] bestPos = globalBest.getPersonalBestPos();
        
        // update velocity
        for (int i = 0; i < velocity.length; i++)
        {
            // update velocity
            velocity[i] = (momentum*velocity[i]) + (1-momentum)*( 
                                   + (c1 * rand.nextDouble() * (pBestPos[i] - position[i])) 
                                   + (c2 * rand.nextDouble() * (bestPos[i] - position[i]))
                                   );
                        
            // set upper bound to velocity
            velocity[i] = (velocity[i] > vMax) ? vMax : velocity[i];
        }
        
        // update position
        for (int i = 0; i < position.length; i++)
        {
            // set position using new velocity
            position[i] = position[i] + velocity[i];
            // wrap position to a torid
//            while(position[i] < minmax[i][0])
//            {
//                position[i] = (minmax[i][1] - Math.abs(position[i]));
//            }            
//            while(position[i] > minmax[i][1])
//            {
//                position[i] = (minmax[i][0] + position[i]);
//            }
            
            // bounce particles
            if(position[i] < minmax[i][0])
            {
                position[i] = (position[i] - minmax[i][0]);
            }
            else if (position[i] > minmax[i][1])
            {
                position[i] = (position[i] - minmax[i][1]);
            }
        }       
        
        // set bits for current position
        boolean [] bitstring = BitStringCommonUtils.calculateBitString(position, minmax, child.getBitString().length);
        child.setBitString(bitstring);
    }
    
    
    protected void updatePersonalBestPositions(LinkedList<Unit> all)
    {
        for(Unit u : all)
        {
            FuncOptUnit fou = (FuncOptUnit) u;
            
            if(fou.isHasPersonalBest())
            {
                if(problem.isMinimisation())
                {
                    if(fou.getFunctionEvaluation() < fou.getPersonalBest())
                    {
                        fou.setPersonalBest(fou.getFunctionEvaluation());
                        fou.setPersonalBestPos(fou.getVectorData());
//                        System.out.println(" --> personal best updated");
                    }
                }
                else
                {
                    if(fou.getFunctionEvaluation() > fou.getPersonalBest())
                    {
                        fou.setPersonalBest(fou.getFunctionEvaluation());
                        fou.setPersonalBestPos(fou.getVectorData());
                    }
                }
            }
            else
            {                
                fou.setPersonalBest(fou.getFunctionEvaluation());
                fou.setPersonalBestPos(fou.getVectorData());
                fou.setHasPersonalBest(true);
            }
        }
    }
   
    protected FuncOptUnit getBestQuality(LinkedList<Unit> selected)
    {
        FuncOptUnit best = (FuncOptUnit) selected.get(0);
        
        for(Unit u : selected)
        {
            FuncOptUnit fou = (FuncOptUnit) u;
            
            // minimisation
            if(problem.isMinimisation())
            {
                if(fou.getPersonalBest() < best.getPersonalBest())
                {
                    best = fou;
                }
            }
            // maximisation
            else
            {
                if(fou.getPersonalBest() > best.getPersonalBest())
                {
                    best = fou;
                }
            }
        }
        
        return best;
    }
}
