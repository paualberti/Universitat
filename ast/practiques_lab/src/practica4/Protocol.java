package practica4;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

  public Protocol(SimNet network) {
    super(network);
  }

  protected void ipInput(TCPSegment seg) {
    // throw new RuntimeException("//Completar...");
    TSocket_base tSocket_base = getMatchingTSocket(seg.getSourcePort(), seg.getDestinationPort());
    if (tSocket_base == null) {
      return;
    }
    tSocket_base.processReceivedSegment(seg);
  }

  protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
    lk.lock();
    try {
      // throw new RuntimeException("//Completar...");
      for (TSocket_base tSocket_base : activeSockets) {
        if (localPort == tSocket_base.getLocalPort() && remotePort == tSocket_base.getRemotePort()) {
          return tSocket_base;
        }
      }
      return null;
    } finally {
      lk.unlock();
    }
  }
}
