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
        TCPSegment seg = network.receive();
        byte[] segBin = seg.getData();
        int count = 0;
        try {
            for (int i = 0; i < length; i++) {
                data[i] = segBin[i + offset];
                count++;
            }
        } catch (IndexOutOfBoundsException e) {
            return count;
        }
        return length;
    }
}
