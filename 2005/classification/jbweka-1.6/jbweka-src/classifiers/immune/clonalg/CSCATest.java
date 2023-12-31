
package weka.classifiers.immune.clonalg;

import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Type: CLONALGTest<br>
 * Date: 19/01/2005<br>
 * <br>
 * 
 * Description: 
 * 
 * @author Jason Brownlee
 */
public class CSCATest
{
    public static void main(String[] args)
    {
		try
		{
//			String filename = "data/diabetes.arff";
//			String filename = "data/sonar.arff";
//			String filename = "data/iris.arff";
//			String filename = "data/balance-scale.arff";
//			String filename = "data/breast-w.arff";
			String filename = "data/heart-c.arff";
			
			// prepare dataset
            Instances dataset = new Instances(new FileReader(filename));
            dataset.setClassIndex(dataset.numAttributes() - 1);
            CSCA algorithm = new CSCA();   
        	// evaulate
            Evaluation evaluation = new Evaluation(dataset);
            evaluation.crossValidateModel(algorithm, dataset, 10, new Random(1));            
            // print algorithm details
            System.out.println(algorithm.toString());
            // print stats
            System.out.println(evaluation.toSummaryString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
}
