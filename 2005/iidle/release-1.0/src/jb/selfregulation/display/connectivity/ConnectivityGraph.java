
package jb.selfregulation.display.connectivity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import jb.selfregulation.Cell;
import jb.selfregulation.Lattice;
import jb.selfregulation.LatticeStatusListener;
import jb.selfregulation.application.SystemState;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.ZoomPanGraphMouse;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;

public class ConnectivityGraph extends JPanel 
    implements LatticeStatusListener, ActionListener
{
    public final static String CELL_KEY = "CELL_KEY";
    
    protected Lattice lattice;   
    protected DirectedGraph graph;
    protected VisualizationViewer viewer;
    
    protected volatile int lastTotalLocalities;
    
    protected JRadioButton circleLayout;
    protected JRadioButton frLayout;    
    protected JRadioButton isomLayout;
    protected JRadioButton kkLayout;
    protected JRadioButton springLayout;
    

    public ConnectivityGraph()
    {}
    
    public void latticeChangedEvent(Lattice aLattice)
    {
        // prepare the graph structure
        LinkedList<Cell> cells = aLattice.getDuplicateCellList();
        if(cells.size() != lastTotalLocalities)
        {
            resize(cells);
        }        
        
        // repaint things
        repaint();
    }
    
    public void resize(final LinkedList<Cell> cells)
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                setEnabled(false);
                setVisible(false);
                viewer.stop();                
                prepareGraph(cells);
                viewer.setGraphLayout(new KKLayout(graph));
                // redraw things
                try
                {
                    viewer.restart();
                }
                catch(Exception ee)
                {}// ignore
                setEnabled(true);
                setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    
    public String getBase()
    {
        return "connectivitygraph";
    }
    public void loadConfig(String aBase, Properties prop)
    {}
    public void setup(SystemState aState)
    {
        lattice = aState.lattice;        
        prepareGui();
        // add to common panels
        ((LinkedList<JComponent>)aState.getUserDatum(SystemState.KEY_COMMON_PANELS)).add(this);
    }
    
    
    
    protected JPanel getControlPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Connectivity Display Control"));
        
        circleLayout = new JRadioButton("Circle Layout", false);
        frLayout = new JRadioButton("FR Layout", false);
        isomLayout = new JRadioButton("ISOM Layout", false);
        kkLayout = new JRadioButton("KK Layout", true);
        springLayout = new JRadioButton("Spring Layout", false);
        
        circleLayout.addActionListener(this);
        frLayout.addActionListener(this);
        isomLayout.addActionListener(this);
        kkLayout.addActionListener(this);
        springLayout.addActionListener(this);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(circleLayout);
        bg.add(frLayout);
        bg.add(isomLayout);
        bg.add(kkLayout);
        bg.add(springLayout);
        
        p.add(circleLayout);
        p.add(frLayout);
        p.add(isomLayout);
        p.add(kkLayout);
        p.add(springLayout);
        
        return p;
    }
    
    protected void prepareGui()
    {
        // prepare the graph
        graph = new DirectedSparseGraph();
        prepareGraph(lattice.getDuplicateCellList());
        
        // prepare the graph renderer
        PluggableRenderer graphRenderer = new PluggableRenderer();
        Layout layout = new KKLayout(graph);
        viewer = new VisualizationViewer(layout, graphRenderer);   
        viewer.setBackground(Color.white);
        viewer.setPickSupport(new ShapePickSupport(viewer));
        
        // prepare renders
        CellVertexPainter vertexPainter = new CellVertexPainter();
        graphRenderer.setVertexPaintFunction(vertexPainter);
        EdgeWeightStrokeFunction edgePainter = new EdgeWeightStrokeFunction();
        graphRenderer.setEdgeStrokeFunction(edgePainter);
        CellVertexSizePainter sizePainter = new CellVertexSizePainter();
        graphRenderer.setVertexShapeFunction(sizePainter);        
        
        // permit scrolling
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(viewer);
        // permit zooming with the mouse
        ZoomPanGraphMouse gm = new ZoomPanGraphMouse(viewer);
//        gm.setZoomAtMouse(true);
        viewer.setGraphMouse(gm);
        
        JPanel p = getControlPanel();
        
        setName("Connectivity");
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
    }
    
    protected void prepareGraph(LinkedList<Cell> cells)
    {
        graph.removeAllEdges();
        graph.removeAllVertices();
        lastTotalLocalities = cells.size();
        
        if(cells.size() < 3)
        {
            return;
        }
        
        // create all verticies        
        for(Cell c : cells)
        {
            // prepare the vertice
            SimpleDirectedSparseVertex vertex = new SimpleDirectedSparseVertex();
            vertex.addUserDatum(CELL_KEY, c, new UserDataContainer.CopyAction.Shared());
            graph.addVertex(vertex);
        }
                
        // prepare all connectivity
        Set allVerticies = graph.getVertices();
        for (Iterator iter = allVerticies.iterator(); iter.hasNext();)
        {
            SimpleDirectedSparseVertex v = (SimpleDirectedSparseVertex) iter.next();
            Cell cell = (Cell) v.getUserDatum(CELL_KEY);
            // get all neighbouring verticies
            LinkedList<SimpleDirectedSparseVertex> neigh = getNeighbours(cell, allVerticies);
            for(SimpleDirectedSparseVertex s : neigh)
            {
                // prepare the edge
                DirectedSparseEdge edge = new DirectedSparseEdge(v, s);
                // add the edge to the graph
                graph.addEdge(edge);
            }
        }        
    }
    
//    protected void paintComponent(Graphics g)
//    {
//        super.paintComponent(g);
//    }
     
    public LinkedList<SimpleDirectedSparseVertex> getNeighbours(Cell aCell, Set allVerticies)
    {
        LinkedList<SimpleDirectedSparseVertex> list = new LinkedList<SimpleDirectedSparseVertex>();
        
        LinkedList<Cell> neighbours = aCell.getNeighbours();
        for(Cell c : neighbours)
        {
            boolean found = false;
            
            // locate vertex with this node as its user data
            for (Iterator iter = allVerticies.iterator(); !found && iter.hasNext();)
            {
                SimpleDirectedSparseVertex v = (SimpleDirectedSparseVertex) iter.next();
                Cell u = (Cell) v.getUserDatum(CELL_KEY);
                if(u == c)
                {
                    list.add(v);
                    found = true;
                }
            }
            
            if(!found)
            {
                //throw new RuntimeException("Unable to locate vertex with cell.");
            }
        }
        
        return list;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == circleLayout)
        {
            viewer.setGraphLayout(new CircleLayout(graph));
        }
        else if(src == frLayout)
        {
            viewer.setGraphLayout(new FRLayout(graph));
        }
        else if(src == isomLayout)
        {
            viewer.setGraphLayout(new ISOMLayout(graph));
        }
        else if(src == kkLayout)
        {
            viewer.setGraphLayout(new KKLayout(graph));
        }
        else if(src == springLayout)
        {
            viewer.setGraphLayout(new SpringLayout(graph));
        }
        
        // redraw things
        try
        {
            viewer.restart();
        }
        catch(Exception ee)
        {}// ignore
    }        
}
