
package funcopt.algorithms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcopt.Algorithm;
import funcopt.Problem;
import funcopt.Solution;
import funcopt.algorithms.utls.BitStringUtils;
import funcopt.algorithms.utls.GAUtils;
import funcopt.algorithms.utls.PopulationAlgorithmUtils;
import funcopt.algorithms.utls.RandomUtils;

/**
 * Type: DiffuseGeneticAlgorithm<br/>
 * Date: 25/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class DiffuseGeneticAlgorithm extends Algorithm implements ActionListener
{
    public final static int TOTAL_NEIGHBOURS = 4;

    public final static double DEFAULT_CROSSOVER = 0.95;
    public final static double DEFAULT_MUTATION = 0.005;
    public final static int DEFAULT_POPSIZE = 100;
    public final static int DEFAULT_BOUTSIZE = 2;
    public final static int DEFAULT_ELITES = 1;
    public final static BitStringUtils.DECODE_MODE DEFAULT_DECODE_MODE = BitStringUtils.DECODE_MODE.Binary; 
    
    protected Random r;
    
    protected long seed;
    protected double crossover;
    protected double mutation;
    protected int popsize;
    protected int boutSize;
    protected int elitism;
    public BitStringUtils.DECODE_MODE decodeMode;
    
    protected JTextField seedField;
    protected JTextField crossoverField;
    protected JTextField mutationField;
    protected JTextField popsizeField;
    protected JTextField boutsizeField;
    protected JTextField elitismField;
    protected JComboBox decodeField;
    
    protected static class BinarySolution extends Solution
    {
        boolean [] bitString;
        
        public BinarySolution(){}
        
        public BinarySolution(boolean[] abitString)
        {
            setBitString(abitString);
        }

        public boolean[] getBitString()
        {
            return bitString;
        }

        public void setBitString(boolean[] bitString)
        {
            this.bitString = bitString;
        }
    }
    
    
    
    

    @Override
    public Solution executeAlgorithm(Problem p)
    {
        LinkedList<Solution> pop = new LinkedList<Solution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            boolean [] b = RandomUtils.randomBitString(r, p);
            BinarySolution s = new BinarySolution();
            s.setBitString(b);
            decode(p, s);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        bestEver = PopulationAlgorithmUtils.getBest(pop, p, bestEver);
        
        int square = (int) Math.sqrt(popsize);
        Solution [][] lattice = new Solution[square][square];
        int offset = 0;
        for (int i = 0; i < lattice.length; i++)
        {
            for (int j = 0; j < lattice[i].length; j++)
            {
                lattice[i][j] = pop.get(offset++);
            }            
        }
        
        // run algorithm until there are no evaluations left
        while(p.isReamainingEvaluations())
        {
            // reproduce
            LinkedList<Solution> children = reproduce(lattice, p);            
            // evaluate
            p.cost(children);
            // test
            bestEver = PopulationAlgorithmUtils.getBest(children, p, bestEver);
            // elitism
            elitism(pop, children, p);
            pop = children;
            notifyListeners(p,pop,bestEver);
        }
        
        return bestEver;
    }
    
    /**
     * Assumed to be sorted
     * @param pop
     * @param children
     * @param p
     */
    protected void elitism(
            LinkedList<Solution> pop, 
            LinkedList<Solution> children, 
            Problem p)
    {   
        for (int i = 0; i < elitism; i++)
        {
            children.removeFirst();
            if(p.isMinimise())
            {
                children.addLast(pop.get(i));
            }
            else
            {
                children.addLast(pop.get(pop.size()-1-i));
            }
        }
    }
    
    protected LinkedList<Solution> getNeighbours(Solution [][] lattice, int i, int j)
    {
        // get neighbours (four of them)
        LinkedList<Solution> neighbours = new LinkedList<Solution>();
        if(i > 0) // above
        {
            neighbours.add(lattice[i-1][j]);
        }
        if(i < lattice.length-1) // below
        {
            neighbours.add(lattice[i+1][j]);
        }
        if(j > 0) // left
        {
            neighbours.add(lattice[i][j-1]);
        }
        if(j < lattice[i].length-1) // right
        {
            neighbours.add(lattice[i][j+1]);
        }
        
        return neighbours;
    }

    
    public LinkedList<Solution> reproduce(Solution [][] lattice, Problem p)
    {
        LinkedList<Solution> children = new LinkedList<Solution>();
        
        for (int i = 0; i < lattice.length; i++)
        {
            for (int j = 0; j < lattice[i].length; j++)
            {
                Solution self = lattice[i][j];
                // get neighbours
                LinkedList<Solution> neighbours = getNeighbours(lattice, i, j);                
                // select other parent
                Solution other = GAUtils.tournamentSelection(neighbours, 1, p, r, boutSize).getFirst();                
                // reproduce
                boolean [][] b = GAUtils.binaryCrossover(((BinarySolution)self).bitString, ((BinarySolution)other).bitString, r, crossover);
                Solution [] c = new Solution[]{new BinarySolution(b[0]), new BinarySolution(b[1])};
                // replacement
                Solution child = (r.nextBoolean() ? c[0] : c[1]); // randomly select one               
                GAUtils.binaryMutate(((BinarySolution)child).bitString, r, mutation);
                decode(p, (BinarySolution)child);
                children.add(child);
                lattice[i][j] = child;
            }
        }
        return children;
    }
    
    protected void decode(Problem p, BinarySolution bs)
    {
        boolean [] b = bs.getBitString();
        double [] coord = BitStringUtils.decode(p, decodeMode, b);
        bs.setCoordinate(coord);
    }
    

    @Override
    public String getName()
    {
        return "Diffuse Genetic Algorithm (Cellular)";
    }


    
    @Override
    protected JPanel getConfigurationPane()
    {
        seed = System.currentTimeMillis();
        crossover = DEFAULT_CROSSOVER;
        mutation = DEFAULT_MUTATION;
        popsize = DEFAULT_POPSIZE;
        boutSize = DEFAULT_BOUTSIZE;
        elitism = DEFAULT_ELITES;
        decodeMode = DEFAULT_DECODE_MODE;
        
        // labels
        JLabel seedLabel = new JLabel("Random number seed:");
        JLabel crossoverLabel = new JLabel("Crossover percentage:");
        JLabel mutationLabel = new JLabel("Mutation percentage:");
        JLabel popsizeLabel = new JLabel("Population size:");
        JLabel boutSizeLabel = new JLabel("Tournament bout size:");
        JLabel elitismLabel = new JLabel("Total elites:");
        JLabel decodeModeLabel = new JLabel("Bit decode mode:");
        
        // fields
        seedField = new JTextField("SYSTIME", 10);
        crossoverField = new JTextField(Double.toString(crossover), 10);
        mutationField = new JTextField(Double.toString(mutation), 10);
        popsizeField = new JTextField(Integer.toString(popsize), 10);
        boutsizeField = new JTextField(Integer.toString(boutSize), 10);
        elitismField = new JTextField(Integer.toString(elitism), 10);
        decodeField = new JComboBox(BitStringUtils.DECODE_MODE.values());
        decodeField.addActionListener(this);
        
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(seedLabel);
        labelPane.add(crossoverLabel);
        labelPane.add(mutationLabel);
        labelPane.add(popsizeLabel);
        labelPane.add(boutSizeLabel);
        labelPane.add(elitismLabel);
        labelPane.add(decodeModeLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(seedField);
        fieldPane.add(crossoverField);
        fieldPane.add(mutationField);
        fieldPane.add(popsizeField);
        fieldPane.add(boutsizeField);
        fieldPane.add(elitismField);
        fieldPane.add(decodeField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        
        return contentPane;
    }
    
    @Override
    public void initialise(Problem p)
    {        
        // seed
        try
        {
            if(seedField.getText().equalsIgnoreCase("SYSTIME"))
            {
                seed = System.currentTimeMillis();
            }
            else
            {
                seed = Long.parseLong(seedField.getText());
            }
        }
        catch(Exception e)
        {
            seed = System.currentTimeMillis();
            seedField.setText("SYSTIME");
        }
        r = new Random(seed);
        // crossover
        try
        {
            crossover = Double.parseDouble(crossoverField.getText());
        }
        catch (Exception e)
        {
            crossover = DEFAULT_CROSSOVER;
            crossoverField.setText("" + crossover);
        }
        finally
        {
            if(crossover>1||crossover<0)
            {
                crossover = DEFAULT_CROSSOVER;
                crossoverField.setText("" + crossover);
            }
        }
        // mutation
        try
        {
            mutation = Double.parseDouble(mutationField.getText());
        }
        catch (Exception e)
        {
            mutation = DEFAULT_MUTATION;
            mutationField.setText("" + mutation);
        }
        finally
        {
            if(mutation>1||mutation<0)
            {
                mutation = DEFAULT_MUTATION;
                mutationField.setText("" + mutation);
            }
        }
        // popsize
        try
        {
            popsize = Integer.parseInt(popsizeField.getText());
        }
        catch (Exception e)
        {
            popsize = DEFAULT_POPSIZE;
            popsizeField.setText("" + popsize);
        }
        finally
        {
            int a = (int) Math.round(Math.sqrt(popsize));
            popsize = a*a; // must be a square
            
            if(popsize>p.getMaxEvaluations()||popsize<TOTAL_NEIGHBOURS)
            {
                popsize = DEFAULT_POPSIZE;
                popsizeField.setText("" + popsize);
            }
        }
        // boutsize
        try
        {
            boutSize = Integer.parseInt(boutsizeField.getText());
        }
        catch (Exception e)
        {
            boutSize = DEFAULT_BOUTSIZE;
            boutsizeField.setText("" + boutSize);
        }
        finally
        {
            if(boutSize>TOTAL_NEIGHBOURS||popsize<0)
            {
                boutSize = DEFAULT_BOUTSIZE;
                boutsizeField.setText("" + boutSize);
            }
        }
        // elitism
        try
        {
            elitism = Integer.parseInt(elitismField.getText());
        }
        catch (Exception e)
        {
            elitism = DEFAULT_ELITES;
            elitismField.setText("" + elitism);
        }
        finally
        {
            if(elitism>popsize||elitism<0)
            {
                elitism = DEFAULT_ELITES;
                elitismField.setText("" + elitism);
            }
        }
    }

    @Override
    protected int getNumParameters()
    {
        return 7;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == decodeField)
        {
            decodeMode = (BitStringUtils.DECODE_MODE) decodeField.getSelectedItem();
        }        
    }
}

