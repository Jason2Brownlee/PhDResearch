
package jb.selfregulation.impl.message;

import rice.p2p.commonapi.Message;

public class ApplicationMessage implements Message
{

    public ApplicationMessage()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public int getPriority()
    {
        return Message.LOW_PRIORITY;
    }

}
