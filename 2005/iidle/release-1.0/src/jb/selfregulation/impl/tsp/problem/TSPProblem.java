
package jb.selfregulation.impl.tsp.problem;

import java.awt.geom.Line2D;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;

import jb.selfregulation.FileUtil;
import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.tsp.units.TSPUnit;

public class TSPProblem extends Problem
{
    protected String problemFilename;
    protected String solutionFilename;
    
    protected int [] solutionCityList;
    protected double solutionTourLength;
    
    protected double [][] cities;    
    protected double [][] distanceMatrix;
    protected int [][] nearestNeighbours;
    
    
    protected long totalTourEvaluations;
    
    
    public String getBase()
    {
        return ".problem.tsp";
    }
    
    public void loadConfig(String aBase, Properties prop)
    {
        problemFilename = prop.getProperty(aBase + getBase() + ".filename");
        solutionFilename = prop.getProperty(aBase + getBase() + ".solution");
    }
    
    public void setup(SystemState aState)
    {
        // load the problem
        try
        {
            loadProblem();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to load problem file: " + problemFilename, e);
        }
        // load the solution
        try
        {
            loadSolution();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to load solution file: " + solutionFilename, e);
        }
        // evaluate the solution
        evaluateSolution();
    }
    
    protected void evaluateSolution()
    {
        solutionTourLength = calculateTourLength(solutionCityList);
        totalTourEvaluations = 0; // reset
        logger.config("Loaded solution file " + solutionFilename + ", best tour length: " + solutionTourLength);
    }
    
    protected void loadSolution()
        throws Exception
    {
        // load the data
        //String d = FileUtil.loadFile(solutionFilename);
        String d = loadFile(solutionFilename);
        
        String [] lines = d.trim().split("\n");
        solutionCityList = new int[lines.length];
        for (int i = 0; i < lines.length; i++)
        {
            solutionCityList[i] = Integer.parseInt(lines[i]) - 1;
        }
    }
    
    
    
    protected void loadProblem()
        throws Exception
    {
        // load the data
        //String d = FileUtil.loadFile(problemFilename);
        String d = loadFile(problemFilename);
        
        String [] lines = d.trim().split("\n");
        cities = new double[lines.length][2];
        for (int i = 0; i < cities.length; i++)
        {
            String [] l = lines[i].trim().split(",");
            cities[i][0] = Double.parseDouble(l[0]);
            cities[i][1] = Double.parseDouble(l[1]);                      
        }
        // calculate distances once
        prepareDistanceMatrix();
        // calculate nearest neighbours
        calcuateNearestNeighbours();
        
        logger.config("Loaded problem file " + problemFilename + ", "+cities.length+" total cities.");
    }
    
    protected void calcuateNearestNeighbours()
    {
        nearestNeighbours = new int[cities.length][];
        // process all nodes
        for (int i = 0; i < nearestNeighbours.length; i++)
        {
            nearestNeighbours[i] = calculateTwoClosestNeighbours(i);
        }
    }
    
    protected int [] calculateTwoClosestNeighbours(int aCityNumber)
    {
        int [] closest = new int[2];
        double [] distances = new double[2];
        distances[0] = Double.MAX_VALUE;
        distances[1] = Double.MAX_VALUE;
        
        for (int i = 0; i < distanceMatrix[aCityNumber].length; i++)
        {
            // check for new best
            if(distanceMatrix[aCityNumber][i] < distances[0])
            {
                // shift on down the line
                swap(distances, 0, 1);
                swap(closest, 0, 1);
                // store new values
                distances[0] = distanceMatrix[aCityNumber][i];
                closest[0] = i;
            }
            // check if better than worst
            else if(distanceMatrix[aCityNumber][i] < distances[1])
            {
                distances[1] = distanceMatrix[aCityNumber][i];
                closest[1] = i;
            }
        }
        
        return closest;
    }
    
    protected void swap(double [] v, int i, int j)
    {
        double t = v[i];
        v[i] = v[j];
        v[j] = t;
    }
    protected void swap(int [] v, int i, int j)
    {
        int t = v[i];
        v[i] = v[j];
        v[j] = t;
    }
    
    protected void prepareDistanceMatrix()
    {
        distanceMatrix = new double[cities.length][cities.length];
        
        for (int x = 0; x < cities.length; x++)
        {
            for (int y = 0; y < cities.length; y++)
            {
                distanceMatrix[x][y] = distance(x, y);
            }
        }
    }

    public double calculateTourLength(int [] p)
    {        
        if(p.length != cities.length)
        {
            throw new RuntimeException("Length of tour permutation is unexpected " + p.length +", expected " + cities.length);
        }
        
        // count all tour evaluations
        totalTourEvaluations++;
        
        double sum = 0.0; 
        // do all cities
        for (int i = 1; i < p.length; i++)
        {
            sum += distanceMatrix[p[i-1]][p[i]];
        }
        // do the end to the start
        sum += distanceMatrix[p[p.length-1]][p[0]];
        return sum;
    }
    
    
    public int calculateTotalNNConnections(int [] p)
    {
        int total = 0;
        
        for (int i = 0; i < p.length; i++)
        {
            if(i == p.length-1)
            {
                if(isNNConnection(p[i], p[0]))
                {
                    total++;
                }
            }
            else
            {
                if(isNNConnection(p[i], p[i+1]))
                {
                    total++;
                }
            }
        }
        
        return total;
    }
    
    protected boolean isNNConnection(int c1, int n1)
    {
        if(nearestNeighbours[c1][0] == n1 || nearestNeighbours[c1][1] == n1)
        {
            return true;
        }
        return false;
    }
    
    
    public int calculateTotalCrossesInTour(int [] p)
    {
        int total = 0;
        
        // create lines
        Line2D [] lines = new Line2D[p.length];        
        for (int i = 0; i < p.length; i++)
        {
            int c1 = -1;
            int c2 = -1;
            
            if(i == 0)
            {
                c1 = p[p.length-1];
                c2 = p[0];
            }
            else
            {
                c1 = p[i-1];
                c2 = p[i];
            }
            
            lines[i] = new Line2D.Float((float)cities[c1][0], (float)cities[c1][1], (float)cities[c2][0], (float)cities[c2][1]);
        }
        
        // count intersections
        for (int i = 0; i < lines.length; i++)
        {
            for (int j = i+2; j < lines.length; j++)
            {                
                // no not test the nodes that i is connected to
                if(i == 0)
                {
                    if(lines[j] == lines[lines.length-1])
                    {
                        continue;
                    }
                }
                else
                {
                    if(lines[j] == lines[i-1])
                    {
                        continue;
                    }
                }
                
                if(lines[i].intersectsLine(lines[j]))
                {
                    total++;
                }
            }
        }
        
        return total;
    }
    
    
    public void checkSafety(TSPUnit u)
    {
        checkSafety(u.getData());
    }
    
    public void checkSafety(int [] v)
    {
        HashSet<Integer> set = new HashSet<Integer>(v.length);
        
        for (int i = 0; i < v.length; i++)
        {
            if(set.contains(v[i]))
            {
                throw new RuntimeException("Crossover created an invalid permutation!!!");
            }
            
            set.add(v[i]);
        }
    }
    
    
    private double distance(int c1, int c2)
    {
        // differences
        double diffX = cities[c1][0] - cities[c2][0];
        double diffY = cities[c1][1] - cities[c2][1];
        // sum
        double sum = (diffX * diffX) + (diffY * diffY);
        // root
        return Math.sqrt(sum);
    }
    
    public double[][] getCities()
    {
        return cities;
    }

    public double[][] getDistanceMatrix()
    {
        return distanceMatrix;
    }

    public int[] getSolutionCityList()
    {
        return solutionCityList;
    }

    public double getSolutionTourLength()
    {
        return solutionTourLength;
    }

    public long getTotalEvaluations()
    {
        return totalTourEvaluations;
    }
    
    
    /**
     * Read in a file as a string
     * @param filename
     * @return
     * @throws IOException
     */
    public String loadFile(String filename)
    {       
        InputStream in = null;
        byte [] b = new byte[1024 * 5];
        int offset = 0;
        
        try
        {
            in = this.getClass().getResourceAsStream("/"+filename);
            int t = 0;
            while((t=in.read(b, offset, 1024)) > 0)
            {
                offset += t;
            }
        }
        catch(Exception e)
        {}
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e)
                {}
            }
        }
        
        return new String(b, 0, offset);
    }
}
