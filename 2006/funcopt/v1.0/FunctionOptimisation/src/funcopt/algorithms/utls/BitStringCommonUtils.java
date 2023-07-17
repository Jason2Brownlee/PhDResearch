
package funcopt.algorithms.utls;

import funcopt.Problem;

/**
 * 
 * Type: CommonUtils
 * File: CommonUtils.java
 * Date: 19/07/2004
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 *
 */
public class BitStringCommonUtils
{
	/**
	 * The number of bits in the lookup table
	 * Suuports from 0 <-> ((2^64)-1)
	 * The last value is the largest possible value
	 */
	public final static int LOOKUP_TABLE_SIZE = 64;

	/**
	 * Integer lookup table for powers of 2 (2^0 -> 2^LOOKUP_TABLE_SIZE)
	 */
	public final static long[] INTEGER_LOOKUP_TABLE = new long[LOOKUP_TABLE_SIZE];

	/**
	 * populate 1024 bit lookup table
	 */
	static
	{
		for (int i = 0; i < LOOKUP_TABLE_SIZE; i++)
		{
			INTEGER_LOOKUP_TABLE[i] = (long) Math.pow(2, i);
		}
	}

	
	/**
	 * Returns a long value between 0 <-> (2^length)-1
	 * @param bits
	 * @param offset
	 * @param length
	 * @return
	 */
	public final static long bitsToLong(boolean[] bits, int offset, int length)
	{
		if(length >= LOOKUP_TABLE_SIZE)
		{
			throw new IllegalArgumentException("Unsupported number of bits: " + length);
		}
		
		long sum = 0;

		for (int i = offset, lookupOffset = 0; i < offset+length; i++, lookupOffset++)
		{
			sum += (bits[i]) ? INTEGER_LOOKUP_TABLE[lookupOffset] : 0;
		}

		return sum;
	}

	
    
    public final static double [] bitsToCoord(boolean [] b, Problem p)
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
            // convert to double
            coord[i] = BitStringCommonUtils.bitsToDouble(b, offset, bitsPerCoord);
            // scale to required range
            coord[i] = (coord[i] * (minmax[i][1]-minmax[i][0])) + minmax[i][0];
        }
        
        return coord;
    }
    
   
    
    
    
	/**
	 * Returns a double value between 0 <-> 1 with a resolution of (2^length)-1
	 * @param bits
	 * @param offset
	 * @param length
	 * @return
	 */
	public final static double bitsToDouble(boolean[] bits, int offset, int length)
	{
		// calculate the long sum of bits
		long sum = bitsToLong(bits, offset, length);
		// convert to a ratio
		return (double) sum / ((double) (INTEGER_LOOKUP_TABLE[length]-1) );
	}
	
	public final static boolean [] doubleToBitString(double aValue, int numBits)
	{
		// convert to long
		long value = (long) (aValue * (double) (INTEGER_LOOKUP_TABLE[numBits]-1));
		// convert to bits
		return longToBitString(value, numBits);
	}
	
    
    
    
	public final static boolean[] calculateBitString(double[] aPhenotype, double [][] phenotypeMinMax, int totalLength)
	{
		boolean[] bitString = new boolean[totalLength];
		int bitsPerVar = totalLength / aPhenotype.length;

		int bitStringOffset = 0;
		for (int i = 0; i < aPhenotype.length; i++)
		{
			// convert back to a ratio (v-min)/range
			double value = (aPhenotype[i] - phenotypeMinMax[i][0]) / (phenotypeMinMax[i][1] - phenotypeMinMax[i][0]);
			// convert to bit string
			boolean[] tmpBits = BitStringCommonUtils.doubleToBitString(value, bitsPerVar);
			// store bits
			for (int j = 0; j < tmpBits.length; j++)
			{
				bitString[bitStringOffset++] = tmpBits[j];
			}
		}

		return bitString;
	}
	
	public final static boolean [] longToBitString(long aValue, int numBits)
	{
		boolean [] bitString = new boolean[numBits];
		
		for (int i = numBits-1; i >= 0; i--)
		{
			if((aValue - INTEGER_LOOKUP_TABLE[i]) >= 0)
			{
				bitString[i] = true;
				aValue -= INTEGER_LOOKUP_TABLE[i];
			}
		}
		
		return bitString;
	}
	
	public final static String bitsToString(boolean [] aBitString)
	{
		char [] s = new char[aBitString.length];
		
		for (int i = 0; i < s.length; i++)
		{
			if(aBitString[i])
			{
				s[i] = '1';
			}
			else
			{
				s[i] = '0';
			}
		}
		
		return new String(s);
	}

	/**
	 * Testing
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
//		long value = 536870911;
//		
//		long decoded = bitsToLong(encodedBits, 0, encodedBits.length);
//		System.out.println("expected:"+value+", got:"+decoded);
		
		
		boolean [] encodedBits = longToBitString(536870911, 30);
		System.out.println(bitsToString(encodedBits));
		
		double d = 0.4999999995343387;
		encodedBits = doubleToBitString(d, 30);
		System.out.println(bitsToString(encodedBits));
		
		boolean [] testBits = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false};
		System.out.println(bitsToLong(testBits, 0, testBits.length));
		System.out.println(bitsToDouble(testBits, 0, testBits.length));
		
		double d2 = bitsToDouble(encodedBits, 0, encodedBits.length);
		System.out.println("expected:"+d+", got:"+d2);
		
		
		/*
		System.out.println(Long.MAX_VALUE);
		System.out.println(INTEGER_LOOKUP_TABLE[LOOKUP_TABLE_SIZE - 1]);
		
		// test all num bits supported
		for (int k = 0; k < LOOKUP_TABLE_SIZE; k++)
		{
			int numBitsToTest = k;
			boolean [] bits = new boolean[numBitsToTest];
			
			// test zero case
			if(0 != bitsToLong(bits, 0, bits.length))
			{
				throw new RuntimeException("CASE["+numBitsToTest+"] - Values do not match expected "+0+", actual "+bitsToLong(bits, 0, bits.length)+".");
			}
			for (int i = 0; i < numBitsToTest; i++)
			{
				bits = new boolean[numBitsToTest];
				bits[i] = true;
				long expected = INTEGER_LOOKUP_TABLE[i];
				long actual = bitsToLong(bits, 0, bits.length);
				if(expected != actual)
				{
					throw new RuntimeException("CASE["+numBitsToTest+"] - Values do not match expected "+expected+", actual "+actual+".");
				}	
			}
			System.out.println("CASE["+numBitsToTest+"] - " + bitsToLong(bits, 0, bits.length));
			
			
			// test zero case
			bits = bits = new boolean[numBitsToTest];
			if(0.0 != bitsToDouble(bits, 0, bits.length))
			{
				throw new RuntimeException("CASE["+numBitsToTest+"] - Values do not match expected "+0+", actual "+bitsToDouble(bits, 0, bits.length)+".");
			}
			for (int i = 0; i < numBitsToTest; i++)
			{
				bits = new boolean[numBitsToTest];
				bits[i] = true;
				double expected = (Math.pow(2, i) / Math.pow(2, bits.length));
				double actual = bitsToDouble(bits, 0, bits.length);
				if(expected != actual)
				{
					throw new RuntimeException("CASE["+numBitsToTest+"] - Values do not match expected "+expected+", actual "+actual+".");
				}		
			}
		}
		
		// test largest possible value
		boolean [] bits = new boolean[LOOKUP_TABLE_SIZE - 1];
		for (int i = 0; i < bits.length; i++)
		{
			bits[i] = true;
		}
		System.out.println("Largest possible value: " + bitsToLong(bits, 0, bits.length));
		
		System.out.println("Finished all tests.");
		*/
	}
}