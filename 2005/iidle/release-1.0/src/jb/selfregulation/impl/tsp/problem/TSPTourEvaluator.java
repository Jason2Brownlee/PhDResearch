
package jb.selfregulation.impl.tsp.problem;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.tsp.units.TSPUnit;

/**
 * Type: TSPTourEvaluator<br/>
 * Date: 20/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TSPTourEvaluator implements UnitVisitor
{
    public double tourMax;
    public double tourMin;
    public double tourMean;
    public long tourTotal;
    
    public TSPTourEvaluator()
    {
        reset();
    }
    
    
    protected TSPProblem problem;
    
    public void setProblem(TSPProblem aProblem)
    {
        problem = aProblem;
    }
    

    public void visitUnit(Unit aUnit)
    {
        TSPUnit u = (TSPUnit) aUnit;
        
        // HACK HACK HACK
        if(!u.isHasTourLength())
        {
/*
            try
            {
            u.setTourLength(problem.calculateTourLength(u.getData()));
            u.setHasTourLength(true);
            }catch(NullPointerException e)
            {
                System.out.println("test");
            }
*/
        }
        
        // tour data
        updateTour(u);
    }
    
    
    
    protected void updateTour(TSPUnit u)
    {
        if(u.isHasTourLength())
        {
            double s = u.getTourLength();
            if(s < tourMin)
            {
                tourMin = s;
            }
            if(s > tourMax)
            {
                tourMax = s;
            }
            tourMean += s;
            tourTotal++;
        }
    }
    
    
    public void finished()
    {
        tourMean /= tourTotal;
    }
    
    public void reset()
    {
        tourMax = Double.MIN_VALUE;
        tourMin = Double.MAX_VALUE;
        tourMean = 0D;
        tourTotal = 0L;
    }
    
}