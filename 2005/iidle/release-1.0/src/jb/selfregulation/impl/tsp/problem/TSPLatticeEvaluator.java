
package jb.selfregulation.impl.tsp.problem;

import jb.selfregulation.Unit;
import jb.selfregulation.UnitVisitor;
import jb.selfregulation.impl.tsp.units.TSPUnit;

public class TSPLatticeEvaluator implements UnitVisitor
{
    public double tourMax;
    public double tourMin;
    public double tourMean;
    public long tourTotal;

    public double crossesMax;
    public double crossesMin;
    public double crossesMean;
    public long crossesTotal;
    
    public double nnConnectionsMax;
    public double nnConnectionsMin;
    public double nnConnectionsMean;
    public long nnConnectionsTotal;
    
    public TSPLatticeEvaluator()
    {
        reset();
    }
    

    public void visitUnit(Unit aUnit)
    {
        TSPUnit u = (TSPUnit) aUnit;
        // tour data
        updateTour(u);
        // crosses data
        updateCrosses(u);
        // nn connections data
        updateNNConnections(u);
    }
    
    
    protected void updateNNConnections(TSPUnit u)
    {
        if(u.isHasNNConnections())
        {
            double s = u.getTotalNNConnections();
            if(s < nnConnectionsMin)
            {
                nnConnectionsMin = s;
            }
            if(s > nnConnectionsMax)
            {
                nnConnectionsMax = s;
            }
            nnConnectionsMean += s;
            nnConnectionsTotal++;
        }
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
    
    protected void updateCrosses(TSPUnit u)
    {
        if(u.isHasTotalCrosses())
        {
            double s = u.getTotalCrosses();
            if(s < crossesMin)
            {
                crossesMin = s;
            }
            if(s > crossesMax)
            {
                crossesMax = s;
            }
            crossesMean += s;
            crossesTotal++;
        }
    }
    
    public void finished()
    {
        tourMean /= tourTotal;
        crossesMean /= crossesTotal;
        nnConnectionsMean /= nnConnectionsTotal;
    }
    
    public void reset()
    {
        tourMax = crossesMax = nnConnectionsMax = Double.MIN_VALUE;
        tourMin = crossesMin = nnConnectionsMin = Double.MAX_VALUE;
        tourMean = crossesMean = nnConnectionsMean = 0D;
        tourTotal = crossesTotal = nnConnectionsTotal = 0L;
    }
    
}