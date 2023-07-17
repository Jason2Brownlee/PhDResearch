
package maxpath;

/**
 * Type: SolutionConverter
 * Date: 19/11/2004
 * 
 * 
 * @author Jason Brownlee
 */
public class SolutionConverter
{
    public static void main(String[] args)
    {
        try
        {
            String filename = "C:/JasonBrownlee/MaxPath/Hard_153017.txt"; 
            String output = "C:/JasonBrownlee/MaxPath/Hard_153017_2.txt"; 
            
            
            StringBuffer buffer = new StringBuffer(1024);
            String fileData = ProblemReader.loadFile(filename);
            String [] lines = fileData.split("\\], ");
            for(String line : lines)
            {
                if(line != null && (line=line.trim()).length()>0)
                {
                    line = line.replace("[", " ");
                    line = line.replace(", ", " ");
                    line = line.replace("]", " ");
                    line = line.trim();
                    
                    buffer.append(line);
                    buffer.append("\r\n");
                }
            }
            
            // write the thing
            Utils.stringToFile(buffer.toString(), output);
//            System.out.println(buffer.toString());
            System.out.println("Completed, file written to: " + output);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
