/**
 * Created on 9/11/2004
 *
 */
package maxpath;
import java.util.LinkedList;
import java.util.Random;

/**
 * Type: MaxPathProblem
 * File: MaxPathProblem.java
 * Date: 9/11/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class MaxPathProblem
{
    public final static int MIN_SQUARE_VALUE = 1;
    public final static int MAX_SQUARE_VALUE = 16;
    
    
    
    protected final byte [] grid;
    
    protected final int pathLength;
    
    protected final int numDimensions;
    
    protected final int gridWidth;
    
    
    protected byte [][] highestScoreCoords;
    
    
    /**
     * Constructor
     * 
     * @param aGrid
     * @param aPathLength
     * @param aNumDimensions
     * @param aGridWidth
     */
    public MaxPathProblem(
            byte [] aGrid, 
            int aPathLength,
            int aNumDimensions,
            int aGridWidth)
    {
        grid = aGrid;
        pathLength = aPathLength;
        numDimensions = aNumDimensions; 
        gridWidth = aGridWidth;
        
        // prepare highest score coords
        prepareHighestScoreCoords();
    }
    
    
    protected void prepareHighestScoreCoords()
    {
        LinkedList<byte []> list = new LinkedList<byte []>();
        
        for (int i = 0; i < grid.length; i++)
        {
            if(grid[i] >= 10 /*== MAX_SQUARE_VALUE*/)
            {
                list.add(offsetToByteCoord(i));
            }
        }
        
        highestScoreCoords = list.toArray(new byte[list.size()][]);
    }
    
    public byte [][] getHighestScoreCoords()
    {
        return highestScoreCoords;
    }
    
    
    public static boolean isSquareValueValid(byte aSquareValue)
    {
        return (aSquareValue<=MAX_SQUARE_VALUE && aSquareValue>=MIN_SQUARE_VALUE);
    }
    
    public static String pathToHumanReadable(int [][] aPath)
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        for (int i = 0; i < aPath.length; i++)
        {
            buffer.append("[");
            for (int j = 0; j < aPath[i].length; j++)
            {
                buffer.append(aPath[i][j]);
                if(j < aPath[i].length-1)
                {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            
            if(i < aPath.length-1)
            {
                buffer.append(", ");
            }
        }
        
        return buffer.toString();
    }
    
    public static String pathToHumanReadable(byte [][] aPath)
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        for (int i = 0; i < aPath.length; i++)
        {
            buffer.append("[");
            for (int j = 0; j < aPath[i].length; j++)
            {
                buffer.append(aPath[i][j]);
                if(j < aPath[i].length-1)
                {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            
            if(i < aPath.length-1)
            {
                buffer.append(", ");
            }
        }
        
        return buffer.toString();
    }
    
    public static String pathToSolutionFileFormat(int [][] aPath)
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        for (int i = 0; i < aPath.length; i++)
        {
            for (int j = 0; j < aPath[i].length; j++)
            {
                buffer.append(aPath[i][j]);
                if(j < aPath[i].length - 1)
                {
                    buffer.append(" ");   
                } 
            }
            
            if(i < aPath.length - 1)
            {
                buffer.append("\n");   
            }            
        }
        
        return buffer.toString();
    }
    
    public static String pathToSolutionFileFormat(byte [][] aPath)
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        for (int i = 0; i < aPath.length; i++)
        {
            for (int j = 0; j < aPath[i].length; j++)
            {
                buffer.append(aPath[i][j]);
                if(j < aPath[i].length - 1)
                {
                    buffer.append(" ");   
                } 
            }
            
            if(i < aPath.length - 1)
            {
                buffer.append("\n");   
            }            
        }
        
        return buffer.toString();
    }
    
    public static void main(String[] args)
    {
        try
        {            
            Random rand = new Random(); // systime
            
            // test 2d
            MaxPathProblem problem = ProblemReader.readProblem("easy_problem.txt");
            int numTests = problem.getNumSquares();
            for (int i = 0; i < numTests; i++)
            {
                int x = rand.nextInt(problem.getGridWidth());
                int y = rand.nextInt(problem.getGridWidth());
                int [] coord = new int[]{x,y};
                
                if(!problem.isCoordWithinBounds(coord))
                {
                    throw new Exception("Coord not in bounds of problem!");
                }
                
                if(problem.getValue(x,y) != problem.getValue(coord))
                {
                    throw new Exception("Values do not match in 2d Test!");
                }
                
	            int offset = problem.getOffset(coord);
	            int [] secondCoord = problem.offsetToCoord(offset);
	            if(!problem.isCoordsSame(coord, secondCoord))
	            {
	                throw new Exception("Coords do not match");
	            }                
            }
            System.out.println("Finished 2d Test!");
            
            
            // test 3d
            problem = ProblemReader.readProblem("medium_problem.txt");
            numTests = problem.getNumSquares();
            for (int i = 0; i < numTests; i++)
            {
                int x = rand.nextInt(problem.getGridWidth());
                int y = rand.nextInt(problem.getGridWidth());
                int z = rand.nextInt(problem.getGridWidth());
                int [] coord = new int[]{x,y,z};
                
                if(!problem.isCoordWithinBounds(coord))
                {
                    throw new Exception("Coord not in bounds of problem!");
                }
                
                if(problem.getValue(x,y,z) != problem.getValue(coord))
                {
                    throw new Exception("Values do not match in 3d Test!");
                }
                
	            int offset = problem.getOffset(coord);
	            int [] secondCoord = problem.offsetToCoord(offset);
	            if(!problem.isCoordsSame(coord, secondCoord))
	            {
	                throw new Exception("Coords do not match");
	            }
            }
            System.out.println("Finished 3d Test!");  
            
            
            // test 5d
            problem = ProblemReader.readProblem("hard_problem.txt");
            numTests = problem.getNumSquares();
            for (int i = 0; i < numTests; i++)
            {
                int x = rand.nextInt(problem.getGridWidth());
                int y = rand.nextInt(problem.getGridWidth());
                int z = rand.nextInt(problem.getGridWidth());
                int a = rand.nextInt(problem.getGridWidth());
                int b = rand.nextInt(problem.getGridWidth());
                int [] coord = new int[]{x,y,z,a,b};
                
                if(!problem.isCoordWithinBounds(coord))
                {
                    throw new Exception("Coord not in bounds of problem!");
                }
                
                if(problem.getValue(x,y,z,a,b) != problem.getValue(coord))
                {
                    throw new Exception("Values do not match in 5d Test!");
                }
                
	            int offset = problem.getOffset(coord);
	            int [] secondCoord = problem.offsetToCoord(offset);
	            if(!problem.isCoordsSame(coord, secondCoord))
	            {
	                throw new Exception("Coords do not match");
	            }
            }
            System.out.println("Finished 5d Test!");               
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }
    
    
    
    
    public byte getValue(int x, int y)
    {
        int offset = (gridWidth * y) + x;
        return grid[offset];
    }
    
    public byte getValue(int x, int y, int z)
    {
        int offset = ((gridWidth * gridWidth) * z) + (gridWidth * y) + x;
        return grid[offset];
    }
    
    public byte getValue(int x, int y, int z, int a, int b)
    {
        int offset = x;
        offset +=  (gridWidth * y);
        offset += ((gridWidth * gridWidth) * z);
        offset += ((gridWidth * gridWidth * gridWidth) * a);
        offset += ((gridWidth * gridWidth * gridWidth * gridWidth) * b);        
        return grid[offset];
    }
    
    public byte getValue(int [] coord)
    {
        int offset = coord[0];
        for (int i = 1; i < coord.length; i++)
        {
            offset += ((int)(Math.pow(gridWidth, i)) * coord[i]);                    
        }
        
        return grid[offset];
    }
    
    public byte getValue(short [] coord)
    {
        int offset = coord[0];
        for (int i = 1; i < coord.length; i++)
        {
            offset += ((int)(Math.pow(gridWidth, i)) * coord[i]);                    
        }
        
        return grid[offset];
    }
    
    public byte getValue(byte [] coord)
    {
        int offset = coord[0];
        for (int i = 1; i < coord.length; i++)
        {
            offset += ((int)(Math.pow(gridWidth, i)) * coord[i]);                    
        }
        
        return grid[offset];
    }
    
    public int getOffset(int [] coord)
    {
        int offset = coord[0];
        for (int i = 1; i < coord.length; i++)
        {
            offset += ((int)(Math.pow(gridWidth, i)) * coord[i]);                    
        }
        
        return offset;
    }
    
    
    public int [] offsetToCoord(int aOffset)
    {
        int [] coord = new int[numDimensions];
        
        for (int i = coord.length-1; i > 0; i--)
        {
            int planes = (int) (Math.pow(gridWidth, i));
            int numPlanes = aOffset / planes;
            aOffset -= (numPlanes * planes);
            coord[i] = numPlanes;
        }
        
        coord[0] = aOffset;
        
        return coord;
    }
    
    public short [] offsetToShortCoord(int aOffset)
    {
        short [] coord = new short[numDimensions];
        
        for (int i = coord.length-1; i > 0; i--)
        {
            int planes = (int) (Math.pow(gridWidth, i));
            int numPlanes = aOffset / planes;
            aOffset -= (numPlanes * planes);
            coord[i] = (short) numPlanes;
        }
        
        coord[0] = (short) aOffset;
        
        return coord;
    }
    
    public byte [] offsetToByteCoord(int aOffset)
    {
        byte [] coord = new byte[numDimensions];
        
        for (int i = coord.length-1; i > 0; i--)
        {
            int planes = (int) (Math.pow(gridWidth, i));
            int numPlanes = aOffset / planes;
            aOffset -= (numPlanes * planes);
            coord[i] = (byte) numPlanes;
        }
        
        coord[0] = (byte) aOffset;
        
        return coord;
    }
    
    
    
    public boolean isCoordWithinBounds(int [] coord)
    {
        if(coord.length == numDimensions)
        {
            for (int i = 0; i < coord.length; i++)
            {
                if(coord[i]<0 || coord[i]>gridWidth-1)
                {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    public boolean isCoordWithinBounds(byte [] coord)
    {
        if(coord.length == numDimensions)
        {
            for (int i = 0; i < coord.length; i++)
            {
                if(coord[i]<0 || coord[i]>gridWidth-1)
                {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    public int evaulatePath(int [][] aPath)
    {
        if(aPath.length != pathLength)
        {
            throw new RuntimeException("Path has incorrect length ["+aPath.length+"], expected ["+pathLength+"].");
        }
        
        int score = 0;
        
        for (int i = 0; i < aPath.length; i++)
        {
            if(!isCoordWithinBounds(aPath[i]))
            {
                throw new RuntimeException("Coord ["+i+"] is not in the bounds of the problem.");
            }
            
            // check if the score is in the path already
            boolean pointReused = false;
            for (int j = 0; !pointReused && j < i; j++)
            {
                if(isCoordsSame(aPath[i], aPath[j]))
                {
                    pointReused = true;
                }
            }
            
            if(!pointReused)
            {
                score += getValue(aPath[i]);
            }
        }
        
        return score;
    }
    
    public int evaulatePath(byte [][] aPath)
    {
        if(aPath.length != pathLength)
        {
            throw new RuntimeException("Path has incorrect length ["+aPath.length+"], expected ["+pathLength+"].");
        }
        
        int score = 0;
        
        for (int i = 0; i < aPath.length; i++)
        {
            if(!isCoordWithinBounds(aPath[i]))
            {
                throw new RuntimeException("Coord ["+i+"] is not in the bounds of the problem.");
            }
            
            // check if the score is in the path already
            boolean pointReused = false;
            for (int j = 0; !pointReused && j < i; j++)
            {
                if(isCoordsSame(aPath[i], aPath[j]))
                {
                    pointReused = true;
                }
            }
            
            if(!pointReused)
            {
                score += getValue(aPath[i]);
            }
        }
        
        return score;
    }
    
    
    public boolean isCoordsSame(int [] coord1, int [] coord2)
    {
        for (int i = 0; i < coord1.length; i++)
        {
            if(coord1[i] != coord2[i])
            {
                return false;
            }
        }
        
        return true;
    }
    public boolean isCoordsSame(byte [] coord1, byte [] coord2)
    {
        for (int i = 0; i < coord1.length; i++)
        {
            if(coord1[i] != coord2[i])
            {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isPathContiguous(int [][] aPath)
    {
        for (int i = 1; i < aPath.length; i++)
        {
            int [] lastPoint = aPath[i-1];
            // all points must be the same except a movement of 1 on one dimension
            boolean haveMovement = false;
            for (int j = 0; j < lastPoint.length; j++)
            {
                if(aPath[i][j] != lastPoint[j])
                {
                    if(haveMovement)
                    {
                        return false;
                    }
                    else if(aPath[i][j] != lastPoint[j]+1 && aPath[i][j] != lastPoint[j]-1)
                    {
                        return false;
                    }
                    else
                    {
                        haveMovement = true;
                    }
                }
            }
        }
        
        return true;
    }
    
    
    
    public boolean isPathContiguous(short [][] aPath)
    {
        for (int i = 1; i < aPath.length; i++)
        {
            short [] lastPoint = aPath[i-1];
            // all points must be the same except a movement of 1 on one dimension
            boolean haveMovement = false;
            for (int j = 0; j < lastPoint.length; j++)
            {
                if(aPath[i][j] != lastPoint[j])
                {
                    if(haveMovement)
                    {
                        return false;
                    }
                    else if(aPath[i][j] != lastPoint[j]+1 && aPath[i][j] != lastPoint[j]-1)
                    {
                        return false;
                    }
                    else
                    {
                        haveMovement = true;
                    }
                }
            }
        }
        
        return true;
    }
    
    
    public boolean isPathContiguous(byte [][] aPath)
    {
        for (int i = 1; i < aPath.length; i++)
        {
            byte [] lastPoint = aPath[i-1];
            // all points must be the same except a movement of 1 on one dimension
            boolean haveMovement = false;
            for (int j = 0; j < lastPoint.length; j++)
            {
                if(aPath[i][j] != lastPoint[j])
                {
                    if(haveMovement)
                    {
                        return false;
                    }
                    else if(aPath[i][j] != lastPoint[j]+1 && aPath[i][j] != lastPoint[j]-1)
                    {
                        return false;
                    }
                    else
                    {
                        haveMovement = true;
                    }
                }
            }
        }
        
        return true;
    }
    
    
    public boolean isPathValid(byte [][] aPath)
    {
        // check length
        if(aPath.length != pathLength)
        {
            return false;
        }      
        
        // check all coords are valid
        for(byte [] coord : aPath)
        {
            if(!isCoordWithinBounds(coord))
            {
                return false;
            }
        }             
            
        // check path is contigious
        if(!isPathContiguous(aPath))
        {
            return false;
        }
        
        return true;
    }
    
    public boolean isPathValid(int [][] aPath)
    {
        // check length
        if(aPath.length != pathLength)
        {
            return false;
        }      
        
        // check all coords are valid
        for(int [] coord : aPath)
        {
            if(!isCoordWithinBounds(coord))
            {
                return false;
            }
        }             
            
        // check path is contigious
        if(!isPathContiguous(aPath))
        {
            return false;
        }
        
        return true;
    }
    
    public String toString()
    {       
        return "Problem Details: Dimensions["+numDimensions+"], GridWidth["+gridWidth+"], TotalSquares["+getNumSquares()+"], PathLength["+pathLength+"].";
    }
    
    
    
    /**
     * @return Returns the grid.
     */
    public byte[] getGrid()
    {
        return grid;
    }
    /**
     * @return Returns the gridWidth.
     */
    public int getGridWidth()
    {
        return gridWidth;
    }
    /**
     * @return Returns the numDimensions.
     */
    public int getNumDimensions()
    {
        return numDimensions;
    }
    /**
     * @return Returns the pathLength.
     */
    public int getPathLength()
    {
        return pathLength;
    }
    
    public int getNumSquares()
    {
        return grid.length;
    }
    
}
