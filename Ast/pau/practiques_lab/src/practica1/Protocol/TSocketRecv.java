package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketRecv extends TSocket_base {

  public TSocketRecv(SimNet network) {
    super(network);
  }

  @Override
  public int receiveData(byte[] data, int offset, int length) {
    // throw new RuntimeException("//Completar...");
    TCPSegment tcpSegment = network.receive();
    byte[] recieved = tcpSegment.getData();
    for (int i = 0; i < recieved.length; i++) {
      data[i] = recieved[offset + i];
    }
    tcpSegment.setPsh(true);
    printRcvSeg(tcpSegment);
    return recieved.length;
  }
}
