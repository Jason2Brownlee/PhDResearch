
package swsom.algorithm;



/**
 * Type: Distance<br>
 * Date: 23/02/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class MathUtils
{
    public static double distanceEuclidean(double [] v1, double [] v2)    
    {
        double sumSquaredDiff = 0.0;
        
        for (int i = 0; i < v1.length; i++)
        {
            double diff = v1[i] - v2[i];
            sumSquaredDiff += (diff * diff);
        }
        
        return Math.sqrt(sumSquaredDiff);
    }
    
    /**
     * Taken from
     * http://mathforum.org/library/drmath/view/54785.html
     * 
     * @param c1
     * @param r1
     * @param c2
     * @param r2
     * @return
     */
   /* public static double intersectionAreaCircles(
            double [] c1, 
            double r1, 
            double [] c2, 
            double r2)
    {
        // length of line between two circles
        double length = distanceEuclidean(c1,c2);
        // first intersection point
        // cos(CBA) = (r1^2 + c^2 - r0^2)/(2*r1*c)
        double cba = (((r2*r2) + (length*length) - (r2*r2)) / (2*r2*length)) * (PI/180.0);
        double cbd = 2*cba;
        // cos(CAB) = (r0^2 + c^2 - r1^2)/(2*r0*c)
        double cab = (((r1*r1) + (length*length) - (r2*r2))/(2*r1*length)) * (PI/180.0);
        double cad = 2 * cab;
        
        // Area = (1/2)(CBD)r1^2 - (1/2)r1^2*sin(CBD)
        // + (1/2)(CAD)r0^2 - (1/2)r0^2*sin(CAD)
        
        double area = 0.5*cbd*(r2*r2) - 0.5*(r2*r2)*sin(cbd)
                    + 0.5*cad*(r1*r1) - 0.5*(r1*r1)*sin(cad);
        return area;
    }*/
    
    /*public static void main(String[] args)
    {
        double [] v1 = {1, 1};
        double r1 = 1;
        
        double [] v2 = {1, 1};
        double r2 = 1;
        
        System.out.println("Area: " + intersectionAreaCircles(v1,r1,v2,r2));
    }*/
}
