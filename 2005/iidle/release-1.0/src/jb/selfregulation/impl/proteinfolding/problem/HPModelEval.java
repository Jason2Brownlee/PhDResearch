
package jb.selfregulation.impl.proteinfolding.problem;

import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.application.Problem;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.proteinfolding.drawing.SolutionDrawer;

/**
 * Type: HPModelEval<br/>
 * Date: 13/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class HPModelEval extends Problem
{
    /**
     * H == 0 (false)
     * P == 1 (true)
     */    
    protected boolean [] dataset;
    protected String permutation;
    protected int totalNaturalConnections;
    protected int totalEvals;
    

    public int getModelLength()
    {
        return dataset.length - 1;
    }
    
    
    public void isValid(byte [] aModel)
    {
        if(aModel.length != getModelLength())
        {
            throw new RuntimeException("Model length "+aModel.length+" does not match expected " + getModelLength());
        }
        for (int i = 0; i < aModel.length; i++)
        {
            if(aModel[i]<LEFT||aModel[i]>BACK)
            {
                throw new RuntimeException("Invalid model value " + aModel[i]);
            }
        }
    }
    
    
    
    @Override
    public long getTotalEvaluations()
    {
        return totalEvals;
    }

    public String getBase()
    {
        return ".problem.proteinfolding";
    }

    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        permutation = prop.getProperty(b+".perm");
    }

    public void setup(SystemState aState)
    {
        totalEvals = 0;
        dataset = stringDatasetToBoolean(permutation);
        totalNaturalConnections = totalNaturalConnections(dataset);
        logger.info(stats());
    }
    
    public String stats()
    {
        return "Protein Folding Problem Loaded.\n"
        +      "Permutation: " + permutation+"\n"  +
               "Length: " + dataset.length+"\n" +
               "Natural H-H connections: " + totalNaturalConnections;
    }

    public float evaluateModel(byte [] model)
    {
        // validate
        isValid(model);
        
        byte [][] map = null;        
        
        try
        {
            map = modelToMap(model);
        }
        catch (InvalidModelException e)
        {
            return e.collisionLength;
        }
        
        totalEvals++;
        float score = calculateScore(map);
        return dataset.length + score;
    }
    
    public int calculateScore(byte [][] aMap)
    {
        int total = 0;
        
        // sum total H-H connections
        for (int i = 0; i < aMap.length; i++)
        {
            for (int j = 0; j < aMap[i].length; j++)
            {
                if(aMap[i][j] == H)
                {
                    // could be both
                    if(i!=aMap.length-1 && aMap[i+1][j]==H)
                    {
                        total++;
                    }
                    if(j!=aMap[i].length-1 && aMap[i][j+1]==H)
                    {
                        total++;
                    }
                }
            }
        }        
        // subtract natural connecitons
        total = (total - totalNaturalConnections);
        
        if(total < 0)
        {
            throw new RuntimeException("Invalid total connections " + total);
        }
        
        return total;
    }
    
    public int getMapWidth()
    {
        // always double the (even) permutation length
        if((dataset.length%2)==0)
        {
            return dataset.length*2;
        }
        
        return (dataset.length+1)*2; // even
    }
    
    public int getStartCoordInMap()
    {
        return getMapWidth()/2;
    }
    
    
    public static final int LEFT = 1, FOWARD = 2, RIGHT = 3, BACK = 4;
    /**
     * @param model
     */
    public byte [][] modelToMap(byte [] aModel)
        throws InvalidModelException
    {
        byte [][] map = new byte [getMapWidth()][getMapWidth()];
        int [] coord = {getStartCoordInMap(),getStartCoordInMap()};
        
        // lay down the first value
        map[coord[0]][coord[1]] = val(dataset[0]);
        // step through the path
        for (int i = 0; i < aModel.length; i++)
        {
            switch(aModel[i])
            {
                case LEFT:
                {
                    coord[0] -= 1;
                    break;
                }       
                case FOWARD:
                {
                    coord[1] += 1;
                    break;
                }  
                case RIGHT:
                {
                    coord[0] += 1;                  
                    break;
                }
                case BACK:
                {
                    coord[1] -= 1;
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid model value " + aModel[i]);
                }                   
            }
            if(map[coord[0]][coord[1]] != EMPTY)
            {
                throw new InvalidModelException("Collision", i, map);
            }
            map[coord[0]][coord[1]] = val(dataset[i+1]);  
        }
        
        return map;
    }

    public final static int EMPTY = 0, H = 1, P = 2;
    public final static byte val(boolean b)
    {
        return (b) ? (byte)P : (byte)H;
    }
    
    public final static int totalNaturalConnections(boolean [] a)
    {
        int total = 0;
        
        for (int i = 0; i < a.length-1; i++)
        {
            if(!a[i] && !a[i+1])
            {
                total++;
            }
        }
        
        return total;
    }
    
    public final static String modelToString(byte[] aModel)
    {
        char [] c = new char[aModel.length];
        
        for (int i = 0; i < c.length; i++)
        {
            switch(aModel[i])
            {
                case LEFT:                
                {
                    c[i] = 'L';
                    break;
                }
                case RIGHT:
                {
                    c[i] = 'R';
                    break;
                }
                case BACK:
                {
                    c[i] = 'B';
                    break;
                }
                case FOWARD:
                {
                    c[i] = 'F';
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid model value " + aModel[i]);
                }                    
            }
        }
        
        return new String(c);
    }
    
    public final static String booleanToString(boolean [] b)
    {
        char [] c = new char[b.length];
        
        for (int i = 0; i < c.length; i++)
        {
            c[i] = b[i] ? 'P' : 'H';
        }
        
        return new String(c);
    }
    
    public final static boolean [] stringDatasetToBoolean(String s)
    {
        boolean [] b = new boolean[s.length()]; 
        char [] c = s.toCharArray();
        
        for (int i = 0; i < c.length; i++)
        {
            if(c[i] == 'H' || c[i] == 'h')
            {
                b[i] = false;
            }
            else if(c[i] == 'P' || c[i] == 'p')
            {
                b[i] = true;
            }
            else
            {
                throw new RuntimeException("Invalid character " + c[i]);
            }
        }
        
        return b;
    }


    public boolean[] getDataset()
    {
        return dataset;
    }


    public void setDataset(boolean[] dataset)
    {
        this.dataset = dataset;
    }


    public String getPermutation()
    {
        return permutation;
    }


    public void setPermutation(String permutation)
    {
        this.permutation = permutation;
    }


    public int getTotalNaturalConnections()
    {
        return totalNaturalConnections;
    }


    public void setTotalNaturalConnections(int totalNaturalConnections)
    {
        this.totalNaturalConnections = totalNaturalConnections;
    }    
    
    
    
    
}
