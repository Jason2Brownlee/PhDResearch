
package jb.selfregulation.expansion.proliforation;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import jb.selfregulation.Cell;
import jb.selfregulation.Unit;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.expansion.stimulation.StimulationStrategy;


/**
 * Type: ProgenyGA<br/>
 * Date: 8/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public abstract class ProgenyGA extends ProgenyStrategy
{
    protected Random rand;
    protected double crossoverPercentage;
    protected double mutationPercentage;
    
    
    public String getBase()
    {
        return super.getBase() + ".ga";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        super.loadConfig(aBase, prop);
        String b = aBase + super.getBase() + ".ga";
        crossoverPercentage = Double.parseDouble(prop.getProperty(b + ".crossover"));
        mutationPercentage = Double.parseDouble(prop.getProperty(b + ".mutation"));
    }
    public void setup(SystemState aState)
    {
        super.setup(aState);
        rand = aState.rand;
    }
    
    
    
    

    @Override
    protected LinkedList<Unit> generateProgeny(
            Cell aCell, 
            LinkedList<Unit> selected, 
            StimulationStrategy aStimulationStrategy)
    {
//        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        // check for nothing selected
        if(selected.isEmpty())
        {
            return new LinkedList<Unit>();
        }
        else if(selected.size() < 2)
        {
            throw new RuntimeException("Unsupported number of selected units, must >= 2, " + selected.size());
        }
        
        // total parents must be even
        int totalParents = ((totalProgeny%2)==0) ? totalProgeny : totalProgeny+1;
        Unit [] parents = null;
        // correct size
        if(selected.size() == totalParents)
        {
            parents = selected.toArray(new Unit[selected.size()]);
        }
        // too many
        else if(selected.size() > totalParents)
        {
            parents = new Unit[totalParents];
            for (int i = 0; i < parents.length; i++)
            {
                parents[i] = selected.get(i);
            }
        }
        // too few
        else
        {
            parents = new Unit[totalParents];
            for (int i = 0; i < parents.length; i++)
            {
                if(i < selected.size())
                {
                    parents[i] = selected.get(i);
                }
                else
                {
                    // add a random selection
                    parents[i] = selected.get(rand.nextInt(selected.size()));
                }
            }
        }       
        
        return reproduce(parents);
        
        // create progeny using crossover
//        for(int i=0; i<parents.length; i+=2)
//        {
//            // create children
//            Unit [] children = crossover(parents[i], parents[i+1]);
//            // add to list until the list is full or all children are added
//            for (int j = 0; progeny.size()<totalProgeny && j < children.length; j++)
//            {
//                mutate(children[j]); // mutate
//                progeny.add(children[j]); // add
//            }
//        }
//
//        return progeny;
    }
    
    
    public LinkedList<Unit> reproduce(Unit [] parents)
    {
        LinkedList<Unit> progeny = new LinkedList<Unit>();
        
        if((parents.length%2)!=0)
        {
            throw new RuntimeException("Total parents must be even steven!");
        }
        
        // create progeny using crossover
        for(int i=0; i<parents.length; i+=2)
        {
            // create children
            Unit [] children = crossover(parents[i], parents[i+1]);
            // add to list until the list is full or all children are added
            for (int j = 0; progeny.size()<totalProgeny && j < children.length; j++)
            {
                mutate(children[j]); // mutate
                progeny.add(children[j]); // add
            }
        }
        
        return progeny;
    }
    
    
    protected abstract Unit [] crossover(Unit p1, Unit p2);
    protected abstract void mutate(Unit aUnit);
    
    
    

    public double getCrossoverPercentage()
    {
        return crossoverPercentage;
    }

    public void setCrossoverPercentage(double crossoverPercentage)
    {
        this.crossoverPercentage = crossoverPercentage;
    }

    public double getMutationPercentage()
    {
        return mutationPercentage;
    }

    public void setMutationPercentage(double mutationPercentage)
    {
        this.mutationPercentage = mutationPercentage;
    }
    public Random getRand()
    {
        return rand;
    }
    public void setRand(Random rand)
    {
        this.rand = rand;
    }
    
}
