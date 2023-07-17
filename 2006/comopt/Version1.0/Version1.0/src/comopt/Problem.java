
package comopt;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Type: Problem<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class Problem implements Comparable<Problem>
{
    public final static String KEY_START_CITIES = "NODE_COORD_SECTION";
    public final static String KEY_DIMENSIONS = "DIMENSION";
    public final static String KEY_NAME = "NAME";
    public final static String KEY_DISTANCE = "EDGE_WEIGHT_TYPE";
    
    public final static String KEY_DISTANCE_EUCLIDEAN = "EUC_2D";
    public final static String KEY_DISTANCE_GEOGRAPHICAL = "GEO";
    
    public final static int TOTAL_EVALUATIONS = 10000;

    public static enum DISTANCE_TYPE
    {
        EUCLIDEAN, GEOGRAPHICAL
    }

    protected LinkedList<SolutionNotify> listeners;
    
    protected int evaluationCount;
    protected int maxEvaluations;
    protected String name;
    protected DISTANCE_TYPE distanceType;
    
    protected String problemFilename;    
    protected String solutionFilename;
    
    protected int[] solutionCityList;
    protected double solutionTourLength;

    protected double[][] cities;
    protected double[][] distanceMatrix;    
    
    public Problem(String aProblemFile, String aSolutionFile)
    {
        problemFilename = aProblemFile;
        solutionFilename = aSolutionFile; 
        listeners = new LinkedList<SolutionNotify>();
    }
   
    /**
     * Load the problem's solution
     * @throws Exception
     */
    protected void loadSolution()
        throws Exception
    {
        // load the data
        String d = FileUtils.loadFile(solutionFilename);        
        String [] lines = d.trim().split("\n");
        
        solutionCityList = new int[cities.length];
        boolean isProcessingCities = false;
        int cityOffset = 0;
        
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i].trim();
            
            if(isProcessingCities)
            {
                solutionCityList[cityOffset] = Integer.parseInt(line.trim());
                solutionCityList[cityOffset]--; // definition is 1 offset, make 0 offset
                if(++cityOffset >= solutionCityList.length)
                {
                    break; // finished
                } 
            }
            else if(line.equalsIgnoreCase(KEY_TOUR_START))
            {
                isProcessingCities = true;
            }
        }
        solutionTourLength = calculateTourLength(solutionCityList);
    }
    
    public final static String KEY_TOUR_START = "TOUR_SECTION";
    
    protected void loadProblem()
        throws Exception
    {
        // load the data
        String d = FileUtils.loadFile(problemFilename);        
        String [] lines = d.trim().split("\n");
       
        int dimensions = 0;
        boolean isProcessingCities = false;
        int cityOffset = 0;
        
        // process all lines
        for (int i = 0; i < lines.length; i++)
        {        
            String line = lines[i].trim();
            
            if(isProcessingCities)
            {                
                String [] tmp = line.split(" ");
                String [] parts = new String[3];
                int o = 0;
                for (int j = 0; j < tmp.length; j++)
                {
                    if(tmp[j] != null && (tmp[j]=tmp[j].trim()).length()>0)
                    {
                        parts[o++] = tmp[j];
                    }
                }                
                if(o != 3)
                {
                    throw new RuntimeException("Unexpected line while processing cities line["+i+"]: " + line);
                }
                // load in city data
                cities[cityOffset][0] = Double.parseDouble(parts[1].trim());
                cities[cityOffset][1] = Double.parseDouble(parts[2].trim());                
                if(++cityOffset >= dimensions)
                {
                    break; // finished
                }                
            }
            else if(line.trim().equalsIgnoreCase(KEY_START_CITIES))
            {
                if(dimensions <= 0)
                {
                    throw new RuntimeException("Reached city nodes before dimensionality was defined!");
                }
                
                isProcessingCities = true;
                cities = new double[dimensions][2];
            }
            else
            {
                String [] parts = line.split(":");                

                if(parts[0].trim().equalsIgnoreCase(KEY_NAME))
                {                    
                    name = parts[1].trim();
                }
                else if(parts[0].trim().equalsIgnoreCase(KEY_DIMENSIONS))
                {
                    dimensions = Integer.parseInt(parts[1].trim());
                }
                else if(parts[0].trim().equalsIgnoreCase(KEY_DISTANCE))
                {
                    String dist = parts[1].trim();
                    if(dist.equalsIgnoreCase(KEY_DISTANCE_EUCLIDEAN))
                    {
                        distanceType = DISTANCE_TYPE.EUCLIDEAN;
                    }
                    else if(dist.equalsIgnoreCase(KEY_DISTANCE_GEOGRAPHICAL))
                    {
                        distanceType = DISTANCE_TYPE.GEOGRAPHICAL;
                    }
                    else
                    {
                        throw new RuntimeException("Unknown distance type: " + dist);
                    }
                }
            }
        }        
        
        // calculate distances once
        prepareDistanceMatrix();
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
    
    public void checkSafety(Solution u)
    {
        checkSafety(u.getPermutation());
    }
    
    public void checkSafety(int [] v)
    {
        HashSet<Integer> set = new HashSet<Integer>(v.length);
        
        for (int i = 0; i < v.length; i++)
        {
            if(set.contains(v[i]))
            {
                throw new RuntimeException("Invalid permutation!!!");
            }
            
            set.add(v[i]);
        }
    }
    
    protected double distance(int c1, int c2)
    {
        double d = 0.0;
        
        switch(distanceType)        
        {
            case EUCLIDEAN:
            {
                d = euclideanDistance(c1,c2);
                break;
            }        
            case GEOGRAPHICAL:
            {
                d = geographicalDistance(c1,c2);
                break;
            }   
            default:
            {
                throw new RuntimeException("Unknown distance type: " + distanceType);
            }
        }
        
        return d;
    }
    
    public final static double RRR = 6378.388;
    
    /**
     * As defined in TSPLIB'95 (GEO)
     * @param c1
     * @param c2
     * @return
     */
    protected double geographicalDistance(int c1, int c2)
    {
        double latitude1 = Math.PI * (Math.round(cities[c1][0]) + 5.0 * (cities[c1][0]-Math.round(cities[c1][0])) / 3.0) / 180.0; // [c1]x
        double longitude1 = Math.PI * (Math.round(cities[c1][1]) + 5.0 * (cities[c1][1]-Math.round(cities[c1][1])) / 3.0) / 180.0; // [c1]y
        double latitude2 = Math.PI * (Math.round(cities[c2][0]) + 5.0 * (cities[c2][0]-Math.round(cities[c2][0])) / 3.0) / 180.0; // [c2]x
        double longitude2 = Math.PI * (Math.round(cities[c2][1]) + 5.0 * (cities[c2][1]-Math.round(cities[c2][1])) / 3.0) / 180.0; // [c2]y
        
        double q1 = Math.cos(longitude1 - longitude2);
        double q2 = Math.cos(latitude1 - latitude2);
        double q3 = Math.cos(latitude1 + latitude2);
        double dij = (int) (RRR * Math.acos(0.5*((1.0+q1)*q2 - (1.0-q1)*q3)) + 1.0);        
        return dij;
    }
    
    /**
     * As defined in TSPLIB'95 (EUC_2D)
     * @param c1
     * @param c2
     * @return
     */
    protected double euclideanDistance(int c1, int c2)
    {
        double xd = cities[c1][0] - cities[c2][0];
        double yd = cities[c1][1] - cities[c2][1];
        double dij = Math.sqrt((xd*xd + yd*yd));
        dij = Math.round(dij); // whatever...
        return dij;
    }
       
    
    protected void notifyListeners(int [] d, double score)
    {
        for(SolutionNotify s : listeners)
        {
            s.notifyOfPoint(d, score);
        }
    }
    
    public void initialise()
    {
        maxEvaluations = TOTAL_EVALUATIONS;        
        resetEvaluations();       
        
        // load the problem definition
        try
        {
            loadProblem();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load problem file: " + problemFilename, e);
        }
        // load the solution definition
        try
        {
            loadSolution();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load solution file: " + solutionFilename, e);
        }
    }
    
    public void setMaximumEvaluations(int aMax)
    {
        maxEvaluations = aMax;
    }
    
    public void resetEvaluations()
    {
        evaluationCount = 0;
    }
    
    public int remainingFunctionEvaluations()
    {
        if(evaluationCount > maxEvaluations)
        {
            return 0;
        }
        
        return maxEvaluations - evaluationCount;
    }
    
    public boolean isReamainingEvaluations()
    {
        return remainingFunctionEvaluations() > 0;
    }
    
    
    public void cost(Solution s)
    {
        if(s.isEvaluated())
        {
            return;
        }
        
        double c = cost(s.getPermutation());
        s.evaluated(c);
    }
    public void cost(LinkedList<Solution> ss)
    {
        for(Solution s : ss)
        {
            cost(s);
        }
    }
    
    protected double cost(int [] v)
    {
        // 1 to n
        if(++evaluationCount > maxEvaluations)
        {
            return Double.NaN;
        }
        
        // safety
        checkSafety(v); // TODO can be removed eventually!
        
        double s = calculateTourLength(v);
        notifyListeners(v, s);
        return s;
    }

    public double unCountedCost(int [] v)
    {
        return calculateTourLength(v);
    }
    
    public void addListener(SolutionNotify l)
    {
        listeners.add(l);
    }
    

    public int getEvaluationCount()
    {
        return evaluationCount;
    }   

    public LinkedList<SolutionNotify> getListeners()
    {
        return listeners;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param o
     * @return
     */
    public int compareTo(Problem o)
    {
        return name.compareTo(o.name);
    }    
    
    public double[][] getCities()
    {
        return cities;
    }

    public double[][] getDistanceMatrix()
    {
        return distanceMatrix;
    }

    public DISTANCE_TYPE getDistanceType()
    {
        return distanceType;
    }

    public int getMaxEvaluations()
    {
        return maxEvaluations;
    }

    public String getName()
    {
        return name;
    }

    public String getProblemFilename()
    {
        return problemFilename;
    }

    public int[] getSolutionCityList()
    {
        return solutionCityList;
    }

    public String getSolutionFilename()
    {
        return solutionFilename;
    }

    public double getSolutionTourLength()
    {
        return solutionTourLength;
    }
    
    public int getTotalCities()
    {
        return cities.length;
    }
    

    public static void main(String[] args)
    {
        Problem p = new Problem("tsp/tsp225.tsp", "tsp/tsp225.opt.tour");
        p.initialise();
        System.out.println(p.getName());
        System.out.println(p.getSolutionTourLength());
        
    }
}
