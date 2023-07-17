
package jb.selfregulation.impl.dummy.problem;

import java.util.Properties;

import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;

public class DummyProblem extends Problem
{
    protected long totalEvaluations;

    @Override
    public long getTotalEvaluations()
    {
        return totalEvaluations;
    }

    public String getBase()
    {
        return ".dummy";
    }

    public void loadConfig(String aBase, Properties prop)
    {}

    public void setup(SystemState aState)
    {}

}
