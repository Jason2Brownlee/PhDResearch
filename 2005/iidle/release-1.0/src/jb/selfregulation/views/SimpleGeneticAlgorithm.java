
package jb.selfregulation.views;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import jb.selfregulation.Unit;
import jb.selfregulation.expansion.selection.SelectionTournament;
import jb.selfregulation.impl.functopt.expansion.progeny.ProgenyFuncOptGA;
import jb.selfregulation.impl.functopt.expansion.stimulation.StimulationFunctionEvaluation;
import jb.selfregulation.impl.functopt.problem.Function;
import jb.selfregulation.impl.functopt.units.FuncOptUnit;
import jb.selfregulation.impl.functopt.units.FuncOptUnitFactory;

/**
 * Type: SimpleGeneticAlgorithm<br/>
 * Date: 21/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class SimpleGeneticAlgorithm
    implements Comparator<Unit>
{
    protected int convergenceWindowSize;
    protected int populationSize;
    protected double crossoverPercentage;
    protected double mutationPercentage;
    protected int totalElites;
    protected int tournamentSize;

    public SimpleGeneticAlgorithm()
    {
        // defaults
        convergenceWindowSize = 50;
        populationSize = 100;
        crossoverPercentage = 0.95;
        mutationPercentage = 0.01;
        totalElites = 2;
        tournamentSize = 3;
    }
    
    protected FuncOptUnitFactory getFactory(Function  p, Random r)
    {
        FuncOptUnitFactory factory = new FuncOptUnitFactory();
        factory.setRand(r);
        factory.setBitStringLength(p.getTotalBits());
        factory.setTotalParameters(p.getNumDimensions());
        factory.setProblem(p);
        return factory;
    }
    protected ProgenyFuncOptGA getProgenyGA(FuncOptUnitFactory f, Random r)
    {
        ProgenyFuncOptGA progeny = new ProgenyFuncOptGA();
        progeny.setUnitFactory(f);
        progeny.setTotalProgeny(populationSize);
        progeny.setRand(r);
        progeny.setCrossoverPercentage(crossoverPercentage);
        progeny.setMutationPercentage(mutationPercentage);
        return progeny;
    }
    protected SelectionTournament getSelection(Random r)
    {
        SelectionTournament selection = new SelectionTournament();
        selection.setRand(r);
        selection.setNumToSelect(populationSize);
        selection.setTournamentSize(tournamentSize);
        return selection;
    }
    
    protected StimulationFunctionEvaluation getStimulation(Function p)
    {
        StimulationFunctionEvaluation stimulation = new StimulationFunctionEvaluation();
        stimulation.setFunction(p);
        stimulation.setId(new Long(1));
        stimulation.setMinimise(p.isMinimisation());
        return stimulation;
    }    
    
    public int compare(Unit o1, Unit o2)
    {
        FuncOptUnit f1 = (FuncOptUnit) o1;
        FuncOptUnit f2 = (FuncOptUnit) o2;
    
        // ascending
        if(f1.getFunctionEvaluation() < f2.getFunctionEvaluation())
        {
            return -1;
        }
        else if(f1.getFunctionEvaluation() > f2.getFunctionEvaluation())
        {
            return +1;
        }
        
        return 0;
    }

    public FuncOptUnit getBest(LinkedList<Unit> pop, Function p)
    {
        FuncOptUnit best = (FuncOptUnit) pop.getFirst();
        
        for(Unit u1 : pop)
        {
            FuncOptUnit u = (FuncOptUnit) u1;
            
            if(isBetter(u, best, p))
            {
                best = u;
            }
        }
        
        return best;
    }
    
    public boolean isBetter(FuncOptUnit f1, FuncOptUnit f2, Function p)
    {
        if(p.isMinimisation())
        {
            if(f1.getFunctionEvaluation() < f2.getFunctionEvaluation())
            {
                return true;
            }
            return false;
        }

        if(f1.getFunctionEvaluation() > f2.getFunctionEvaluation())
        {
            return true;
        }
        return false;            
    }
    
    
    public Result runGA(Function p, Random r)
    {
        FuncOptUnitFactory factory = getFactory(p,r);
        ProgenyFuncOptGA progeny = getProgenyGA(factory,r);
        SelectionTournament selection = getSelection(r);
        StimulationFunctionEvaluation stimulation = getStimulation(p);
        
        LinkedList<Unit> population = new LinkedList<Unit>();
        FuncOptUnit bestEver = null;
        double lastBest = (p.isMinimisation()) ? Double.MAX_VALUE : Double.MIN_VALUE;
        int iterations = 0;
        int count = 0;
        
        // initialise
        for (int i = 0; i < populationSize; i++)
        {
            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit();
            population.add(u);
            stimulation.stimulate(u);
        }
        bestEver = getBest(population, p);

        while(true)
        {
            iterations++;                
            // select
            LinkedList<Unit> selected = selection.performSelection(population, stimulation);            
            // reproduce
            LinkedList<Unit> children = progeny.reproduce(selected.toArray(new Unit[selected.size()]));
            // eval
            for (Unit u : children)
            {
                stimulation.stimulate(u);
            }
            // handle elite situation
            if(totalElites > 0)
            {
                // order the old population (ascending)
                Collections.sort(population, this);
                
                // make room for elietes
                for (int i = 0; i < totalElites; i++)
                {
                    children.remove(Math.abs(r.nextInt()%children.size()));
                }
                // add elites
                for (int i = 0; i < totalElites; i++)
                {
                    if(p.isMinimisation())
                    {
                        // take first (remember it is sorted ascending)
                        children.add(population.get(i));
                    }
                    else
                    {
                        // take end (remember it is sorted ascending)
                        children.add(population.get((population.size()-1)-i));
                    }
                }                    
            }
            population = children;
            FuncOptUnit best = getBest(population, p);
            // check for new best ever
            if(isBetter(best, bestEver, p))
            {
                bestEver = best;
                count = 0;
            }
            else if(++count >= convergenceWindowSize)
            {
                break; // stop the search - we have converged
            }
        }
        
        return prepareResult(iterations, bestEver);
    }        
    
    protected Result prepareResult(int iterations,FuncOptUnit best)
    {
        Result result = new Result();
        result.iterations = iterations;
        result.best = best;
        result.bestScore = result.best.getFunctionEvaluation();
        return result;
    }

    public int getConvergenceWindowSize()
    {
        return convergenceWindowSize;
    }

    public void setConvergenceWindowSize(int convergenceWindowSize)
    {
        this.convergenceWindowSize = convergenceWindowSize;
    }

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

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }

    public int getTotalElites()
    {
        return totalElites;
    }

    public void setTotalElites(int totalElites)
    {
        this.totalElites = totalElites;
    }

    public int getTournamentSize()
    {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize)
    {
        this.tournamentSize = tournamentSize;
    }
    
    
}
