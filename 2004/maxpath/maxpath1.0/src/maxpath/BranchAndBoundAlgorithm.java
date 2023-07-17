/**
 * Created on 3/11/2004
 *
 */
package maxpath;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Type: ExhaustiveSearch File: ExhaustiveSearch.java Date: 3/11/2004
 * 
 * Description:
 * 
 * @author Jason Brownlee
 * 
 */
public class BranchAndBoundAlgorithm implements Runnable
{
    private final static DecimalFormat format = new DecimalFormat();
    
    private final static String PROPERTIES_FILENAME = "algorithm.properties";

    
    // algorithm configuration property names
    
    // easy, medium, hard
    private final static String PROP_PROB_TYPE = "problem.type";    
  
    // problem specific
    private final static String PROP_PROB_FILE = "problem.filename";
    private final static String PROP_PATH_EST = "problem.estimation.value";    
    private final static String PROP_START_BEST_SCORE = "problem.initial.start.score";

    // general
    private final static String PROP_MIN_SEARCH = "min.search.square.value.cutoff";
    private final static String PROP_MIN_NEIGHBOUR = "min.neighbour.sqaure.value.cutoff";
    private final static String PROP_POTENTIAL_ORDERING = "search.potential.order.desc";
    private final static String PROP_SEARCH_INCREMENT_MODE = "search.increment.mode";
    private final static String PROP_SEARCH_INCREMENT_VALUE = "search.increment.value";
    private final static String PROP_SEARCH_INCREMENT_ITERATIONS = "search.increment.iterations";
    private final static String PROP_SEARCH_QUICK_FIRST_PASS = "search.firstpass";
    private final static String PROP_SEARCH_QUICK_FIRST_PASS_NUM_NEIGH = "search.firstpass.neighbours";
    
    private final static String PROP_NUM_THREADS = "num.threads";
    private final static String PROP_WRITE_BEST_PATHS = "log.write.best.path";
    private final static String PROP_LOG_GOOD_PATHS = "log.goodpaths";
    private final static String PROP_USE_LOG = "log.tofile";

    // properties
    protected boolean logToFile;
    protected boolean logWriteBestPaths;
    protected String problemFilename;
    protected float cellEstimationValue;
    protected int minSearchCellValueCutoff;
    protected int minNeighbourCellValueCutoff;
    protected int numThreads;
    protected float initialBestPathScore;
    protected boolean logGoodPaths;
    protected String problemType;
    protected boolean potentialOrdering;
    protected boolean incrementMode;
    protected float incrementValue;
    protected int incrementIterations;    
    protected boolean quickFirstPass;
    protected int quickFirstPassNumNeighbours;

    // bets path
    protected Point[] bestPath;
    protected volatile float runningBestPathScore;
    protected float[] estimationLookupTable;

    // search data
    protected LinkedList<Point> pointList;
    protected HashMap<Integer, Point> allPointSet;
    protected Point [] pointArray;

    // multithreading
    protected CountDownLatch latch;
    protected ReentrantLock scoreLock;

    // problem information
    protected int pathLength;
    protected int gridWidth;
    protected int totalSquares;
    protected int totalSquaresSearched;
    protected MaxPathProblem problem;

    // logging
    protected FileWriter writer;  
    
    
   
    /**
     * Entry Point
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            BranchAndBoundAlgorithm search = new BranchAndBoundAlgorithm();
            search.startup();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Constructor
     * 
     * @param aProblem
     * @param bestStartingPoint
     */
    public BranchAndBoundAlgorithm()
    {
        bestPath = null;
        runningBestPathScore = 0;
    }

    protected boolean isMultiThreaded()
    {
        return (numThreads > 1);
    }

    protected void shutdown()
    {
        if (logToFile)
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                    writer.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                writer = null;
            }
        }

        System.exit(0);
    }

    protected void loadProperties()
    {
        FileInputStream reader = null;
        try
        {
            reader = new FileInputStream(PROPERTIES_FILENAME);
            Properties prop = new Properties();
            prop.load(reader);

            // get the problem type
            problemType = prop.getProperty(PROP_PROB_TYPE);
            String prefix = problemType + ".";
            
            // load problem specific things
            problemFilename = prop.getProperty(prefix+PROP_PROB_FILE);
            cellEstimationValue = Float.parseFloat(prop.getProperty(prefix+PROP_PATH_EST));
            initialBestPathScore = Float.parseFloat(prop.getProperty(prefix+PROP_START_BEST_SCORE));
            
            // load general problem things
            minNeighbourCellValueCutoff = Integer.parseInt(prop.getProperty(PROP_MIN_NEIGHBOUR));
            minSearchCellValueCutoff = Integer.parseInt(prop.getProperty(PROP_MIN_SEARCH));
            potentialOrdering = Boolean.parseBoolean(prop.getProperty(PROP_POTENTIAL_ORDERING));
            incrementMode = Boolean.parseBoolean(prop.getProperty(PROP_SEARCH_INCREMENT_MODE));
            incrementValue = Float.parseFloat(prop.getProperty(PROP_SEARCH_INCREMENT_VALUE));
            incrementIterations = Integer.parseInt(prop.getProperty(PROP_SEARCH_INCREMENT_ITERATIONS));            
            quickFirstPass = Boolean.parseBoolean(prop.getProperty(PROP_SEARCH_QUICK_FIRST_PASS));
            quickFirstPassNumNeighbours = Integer.parseInt(prop.getProperty(PROP_SEARCH_QUICK_FIRST_PASS_NUM_NEIGH));
            
            // load run configuration 
            logToFile = Boolean.parseBoolean(prop.getProperty(PROP_USE_LOG));            
            logGoodPaths = Boolean.parseBoolean(prop.getProperty(PROP_LOG_GOOD_PATHS));
            numThreads = Integer.parseInt(prop.getProperty(PROP_NUM_THREADS));
            logWriteBestPaths = Boolean.parseBoolean(prop.getProperty(PROP_WRITE_BEST_PATHS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            shutdown();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public void performQuickFirstPass()
    {
        log("Running quick first pass...");
        
        // ensure best is always at the top
        Point.setPotentialOrderingDecending(true);
        // set estimation value
        float initalEstimation = cellEstimationValue;
        cellEstimationValue = MaxPathProblem.MAX_SQUARE_VALUE;
        // re-prepare the estimation lookup table
        prepareEstimationData();
        
        // prepare all neighbour data keeping only the best for each point
        preparePointNeighbours(allPointSet.values().iterator(), true);
        // run the algorithm
        runMasterAlgorithm();
                
        // prepare primary point list again
        preparePointListAgain();
        // reset the estimation value
        cellEstimationValue = initalEstimation;
        // re-prepare the estimation lookup table
        prepareEstimationData();        
        // set back to what the user desires
        Point.setPotentialOrderingDecending(potentialOrdering);        
        
        log("Finished quick first pass.");
    }
    
    

    public void startup()
    {
        // load properties file
        loadProperties();

        // prepare the log
        prepareLog();
        
        // user feedback
        log(new Date().toString());
        logConfigurationData();
        
        // prepare all problem data
        prepareAllProblemData();
        
        // check if a quick first pass can be performed
        if(quickFirstPass)
        {
            performQuickFirstPass();
        }
                
        // prepare all neighbour data
        preparePointNeighbours(allPointSet.values().iterator(), false);
        
        if(incrementMode)
        {
            for (int i = 0; i < incrementIterations; i++)
            {
                log("Running in iteration mode, iteration: "+(i+1)+"/"+incrementIterations);
                log("Cell Estimation Value: " + cellEstimationValue);
                // run the algorimth
                runMasterAlgorithm();
                // prepare the list a second time
                preparePointListAgain();
                // increment the estimation value
                cellEstimationValue += incrementValue;
                // re-prepare the estimation lookup table
                prepareEstimationData();
            }
        }
        else
        {
            runMasterAlgorithm();
        }
        
        shutdown();
    }

    protected void preparePointListAgain()
    {
        pointList.clear();
        for (int i = 0; i < pointArray.length; i++)
        {
            pointList.add(pointArray[i]);
        }
    }
    
    protected void prepareAllProblemData()
    {
        if (isMultiThreaded())
        {
            scoreLock = new ReentrantLock();
        }
        
        // set point ordering
        Point.setPotentialOrderingDecending(potentialOrdering);

        // load the file
        loadFile();

        // prepare estimation data
        prepareEstimationData();

        // build a list of points
        pointList = new LinkedList<Point>();
        allPointSet = new HashMap<Integer, Point>(totalSquares * 2);
        
        // prepare the list of points
        preparePointList();
        runningBestPathScore = initialBestPathScore; // inital starting
        if(runningBestPathScore==0)
        {
            runningBestPathScore = pathLength * 10;
        }
    }
    
    
    protected void runMasterAlgorithm()
    {
        long runStartTime = System.currentTimeMillis();

        // perform the search
        runAlgorithm();

        long runEndTime = System.currentTimeMillis();
        logFinalReport(runEndTime - runStartTime);
    }
    
    
    protected void prepareLog()
    {
        if (logToFile)
        {
            try
            {
                writer = new FileWriter("logfile[" + System.currentTimeMillis() + "].txt");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                shutdown();
            }
        }
    }
    
    protected void logFinalReport(long totalRunningTime)
    {
        log(new Date().toString());
        log("-----------------------------------------------");
        log("Algorithm Run Completed");
        log("(total run time: " + Utils.calculateTime(totalRunningTime)+")");
        log("-----------------------------------------------");
        if (bestPath == null)
        {
            log("No Path Found!");
        }
        else
        {
            log("Details of Best Path Found:");
            log("Path:......................." + pathToString(bestPath));
            log("Path Score:................." + runningBestPathScore);
            log("Path Length:................" + bestPath.length);
            log("Path Starting Cell Value:..." + bestPath[0].score);
        }
        log("-----------------------------------------------");
    }
    
    protected void logConfigurationData()
    {
        log("-----------------------------------------------");
        log("Algorithm Configuration");
        log("-----------------------------------------------");
        log("Problem Type:...................." + problemType);
        log("Problem Filename:................" + problemFilename);
        log("Problem Initial Best Score:......" + initialBestPathScore);
        log("Problem Cell Estimation Value:..." + cellEstimationValue);
        log("Min Search Cell Cutoff:.........." + minSearchCellValueCutoff);
        log("Min Neighbour Cell Cutoff:......." + minNeighbourCellValueCutoff);
        log("Number Threads:.................." + numThreads);
        log("Log to File:....................." + logToFile);
        log("Log Best Paths:.................." + logGoodPaths);
        log("Write Best To Files:............." + logWriteBestPaths);
        log("Point Potential Order Desc:......" + potentialOrdering);
        log("Search Increment Mode:..........." + incrementMode);
        log("Search Increment Value:.........." + incrementValue);
        log("Search Increment Iterations:....." + incrementIterations);
        log("-----------------------------------------------");
    }
    
    protected void prepareEstimationData()
    {
        estimationLookupTable = new float[pathLength + 1];

        for (int i = 0; i < estimationLookupTable.length; i++)
        {
            estimationLookupTable[i] = (i * cellEstimationValue);
        }
    }

    protected void loadFile()
    {
        long start = System.currentTimeMillis();

        try
        {
            problem = ProblemReader.readProblem(problemFilename);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pathLength = problem.getPathLength();
        gridWidth = problem.getGridWidth();
        totalSquares = problem.getNumSquares();

        long finish = System.currentTimeMillis();
        log("Loaded problem file: " + problemFilename + ", Took: " + Utils.calculateTime(finish - start));
        log(problem.toString());
    }

    protected void preparePointList()
    {
        long start = System.currentTimeMillis();

        byte[] grid = problem.getGrid();

        LinkedList<Point>[] sortedList = new LinkedList[MaxPathProblem.MAX_SQUARE_VALUE];
        for (int i = 0; i < sortedList.length; i++)
        {
            sortedList[i] = new LinkedList<Point>();
        }

        for (int i = 0; i < grid.length; i++)
        {
            Point p = pointFactoryNewPoint(problem.offsetToByteCoord(i));

            // add to the search list
            if (p.score >= minSearchCellValueCutoff)
            {
                sortedList[p.score - 1].add(p);
            }

            // add to the lookup set
            Integer key = new Integer(p.customHash);
            if (allPointSet.get(key) != null)
            {
                throw new RuntimeException("Got a collosition representing points.");
            }
            allPointSet.put(key, p);
        }

        for (int i = sortedList.length - 1; i >= 0; i--)
        {
            pointList.addAll(sortedList[i]);
        }

        totalSquaresSearched = pointList.size();
        
        long finish = System.currentTimeMillis();
        log("Prepared point list, took: " + Utils.calculateTime(finish - start) + ", Total Nodes Added: " + pointList.size());
    }

    protected class PreProcessPoints implements Runnable
    {
        private final Iterator<Point> list;
        private final boolean performQuickFirstPass;

        public PreProcessPoints(final Iterator<Point> aList,
                boolean aPerformQuickFirstPass)
        {
            list = aList;
            performQuickFirstPass = aPerformQuickFirstPass;
        }

        public void run()
        {
            boolean canRun = true;
            do
            {
                Point nextPoint = null;
                synchronized (list)
                {
                    if (!list.hasNext())
                    {
                        canRun = false;
                    }
                    else
                    {
                        nextPoint = list.next();
                    }
                }

                if (canRun)
                {
                    // get neighbours
                    nextPoint.neighbours = getAllNeighboursForPoint(nextPoint, performQuickFirstPass);
                }
            }
            while (canRun);

            // another thread finished
            latch.countDown();
        }
    }
    
    protected void preparePointNeighbours(
            final Iterator<Point> aPointIterator, 
            boolean performQuickFirstPass)
    {
        long start = System.currentTimeMillis();

        if (isMultiThreaded())
        {
            latch = new CountDownLatch(numThreads);

            // create threads
            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++)
            {
                threads[i] = new Thread(new PreProcessPoints(aPointIterator, performQuickFirstPass));
                threads[i].start();
            }

            try
            {
                latch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            long totalNeighbours = 0;
            while (aPointIterator.hasNext())
            {
                Point element = aPointIterator.next();
                element.neighbours = getAllNeighboursForPoint(element, performQuickFirstPass);
                totalNeighbours += element.neighbours.length;
            }

            log("Total prepared neighbours: " + totalNeighbours);
        }
        
        // final preperation
        finalPointListReordering();

        long finish = System.currentTimeMillis();
        log("Prepared point neighbours, took: " + Utils.calculateTime(finish - start));
    }
    
    protected void finalPointListReordering()
    {
        // calculate all points potential
        for(Point p : allPointSet.values())
        {
            p.calculatePotential();
        }
        // reorder all neighbours
        for(Point p : allPointSet.values())
        {
            Arrays.sort(p.neighbours);
        }
        
        // reorder primary search list
        Collections.sort(pointList);
    	
        // sored ordered array of all points
    	pointArray = pointList.toArray(new Point[pointList.size()]);
    	
    	// set point id's as array offsets
    	for (int i = 0; i < pointArray.length; i++)
        {
    	    pointArray[i].id = i;
        }
    }

    protected Point[] getAllNeighboursForPoint(
            Point aPoint,
            boolean performQuickFirstPass)
    {
        LinkedList<Point> list = new LinkedList<Point>();

        for (int i = 0; i < aPoint.coord.length; i++)
        {
            if (aPoint.coord[i] == 0)
            {
                byte[] coord = Utils.duplicate(aPoint.coord);
                coord[i]++;
                preprocessAddToNeighbourList(coord, list);
            }
            else if (aPoint.coord[i] == gridWidth - 1)
            {
                byte[] coord = Utils.duplicate(aPoint.coord);
                coord[i]--;
                preprocessAddToNeighbourList(coord, list);
            }
            else
            {
                byte[] coord = Utils.duplicate(aPoint.coord);
                coord[i]++;
                preprocessAddToNeighbourList(coord, list);

                coord = Utils.duplicate(aPoint.coord);
                coord[i]--;
                preprocessAddToNeighbourList(coord, list);
            }
        }

        // sort
        Collections.sort(list);
        
        // remove any additional 
        if(performQuickFirstPass)
        {
            // only keep the single best
            while(list.size() > quickFirstPassNumNeighbours)
            {
                list.removeLast();
            }
        }
        
        // get neighbours
        return list.toArray(new Point[list.size()]);
    }

    protected void preprocessAddToNeighbourList(byte[] aCoord, LinkedList<Point> aList)
    {
        Point aPoint = pointFactoryLookup(aCoord);
        if (aPoint.score >= minNeighbourCellValueCutoff)
        {
            aList.add(aPoint);
        }
    }

    protected void runAlgorithm()
    {
        if (isMultiThreaded())
        {
            latch = new CountDownLatch(numThreads);

            // create threads
            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++)
            {
                threads[i] = new Thread(this);
                threads[i].start();
            }

            try
            {
                latch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            run();
        }
    }



    public Point requestNextPoint()
    {
        // multiple threads
        if (isMultiThreaded())
        {
            synchronized (pointList)
            {
                if (pointList.isEmpty())
                {
                    return null;
                }
                
                return pointList.removeFirst();
            }
        }
        // single thread
        else if (!pointList.isEmpty())
        {
            return pointList.removeFirst();
        }

        return null;
    }

    public void run()
    {
        long startTime = 0;       
        long endTime = 0;
        long totalNodes = 0;
        long totalTime = 0;
        Point nextPoint = null;        
        
        while ((nextPoint = requestNextPoint()) != null)
        {
            startTime = System.currentTimeMillis();
            try
            {
                totalNodes = investigatePath(nextPoint);    
            }
            catch(Exception e)
            {
                e.printStackTrace();
                shutdown();
            }
            endTime = System.currentTimeMillis();
            totalTime = (endTime - startTime);
            
            finishedInvestigatingPoint(nextPoint, totalTime, totalNodes);
        }

        if (isMultiThreaded())
        {
            latch.countDown();
        }
    }
    
    protected void finishedInvestigatingPoint(
            Point aPoint, 
            long totalRunningTime,
            long totalNodesInvestigated)
    {
        StringBuffer buffer = new StringBuffer(1024);
//        double nodesPerSecond = totalNodesInvestigated / (totalRunningTime / 1000.0);
        double percentagePointsLeft = ((double)pointList.size()/(double)totalSquaresSearched) * 100.0;
        
        buffer.append("Point[" + aPoint.score + "], ");
        buffer.append("Remaining[" + format.format(percentagePointsLeft) + "%], ");
        buffer.append("Time[" + Utils.calculateTime(totalRunningTime) + "], ");
        buffer.append("Nodes[" + format.format(totalNodesInvestigated) + "], ");
        //buffer.append("Rate[" + format.format(nodesPerSecond) + " per sec], ");
        buffer.append("Running Best[" + runningBestPathScore + "].");
        
        // log the fact that the point is completed
        log(buffer.toString());
    }
    
    
    protected static class Path
    {
        protected final LinkedList<Point> pathList;
        protected final HashSet<Point> pathSet;
        protected final int totalPathLength;
        
        protected int runningScore;
        protected int runningLength;
        
        protected long pointsInvestigated;
        
        
        public Path(int aPathLength)
        {
            totalPathLength = aPathLength;
            pathList = new LinkedList<Point>();
            pathSet = new HashSet<Point>(aPathLength * 2);
        }
        
        public boolean isPathRequiredLength()
        {
            return (runningLength == totalPathLength);
        }
        
        public void addToPath(Point aPoint)        
        {
            pathList.add(aPoint);
            pathSet.add(aPoint);
            runningScore += aPoint.score;
            runningLength++;
            pointsInvestigated++;
        }
        
        public void removeLast()
        {
            Point last = pathList.removeLast();
            pathSet.remove(last);
            runningScore -= last.score;
            runningLength--;
        }
        
        public byte [][] toByteArray()
        {
        	byte [][] bytePath = new byte[runningLength][];

            for (int i = 0; i < bytePath.length; i++)
            {
                bytePath[i] = pathList.get(i).coord;
            }
            
            return bytePath;
        }
        
        public Point [] toPointArray()
        {        	
            return pathList.toArray(new Point[runningLength]);
        }
        
        public int calculateManualScore()
        {
            int score = 0;
            
            for(Point p : pathList)
            {
                score += p.score;
            }
            
            return score;
        }
        
        public boolean isPointOnPath(Point aPoint)
        {
            return pathSet.contains(aPoint);
        }
    }
    
    protected class Stack
    {
        protected final LinkedList<Point> stackList;
        
        protected final int [] levelCounts;
        
        protected int currentLevel;
        
        public Stack(int aPathLength)
        {
            stackList = new LinkedList<Point>();
            levelCounts = new int[aPathLength];
        }
        
        public Point pop()
        {
            // check if current level is empty
            if(levelCounts[currentLevel] == 0)
            {
                currentLevel--; // step back a level
                return null; // inform the caller the current level is empty
            }
            
            // decrement the current level
            levelCounts[currentLevel]--;                        
            return stackList.removeLast();
        }
        
        public void push(Point aPoint)
        {
            stackList.add(aPoint);
            levelCounts[currentLevel]++;
        }
        
        public boolean isEmpty()
        {
            return stackList.isEmpty();
        }
        
        public void incrementLevel()
        {
            currentLevel++;
        }
    }

    
    protected long investigatePath(Point aStartPoint)
		throws Exception
	{        
	    Path path = new Path(pathLength);
	    Stack stack = new Stack(pathLength);
	    Point nextPoint = null;
	    
	    // add first point to the stack
	    stack.push(aStartPoint);
	    
	    // run until there are no more points to process
	    while (!stack.isEmpty())
	    {
	        // get the next point from the stack
	        nextPoint = stack.pop();	        
	        
	        // check if current level is empty
	        if (nextPoint == null)
	        {
	            // remove the node from the path
	            path.removeLast();
	        }
	        else 
	        {
	            // add point to the path
	            path.addToPath(nextPoint);
	            
	            // check for suitable path length
	            if(path.isPathRequiredLength())
	            {
	                // respond to the path
	                respondToPath(path);
	                // rempove the last node
	                path.removeLast();
	            }
	            else
	            {
	                // increment the current level
	                stack.incrementLevel();
	                // add all neighbours to the stack
	                addNeighboursToStack(path, stack, nextPoint);	                
	            }
	        }
	    }
	
	    return path.pointsInvestigated;
	}

    protected void addNeighboursToStack(
            Path aPath, 
            Stack aStack, 
            Point lastPointAdded)
    {
        if (lastPointAdded.neighbours != null)
        {
            // add neighbours to stack in reverse order
            for (int i = lastPointAdded.neighbours.length - 1; i >= 0; i--)
            {               
                // check if the score of the point is worth investigating
                if (!isPointScoreWorthInvestigating(lastPointAdded.neighbours[i], aPath.runningScore, aPath.runningLength))
                {
                    continue;
                }
                // check if point is already in the path
                else if (aPath.isPointOnPath(lastPointAdded.neighbours[i]))
                {
                    continue;
                }
                // add point to stack
                else
                {
                    aStack.push(lastPointAdded.neighbours[i]);
                }
            }
        }
    }


    protected boolean isPointScoreWorthInvestigating(
            Point aPoint, 
            int aRunningTotal,
            int aPathLength)
    {
        
        /*
        float estimatedPathScore = aRunningTotal + aPoint.score;
        
        // one away from path length
        if(aPathLength == pathLength-2)            
        {
            estimatedPathScore += MaxPathProblem.MAX_SQUARE_VALUE;
        }
        // anything except path length or one away from path length
        else if(aPathLength != pathLength-1)
        {
            estimatedPathScore += estimationLookupTable[pathLength - (aPathLength + 1)];
            estimatedPathScore += 5.0;
        }        
                
        if (estimatedPathScore >= runningBestPathScore)
        {
            return true;
        }
        */
        
        
        
        ///*
        float estimatedPathScore = aRunningTotal + aPoint.score + estimationLookupTable[pathLength - (aPathLength + 1)];
        if (estimatedPathScore >= (runningBestPathScore))
        {
            return true;
        }
        //*/

        return false;
    }
    
    public void respondToPath(Path aPath)
    	throws Exception
    {
        byte [][] bytePath = aPath.toByteArray();
        
        // safety
        int manualScore = aPath.calculateManualScore();
        if(aPath.runningScore != manualScore)
        {
            throw new Exception("Manually calculated score["+manualScore+"], does not match running total score["+aPath.runningScore+"].");
        }
        else if(!problem.isPathValid(bytePath))
        {
            throw new Exception("The evaulated path of length["+bytePath.length+"] is invalid! " + MaxPathProblem.pathToHumanReadable(bytePath));
        }
        
        if (isMultiThreaded())
        {
            scoreLock.lock();
            
            if (aPath.runningScore > runningBestPathScore)
            {
                updateBestPath(aPath);
            }
            
            scoreLock.unlock();
            
        }
        else
        {
            // check for new best path
            if (aPath.runningScore > runningBestPathScore)
            {
                updateBestPath(aPath);
            }
        }
    }
    
    public void updateBestPath(Path aPath)
    {
        // store
        runningBestPathScore = aPath.runningScore;
        bestPath = aPath.toPointArray();
        
        // log
        if (logGoodPaths)
        {
            printPath(bestPath, runningBestPathScore);
        }
        else
        {
            log(" -> New best path score: " + runningBestPathScore);
        }
        
        // write out best paths
        if(logWriteBestPaths)
        {
        	String solution = MaxPathProblem.pathToSolutionFileFormat(aPath.toByteArray());
        	Utils.stringToFile(solution, problemType+"_solution_"+aPath.runningScore+".txt");
        }
    }


    public String pathToString(Point[] aPath, double aScore)
    {
        StringBuffer buffer = new StringBuffer(1024);

        buffer.append("Score: ");
        buffer.append(aScore);
        buffer.append(", StartValue: ");
        buffer.append(aPath[0].score);
        buffer.append(", Path: ");

        for (int i = 0; i < bestPath.length; i++)
        {
            buffer.append(bestPath[i]);
            if (i < bestPath.length - 1)
            {
                buffer.append(", ");
            }
        }

        return buffer.toString();
    }
    
    public String pathToString(Point[] aPath)
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        for (int i = 0; i < bestPath.length; i++)
        {
            buffer.append(bestPath[i]);
            if (i < bestPath.length - 1)
            {
                buffer.append(", ");
            }
        }

        return buffer.toString();
    }

    public void printPath(Point[] aPath, double aScore)
    {
        log(" -> " + pathToString(aPath, aScore));
    }

    
    public Point pointFactoryNewPoint(byte[] aCoord)
    {
        return new Point(aCoord, problem.getValue(aCoord));
    }
    

    
    public Point pointFactoryLookup(byte[] aCoord)
    {
        int code = Point.calculateCustomHash(aCoord);
        return allPointSet.get(new Integer(code));
    }
    

    protected void log(String aString)
    {
        System.out.println(aString);

        if (logToFile)
        {
            try
            {
                writer.write(aString + "\n");
                writer.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
