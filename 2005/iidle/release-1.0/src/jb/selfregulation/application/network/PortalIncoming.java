
package jb.selfregulation.application.network;

import java.util.LinkedList;

import jb.selfregulation.Unit;
import jb.selfregulation.impl.message.UnitPayload;

public interface PortalIncoming
{
    LinkedList<Unit> getAllUnitsIncomming();    
    
    void addUnitsIncoming(UnitPayload aPayload);   
    
    boolean isUnitsIncoming();
}
