
package jb.selfregulation.impl.proteinfolding.problem;

import java.util.Random;

import javax.swing.JFrame;

import jb.selfregulation.impl.proteinfolding.drawing.SolutionDrawer;

/**
 * Type: TestCase<br/>
 * Date: 14/02/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class TestCase
{
    public static void main(String[] args)
    {
        String s = "hhhhhhhhhhhhphphpphhpphhpphpphhpphhpphpphhpphhpphphphhhhhhhhhhhh";        
        
        HPModelEval p = new HPModelEval();        
        p.dataset = p.stringDatasetToBoolean(s);        
        p.totalNaturalConnections = p.totalNaturalConnections(p.dataset);
        
//        System.out.println("Loaded, total natural: " + p.totalNaturalConnections+ ", length " + p.dataset.length);
//        System.out.println(s);
//        System.out.println(p.booleanToString(p.dataset));
        
        System.out.println(p.stats());
        
        SolutionDrawer sd = new SolutionDrawer(p);
        JFrame jf = new JFrame("Solution Drawer");
        jf.setSize(640, 640);
        jf.getContentPane().add(sd);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        
        int numTests = 1;
        Random r = new Random(-1);
        for (int i = 0; i < numTests; i++)
        {
//            byte [] model = new byte[p.dataset.length];
//            for (int j = 0; j < model.length; j++)
//            {
//                model[j] = (byte) ((Math.abs(r.nextInt()) % 4) + 1);
//            }
            
            byte [] model = new byte[p.getModelLength()];
            for (int j = 0; j < model.length; j++)
            {
                if((j%2)==0)
                    model[j] = HPModelEval.FOWARD;
                else
                    model[j] = HPModelEval.LEFT;
            }
            
//            byte [] model = {LEFT, FOWARD, LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT, FOWARD,LEFT};          
//            byte [] model = {FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD,FOWARD};
//            byte [] model = {BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK,BACK};
//            byte [] model = {LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT,LEFT};
//            byte [] model = {RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT,RIGHT};
            
            
            System.out.println("MAP : "+p.modelToString(model)+", Score: " + p.evaluateModel(model));
            byte [][] map = null;
            try
            {
                map = p.modelToMap(model);
            }
            catch(InvalidModelException e)
            {
                map = e.model;
            }            
            
//            System.out.println(map.length + ", " + map[i].length);
            
            sd.updateMap(map, model);
            
            synchronized(p)
            {
                try
                {
                    p.wait(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
