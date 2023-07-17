
package funcopt.algorithms;

import java.util.LinkedList;

import javax.swing.JPanel;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: UniformSearch<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class UniformSearch extends Algorithm
{
    @Override
    protected Solution executeAlgorithm(Problem p)
    {
        int totalPoints = p.remainingFunctionEvaluations();
        LinkedList<Solution> pop = RandomUtils.generateUniform2DPattern(totalPoints, p);
        p.cost(pop); // batch
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);        
        return bestEver;
    }

    @Override
    public String getName()
    {
        return "Uniform Search";
    }

    @Override
    protected JPanel getConfigurationPane()
    {
        return null;
    }

    @Override
    protected int getNumParameters()
    {
        return 0;
    }

    @Override
    public void initialise(Problem p)
    {}

}
