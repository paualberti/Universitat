package practica7;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

    protected Protocol(SimNet network) {
        super(network);
    }

    public void ipInput(TCPSegment segment) {
        TSocket_base s = getMatchingTSocket(segment.getDestinationPort(), segment.getSourcePort());
        if (s != null) {
            s.processReceivedSegment(segment);
        } else {
            log.printRED("Null socket.");
        }
    }

    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for (TSocket_base s : activeSockets) {
                if (s.localPort == localPort && s.remotePort == remotePort) {
                    return s;
                }
            }
            for (TSocket_base s : listenSockets) {
                if (s.localPort == localPort) {
                    return s;
                }
            }
            return null;
        } finally {
            lk.unlock();
        }
    }

}
