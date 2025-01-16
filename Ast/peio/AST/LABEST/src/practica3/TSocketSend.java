package practica3;

import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

    protected int MSS;       // Maximum Segment Size

    public TSocketSend(SimNet network) {
        super(network);
        MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        int len;
        TCPSegment seg;
        while (length > 0) {
            len = Math.min(MSS, length);
            seg = segmentize(data, offset, len);
            length -= len;
            offset += len;
            network.send(seg);
            printSndSeg(seg);
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        seg.setData(data, offset, length);
        seg.setPsh(true);
        return seg;
    }

}
