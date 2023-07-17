
package jb.selfregulation.application.network;

import java.util.LinkedList;

import jb.selfregulation.Unit;

public interface PortalOutgoing
{
    void sendUnitsToRandomNeighbour(Unit [] aUnits);
    
    void sendUnitsToRandomNeighbour(LinkedList<Unit> aUnits);
}
