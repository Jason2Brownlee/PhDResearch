
package funcopt.algorithms.utls;

import funcopt.Problem;

/**
 * Type: BitStringUtils<br/>
 * Date: 24/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class BitStringUtils
{        
    public static enum DECODE_MODE {Binary, GrayCode}
  
        
    /**
     * Decode a bit string
     * @param p
     * @param mode
     * @param b
     * @return
     */
    public final static double [] decode(Problem p, DECODE_MODE mode, boolean [] b)
    {
        int d = p.getDimensions();                        
        if(d>1 && (b.length % d) != 0)
        {
            throw new RuntimeException("Unable to evenly divide "+b.length+" bits into "+ d);
        }
        
        // determine the even division of bits
        double [] coord = new double[d];
        double [][] minmax = p.getMinmax();
        int bitsPerCoord = b.length / coord.length;        
        
        // process each phenotypic value
        for (int i = 0, offset = 0; i < coord.length; i++, offset+=bitsPerCoord)
        {
            switch(mode)
            {
                case Binary:
                {
                    coord[i] = binaryBitsToDouble(b, offset, bitsPerCoord, minmax[i][0], minmax[i][1]);
                    break;
                }
                case GrayCode:
                {
                    coord[i] = grayBitsToDouble(b, offset, bitsPerCoord, minmax[i][0], minmax[i][1]);
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid decode mode!");
                }
            }
        }
        
        return coord;
    }
    
    /**
     * decode a single binary bitstring
     * @param b
     * @param offset
     * @param length
     * @param min
     * @param max
     * @return
     */
    public static double binaryBitsToDouble(boolean [] b, int offset, int length, double min, double max)
    {
        // do the sum
        double sum = 0.0;
        for (int i = 0, o = offset; i < length; i++, o++)
        {
            sum += (b[o] ? 1.0 : 0.0) * Math.pow(2.0, i);
        }
        // do the division
        double div = (max-min) / (Math.pow(2, length) - 1);
        return min + div * sum;
    }
    
    /**
     * Decode a single gray code string
     * @param b
     * @param offset
     * @param length
     * @param min
     * @param max
     * @return
     */
    public static double grayBitsToDouble(boolean [] b, int offset, int length, double min, double max)
    {        
        // do the sum
        double sum = 0.0;
        for (int i = 0; i < length; i++)        
        {
            // modulo addition to current position
            double mod = 0.0;
            for (int j = 0, o = offset; j<=i; j++, o++)
            {
                mod += (b[o] ? 1.0 : 0.0);
            }
            
            sum += (mod%2) * Math.pow(2.0, i);            
        }
        // do the division
        double div = (max-min) / (Math.pow(2, length) - 1);
        return min + div * sum;
    }    

	/**
     * Testing 
	 */
	public static void main(String[] args)
	{
        // for testing
        /**
            Dec  Gray   Binary
             0   000    000
             1   001    001
             2   011    010
             3   010    011
             4   110    100
             5   111    101
             6   101    110
             7   100    111
         */        
        
	    // 7
        boolean [] bc7 = {true,true,true};
        System.out.println(binaryBitsToDouble(bc7, 0, bc7.length, 0, Math.pow(2,bc7.length)-1));
        
        // 7
        boolean [] gc7 = {true,false,false};
        System.out.println(grayBitsToDouble(gc7, 0, gc7.length, 0, Math.pow(2,gc7.length)-1));        
	}
}