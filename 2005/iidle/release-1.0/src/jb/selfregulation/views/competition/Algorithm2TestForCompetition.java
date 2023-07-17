
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

public class Algorithm2TestForCompetition
{
//    public static void main(String[] args)
//    {
//        Random r = new Random();
//      CompetitionFunction p = (CompetitionFunction) getTestFunction(r);
//      p.setMoon("20_1");
//      p.reset();
//        new Algorithm2TestForCompetition().main(p);
//    }
    
    
    protected int populationSize;
    protected double crossoverPercentage;
    protected double mutationPercentage;
    protected int totalElites;
    protected int tournamentSize;
    
    public Algorithm2TestForCompetition()
    {
        // defaults
        populationSize = 10;
        crossoverPercentage = 1.0;
        mutationPercentage = 0.01;
        totalElites = 1;
        tournamentSize = 3;
    }
    
    protected static Function getTestFunction(Random r)
    {
        CompetitionFunction problem = new CompetitionFunction();
        problem.setRand(r);
        problem.prepare();
        return problem;
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
    
    
    public boolean isMaxEvaluations(Function f)
    {
        if(f.getTotalEvaluations() > CompetitionFunction.MAX_ITERATIONS)
        {
            throw new RuntimeException("Evaluated too many times!!!");
        }
        else if(f.getTotalEvaluations() == CompetitionFunction.MAX_ITERATIONS)
        {
            return true;
        }
        
        return false;
    }
    
    public int calculateRemainingEvaluations(Function f)
    {
        return CompetitionFunction.MAX_ITERATIONS - (int)f.getTotalEvaluations();
    }
    
    public boolean evaluate(
            LinkedList<Unit> pop, 
            Function p, 
            OnlineSamplePlot plot)
    {
        //CompetitionFunction f = (CompetitionFunction) p;
        LinkedList<FuncOptUnit> eval = new LinkedList<FuncOptUnit>();        
        int remaining = calculateRemainingEvaluations(p);
        
        if(remaining<=0)
        {
            return false;
        }
        
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
        double [] results = evaluate(v, p);
        
        // because results may be < the number of units we wanted evaluated
        for (int i = 0; i < results.length; i++)
        {
            FuncOptUnit u = eval.get(i);
            u.setFunctionEvaluation(results[i]);
            u.setHasFunctionEvaluation(true);         
            // plot
//            plot.addPoint(u.getVectorData(),results[i]);
        }
        
//        System.out.println(p.getTotalEvaluations());
        
        return !isMaxEvaluations(p);
    }
    
    public double [] evaluate(LinkedList<double []> v, Function f)
    {       
        double [] results = new double[v.size()];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = f.evaluate(v.get(i));
        }
        
        return results;
    }
    
    
    public double [][] generateUniformPattern(int aTotalPoints, Function p)
    {
        double [][] minmax = p.getGenotypeMinMax();
        
        double min = minmax[0][0];
        double max = minmax[0][1];
        double range = (max-min);
        
        
        double [][] coords = new double[aTotalPoints][2];
        
        int numPerDim = (int) Math.ceil(Math.sqrt(aTotalPoints));
        double increment = (1.0 / numPerDim) * range;
        int count = 0;
        for (int yy = 0; yy < numPerDim; yy++)
        {
            for (int xx = 0; xx < numPerDim && count < aTotalPoints; xx++)
            {
                coords[count][0] = min + (xx * increment) + (increment/2);
                coords[count][1] = min + (yy * increment) + (increment/2);
                count++;
            }
        }
        
        return coords;
    }
    
    protected class MyComparator implements Comparator<Unit>
    {
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
    }
    
    
    public Result main(CompetitionFunction p, Random r)
    {
//        Random r = new Random();
        
//        CompetitionFunction p = (CompetitionFunction) getTestFunction(r);
//        p.setMoon(moon);
//        p.reset();
        
        FuncOptUnitFactory factory = getFactory(p,r);
        ProgenyFuncOptGA progeny = getProgenyGA(factory,r);
        SelectionTournament selection = getSelection(r);
        StimulationFunctionEvaluation stimulation = getStimulation(p);
        
//        SampleDisplayer plot = new SampleDisplayer();
        OnlineSamplePlot plot = null;
        
        LinkedList<Unit> population = new LinkedList<Unit>();
        
        double [][] uniformCoords = generateUniformPattern(256, p);
        double [][] minmax = p.getGenotypeMinMax();
        for (int i = 0; i < uniformCoords.length; i++)
        {
            boolean [] string = BitStringCommonUtils.calculateBitString(uniformCoords[i], minmax, p.getTotalBits());
            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit(string);
            
//            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit();
            population.add(u);
        }
        
        p.resetTotalEvaluations();
        boolean canRun = evaluate(population, p, plot);
        FuncOptUnit bestEver = getBest(population, p);
        
        Collections.sort(population, new MyComparator());
        
        // select top n
        while(population.size() > 4)
        {
            if(p.isMinimisation())
            {
                // remove the big
                population.removeLast();
            }
            else
            {
                // remove the small
                population.removeFirst();
            }
        }        
        
        int smallPopSize = 10;
        int iterations = ((1000-256) / population.size()) / smallPopSize;
        
        // best last
        Collections.reverse(population);        
        for (int i = 0; i < population.size(); i++)
        {
            FuncOptUnit c = (FuncOptUnit) population.get(i);
            if(i == population.size()-1)
            {
                iterations = 9999; // the reset
            }            
            
            
            
            FuncOptUnit best = runGA(smallPopSize,p,plot,iterations,progeny,selection,c,factory,stimulation);
            
            if(isBetter(best, bestEver, p))
            {
                bestEver = best;
            }
        }   
        
//        System.out.println("Best Found: " + bestEver.getFunctionEvaluation());
//        p.getClient().shutdown();
        
        Result result = new Result();
        result.best = bestEver;
        result.bestScore = bestEver.getFunctionEvaluation();
        result.iterations = 0;
        return result;
    }
    
    
    public FuncOptUnit runGA(
            int smallPopSize,
            Function p,
            OnlineSamplePlot plot,
            int iterations, 
            ProgenyFuncOptGA progeny, 
            SelectionTournament selection,
            FuncOptUnit centroid,
            FuncOptUnitFactory factory,
            StimulationFunctionEvaluation stimulation
            )
    {
        long initialSamplesRemaining = p.getTotalEvaluations();
        
        boolean canRun = true;
        FuncOptUnit bestEver = centroid;
        
        LinkedList<Unit> population = new LinkedList<Unit>();
        population.add(centroid);
        while(population.size() < smallPopSize)
        {
            FuncOptUnit u = (FuncOptUnit) factory.generateNewUnit(centroid);
            progeny.setMutationPercentage(0.3);
            progeny.openMutate(u);
            population.add(u);
        }
        
        iterations--;// first is initial pop
        progeny.setMutationPercentage(mutationPercentage);
        progeny.setTotalProgeny(smallPopSize);
        selection.setNumToSelect(smallPopSize);
        
        // evaluate
        canRun = evaluate(population, p, plot);
        
        for (int i = 0; canRun && i < iterations; i++)
        {
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
                Collections.sort(population, new MyComparator());
                // add elites
                for (int j = 0; j < totalElites; j++)
                {
                    if(p.isMinimisation())
                    {
                        // take first (remember it is sorted ascending)
                        children.add(population.get(j));
                    }
                    else
                    {
                        // take end (remember it is sorted ascending)
                        children.add(population.get((population.size()-1)-j));
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
        
        long finalSamplesRemaining = p.getTotalEvaluations();
//        System.out.println("Samples used: " + (finalSamplesRemaining-initialSamplesRemaining));
        
        return bestEver;
    }
}
