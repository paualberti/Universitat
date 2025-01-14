package practica3;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

    protected int MSS; // Maximum Segment Size

    public TSocketSend(SimNet network) {
        super(network);
        MSS = 5;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        // throw new RuntimeException("//Completar...");
        while (length > 0) {
            int len = Math.min(MSS, length);
            TCPSegment tcpSegment = new TCPSegment();
            tcpSegment = segmentize(data, offset, len);
            offset += len;
            length -= len;
            printSndSeg(tcpSegment);
            // log.printBLUE("Sender: sent " + MSS + " bytes");
            network.send(tcpSegment);
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        // throw new RuntimeException("//Completar...");
        TCPSegment tcpSegment = new TCPSegment();
        tcpSegment.setData(data, offset, length);
        tcpSegment.setPsh(true);
        return tcpSegment;
    }

}
