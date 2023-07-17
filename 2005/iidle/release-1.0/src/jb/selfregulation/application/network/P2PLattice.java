
package jb.selfregulation.application.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jb.selfregulation.FileUtil;
import jb.selfregulation.Lattice;
import jb.selfregulation.Unit;
import jb.selfregulation.application.Configurable;
import jb.selfregulation.application.Loggable;
import jb.selfregulation.application.SystemState;
import jb.selfregulation.impl.message.UnitPayload;
import jb.selfregulation.processes.ParallelProcesses;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.RouteMessage;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;


/**
 * Type: P2PLattice<br/>
 * Date: 21/06/2005<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 */
public class P2PLattice 
    implements Application, Loggable, PortalOutgoing, PortalIncoming, Configurable
{    
    public final static String P2P_APP_NAME = "P2PLattice";
    
    protected Logger logger;
    protected InetSocketAddress bootHost;
    protected Endpoint endpoint;
    protected PastryNode node;    
    protected LinkedList<InetSocketAddress> loadedBootList;    
    protected LinkedList<Unit> unitsIncoming;
    protected Set<NodeHandle> distinctLeafSet;
    protected NodeHandle [] distinctLeafArray;
    
    protected Random rand;
    protected Lattice lattice;
    protected LinkedList<ParallelProcesses> processes;
    
    // configs
    protected boolean networkEnabled;
    protected String bootFilename;
    protected int localPort; 
    protected int portalsPerNeighbour;
    

    public P2PLattice()
    {
        logger = Logger.getLogger(LOG_CONFIG);
    }
    
    public String getBase()
    {
        return ".network";
    }    
    public void loadConfig(String aBase, Properties prop)
    {
        String b = aBase + getBase();
        networkEnabled = Boolean.parseBoolean(prop.getProperty(b + ".enabled"));
        if(networkEnabled)
        {
            bootFilename = prop.getProperty(b + ".boot.filename");
            localPort = Integer.parseInt(prop.getProperty(b + ".localport"));
            portalsPerNeighbour = Integer.parseInt(prop.getProperty(b + ".portals"));
        }
    }    
    public void setup(SystemState aState)
    {
        // prepare data structures
        distinctLeafSet = new HashSet<NodeHandle>();
        unitsIncoming = new LinkedList<Unit>();
        // preapare other things
        rand = aState.rand;
        lattice = aState.lattice;
        processes = aState.processes;
        
        // prepare network
        if(networkEnabled)
        {
            // load boot list
            loadBootList();
            // boot the node
            boot();
            
            logger.config("Network prepared.");
        }
        else
        {
            logger.config("Network disabled.");
        }
    }
    
    public void stopNetwork()
    {
        // TODO
    }    
   
    
    protected void boot()
    {
        // prepare the node factory
        NodeIdFactory nidFactory = new RandomNodeIdFactory();
        SocketPastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, localPort);
        NodeHandle bootHandle = null;        
        
        // prepare boot node
        boolean booted = false;
        for (Iterator<InetSocketAddress> iter = loadedBootList.iterator(); !booted && iter.hasNext();)
        {
            InetSocketAddress add = iter.next();
            
            // TODO: failover for hosts
            try
            {                
                bootHandle = factory.getNodeHandle(add);
                booted = true;
                bootHost = add;
                logger.info("Connected to host: " + add);
            }
            catch(Exception e)
            {
                logger.info("Unable to connect to boot address: " + add);
                booted = false;
            }
        }
        
        if(!booted)
        {
            // need to start a new network
            logger.info("Unable to connect to connect to host, starting new network.");
        }
        
        // TODO : support proxy?
        
        // prepare the node
        node = factory.newNode(bootHandle);
        // prepare the endpoint
        endpoint = node.registerApplication(this, P2P_APP_NAME);
        // wait for the node to be ready
        waitForNodeToBeReady();
    }
    
    
    
    protected void waitForNodeToBeReady()
    {
        try
        {
            while (!node.isReady())
            {
                Thread.sleep(100);
            }
        }
        catch(Exception e){}
    }    
    protected void loadBootList()    
    {           
        loadedBootList = new LinkedList<InetSocketAddress>();
        
        // load file
        String fileData = null;
        try
        {
            fileData = FileUtil.loadFile(bootFilename);
        }
        catch (IOException e1)
        {
            getLogger().log(Level.WARNING, "Unable to load boot file ("+bootFilename+"), assuming self as root/boot node.", e1);
            return;
        }
        
        String [] names = fileData.trim().split("\n");
        for (int i = 0; i < names.length; i++)
        {
            InetSocketAddress address = null;
            
            try
            {
                String [] parts = names[i].split(":");
                String sAddress = parts[0];
                int lPort = Integer.parseInt(parts[1]);
                address = new InetSocketAddress(sAddress, lPort);
            }
            catch(Exception e)
            {
                getLogger().log(Level.WARNING, "Host in boot list ("+names[i]+") is invalid, removing from list.", e);
            }
            
            loadedBootList.add(address);
        }        
    }
    
    protected void writeBootList()
    {
        // TODO
        
        // get all neighbours
        
        // add neighbours to boot list
        
        // write all enteries to boot list
    }
    

    public NodeHandle getRandomlySelectedNeighbour()
    {
        if(distinctLeafArray != null && distinctLeafArray.length != 0)
        {
            int index = rand.nextInt(distinctLeafArray.length);
            return distinctLeafArray[index];
        }
        
        return null;
    }   
    protected int getDistinctNeighbourhoodSize()
    {        
        return distinctLeafSet.size();        
    }
    public boolean hasNeighbours()
    {
        return !distinctLeafSet.isEmpty();
    }   
    
    protected void updateDistinctLeafSet()
    {
        LeafSet leafSet = node.getLeafSet();
        distinctLeafSet.clear(); 
    
        // work around the ring
        for (int i = -leafSet.ccwSize(); i <= leafSet.cwSize(); i++) 
        {
            // never send to self
            if (i != 0) 
            {
                distinctLeafSet.add(leafSet.get(i));
            }
        }    
        // prepare a list version
        distinctLeafArray = distinctLeafSet.toArray(new NodeHandle[distinctLeafSet.size()]);
    }
    
    public void update(rice.p2p.commonapi.NodeHandle handle, boolean joined)
    {
        // update neighbourhood
        updateDistinctLeafSet();
        // update lattice configuration
        refreshPortals();
    }
    
    public void refreshPortals()
    {        
        // determine how many neighbours we have
        int total = getDistinctNeighbourhoodSize();   
        // rescale the number of portals
        int portals = total * portalsPerNeighbour;
        // update the number of portals
        lattice.setPortals(portals, rand);
        logger.info("Portals set to " + total);
    }
    
    
    public void deliver(Id id, Message message)
    {        
        if(message instanceof UnitPayload)
        {
            // add units in comming
            addUnitsIncoming((UnitPayload)message);
        }
        else
        {   
            // unknown message
            logger.severe("Unknown message type ("+message.getClass().getName()+"), from ("+id.toStringFull()+"): " + message.toString());
        }        
    }
    
    public boolean forward(RouteMessage message)
    {
        // TODO : implement this
//        logger.warning("Requested to foward message :: foward is not implemented, this message is lost. TODO!!!");
//        return false;
        
        return true;
    }    

    public Logger getLogger()
    {
        return logger;
    }
    
    protected void sendUnitsToNode(Unit [] aUnits, NodeHandle aNode)
    {
        if(hasNeighbours())
        {
            UnitPayload payload = new UnitPayload(aUnits);
            sendMessage(payload, aNode);
        }
    }
    
    protected void sendUnitsToRandomNode(Unit [] aUnits)
    {
        if(hasNeighbours())
        {
            UnitPayload payload = new UnitPayload(aUnits);
            NodeHandle neighbour = getRandomlySelectedNeighbour();
            sendMessage(payload, neighbour);
        }
    }    
    
    
    protected void sendMessage(Message aMessage, NodeHandle aHandle)
    {
        // send directly
        endpoint.route(aHandle.getId(), aMessage, null);
    }
    
    
    public LinkedList<Unit> getAllUnitsIncomming()
    {
        LinkedList<Unit> units = new LinkedList<Unit>();
        
        synchronized (unitsIncoming)
        {
            if(!unitsIncoming.isEmpty())
            {
                // copy
                units.addAll(unitsIncoming);
                // remove all
                unitsIncoming.clear();
            }
        }
        
        return units;
    }
    
    public void addUnitsIncoming(UnitPayload aPayload)
    {
        // must be running to add units
        if(processes.getFirst().isPaused() || processes.getFirst().isStopped())
        {
            logger.info("Unable to add units, processes are not running. Discarded " + aPayload.getUnits().length);
            return; // do nothing while not running
        }
        
        synchronized (unitsIncoming)
        {
            Unit [] units = aPayload.getUnits();
            
            for (int i = 0; i < units.length; i++)
            {
                unitsIncoming.add(units[i]);
            }
        }
    }
    
    public boolean isUnitsIncoming()
    {
        synchronized (unitsIncoming)
        {
            return !unitsIncoming.isEmpty();
        }
    }
    public void sendUnitsToRandomNeighbour(Unit [] aUnits)
    {
        sendUnitsToRandomNode(aUnits);        
    }
    public void sendUnitsToRandomNeighbour(LinkedList<Unit> aUnits)
    {
        sendUnitsToRandomNeighbour(aUnits.toArray(new Unit[aUnits.size()]));        
    }
    public boolean isNetworkEnabled()
    {
        return networkEnabled;
    }    
}
