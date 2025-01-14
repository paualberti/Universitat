package practica4;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    //sender variable:
    protected int MSS;

    //receiver variables:
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegConsumedBytes;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        rcvSegConsumedBytes = 0;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        TCPSegment seg;
        int len;
        while (length > 0) {
            len = Math.min(MSS, length);
            seg = segmentize(data, offset, len);
            length -= len;
            offset += len;
            network.send(seg);
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

    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        lock.lock();
        try {
            while (rcvQueue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int gottenBytes, count = 0;
            while (count < length && !rcvQueue.empty()) {
                gottenBytes = consumeSegment(buf, offset + count, length - count);
                count += gottenBytes;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcvQueue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcvSegConsumedBytes);
        System.arraycopy(seg.getData(), rcvSegConsumedBytes, buf, offset, a_agafar);
        rcvSegConsumedBytes += a_agafar;
        if (rcvSegConsumedBytes == seg.getDataLength()) {
            rcvQueue.get();
            rcvSegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment seg = new TCPSegment();
        seg.setAck(true);
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        network.send(seg);
    }

    @Override
    public void processReceivedSegment(TCPSegment rseg) {

        lock.lock();
        try {
            printRcvSeg(rseg);
            if (rseg.isAck()) {
                //nothing to be done in this exercise.
            }
            if (rseg.isPsh()) {
                if (rcvQueue.full()) {
                    log.printRED("Paquet perdut en recepció");
                    return;
                }
                rcvQueue.put(rseg);
                appCV.signal();
                sendAck();
            }
        } catch (IllegalStateException e) {
        } finally {
            lock.unlock();
        }
    }

}
