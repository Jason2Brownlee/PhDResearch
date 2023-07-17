
package jb.selfregulation.views.competition;

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
import jb.selfregulation.views.Result;

/**
 * Type: SimpleGeneticAlgorithm<br/>
 * Date: 21/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class GA
    implements Comparator<Unit>
{
    public final static boolean USE_PLOT = false;
    

    protected int populationSize;
    protected double crossoverPercentage;
    protected double mutationPercentage;
    protected int totalElites;
    protected int tournamentSize;
    
    protected int initialPopulationSize;

    public GA()
    {
        // defaults
        populationSize = 100;
        initialPopulationSize = 100;
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
            
            if(u.isHasFunctionEvaluation() && isBetter(u, best, p))
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
    
    
    public boolean isMaxEvaluations(CompetitionFunction f)
    {
        if(f.getTotalEvaluations() >= CompetitionFunction.MAX_ITERATIONS)
        {
            return true;
        }
        
        return false;
    }
    
    public int calculateRemainingEvaluations(CompetitionFunction f)
    {
        return CompetitionFunction.MAX_ITERATIONS - (int)f.getTotalEvaluations();
    }
    
    public boolean evaluate(LinkedList<Unit> pop, Function p, OnlineSamplePlot plot)
    {
        CompetitionFunction f = (CompetitionFunction) p;
        LinkedList<FuncOptUnit> eval = new LinkedList<FuncOptUnit>();        
        int remaining = calculateRemainingEvaluations(f);
        
        // build a list of all those units that can be evaluated 
        for (Unit aUnit : pop)
        {
            FuncOptUnit u = (FuncOptUnit) aUnit;
            
            // check if a re-evaluation is required
            if(!u.isHasFunctionEvaluation())
            {
                eval.add(u);
                
                // calculate genotype
                double [] genotype = p.calculateGenotype(u.getBitString());
                u.setVectorData(genotype);
            }
        }
        
        LinkedList<double []> v = new LinkedList<double []>();
        for (int i = 0; i < eval.size() && i<remaining; i++)
        {
            v.add(eval.get(i).getVectorData());
        }
        
        // evaluate
        double [] results = f.evaluate(v);
        // because results may be < the number of units we wanted evaluated
        for (int i = 0; i < results.length; i++)
        {
            FuncOptUnit u = eval.get(i);
            u.setFunctionEvaluation(results[i]);
            u.setHasFunctionEvaluation(true);
            
            if(USE_PLOT)
            {
                plot.addPoint(u.getVectorData(), results[i]);
            }
        }
        
        return !isMaxEvaluations(f);
    }
    
    public static double [][] generateUniformPattern(int aTotalPoints)
    {
        double [][] coords = new double[aTotalPoints][2];
        
        int numPerDim = (int) Math.ceil(Math.sqrt(aTotalPoints));
        double increment = 1.0 / numPerDim;
        int count = 0;
        for (int yy = 0; yy < numPerDim; yy++)
        {
            for (int xx = 0; xx < numPerDim && count < aTotalPoints; xx++)
            {
                coords[count][0] = (xx * increment) + (increment/2);
                coords[count][1] = (yy * increment) + (increment/2);
                count++;
            }
        }
        
        return coords;
    }
    
    public static void main(String[] args)
    {
        OnlineSamplePlot plot= new OnlineSamplePlot();
        double [][] uniformCoords = generateUniformPattern(100);
        for (int i = 0; i < uniformCoords.length; i++)
        {
            plot.addPoint(uniformCoords[i], 0);
        }
    }
    
    public Result runGA(Function p, Random r)
    {
        FuncOptUnitFactory factory = getFactory(p,r);
        ProgenyFuncOptGA progeny = getProgenyGA(factory,r);
        SelectionTournament selection = getSelection(r);
        StimulationFunctionEvaluation stimulation = getStimulation(p);
        OnlineSamplePlot plot = null;
        if(USE_PLOT)
        {
            plot = new OnlineSamplePlot();
        }
        
        LinkedList<Unit> population = new LinkedList<Unit>();
        FuncOptUnit bestEver = null;
        int iterations = 0;
        
        // initialise
//        for (int i = 0; i < initialPopulationSize; i++)
//        {
//            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit();
//            population.add(u);
//        }
        
        double [][] uniformCoords = generateUniformPattern(initialPopulationSize);
        double [][] minmax = p.getGenotypeMinMax();
        for (int i = 0; i < uniformCoords.length; i++)
        {
            boolean [] string = BitStringCommonUtils.calculateBitString(uniformCoords[i], minmax, p.getTotalBits());
            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit(string);
            population.add(u);
        }
        
        boolean canRun = evaluate(population, p, plot);
        bestEver = getBest(population, p);
        
        while(canRun)
        {
            iterations++;
            // select
            LinkedList<Unit> selected = selection.performSelection(population, stimulation);            
            // reproduce
            LinkedList<Unit> children = progeny.reproduce(selected.toArray(new Unit[selected.size()]));            
            // evaluate
            canRun = evaluate(children, p, plot);
            // add elites
            if(totalElites > 0)
            {
                // order the old population (ascending)
                Collections.sort(population, this);
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
            // check for something great in the new population
            FuncOptUnit best = getBest(children, p);
            population = children;
            // check for new best ever
            if(isBetter(best, bestEver, p))
            {
                bestEver = best;
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

    public int getInitialPopulationSize()
    {
        return initialPopulationSize;
    }

    public void setInitialPopulationSize(int initialPopulationSize)
    {
        this.initialPopulationSize = initialPopulationSize;
    }
    
    
}
