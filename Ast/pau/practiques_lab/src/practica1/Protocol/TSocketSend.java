package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

import util.Log;

public class TSocketSend extends TSocket_base {

  public TSocketSend(SimNet network) {
    super(network);
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    // throw new RuntimeException("//Completar...");
    Log log = Log.getLog();
    TCPSegment tcpSegment = new TCPSegment();
    tcpSegment.setData(data, offset, length);
    tcpSegment.setPsh(true);
    printSndSeg(tcpSegment);
    log.printBLUE("Sender: sent " + length + " bytes");
    network.send(tcpSegment);
  }
}
