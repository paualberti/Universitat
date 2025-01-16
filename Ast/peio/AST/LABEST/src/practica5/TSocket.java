package practica5;

import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TSocket_base;
import util.TCPSegment;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvWnd;
    protected int snd_rcvNxt;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcv_SegConsumedBytes;
    protected int rcv_rcvNxt;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);

        // init sender variables
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;

        // init receiver variables
        rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        //rcv_Queue = new CircularQueue<>(2);

    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            TCPSegment seg;
            int len;
            boolean prev_zero_wnd_probe;
            while (length > 0) {
                while (snd_sndNxt != snd_rcvNxt) {
                    appCV.awaitUninterruptibly();
                }
                prev_zero_wnd_probe = zero_wnd_probe_ON;
                zero_wnd_probe_ON = snd_rcvWnd == 0;
                if (zero_wnd_probe_ON && !prev_zero_wnd_probe) {
                    log.printGREEN("Zero-window probe ON");
                } else if (!zero_wnd_probe_ON && prev_zero_wnd_probe) {
                    log.printGREEN("Zero-window probe OFF");
                }
                if (zero_wnd_probe_ON) {
                    len = 1;
                } else {
                    len = Math.min(MSS, length);
                }
                seg = segmentize(data, offset, len);
                length -= len;
                offset += len;
                network.send(seg);
                startRTO(seg);
                printSndSeg(seg);
                snd_rcvWnd--;
                snd_sndNxt++;
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        seg.setData(data, offset, length);
        seg.setPsh(true);
        seg.setSeqNum(snd_sndNxt);
        return seg;
    }

    @Override
    protected void timeout(TCPSegment seg) {
        lock.lock();
        try {
            if (snd_rcvNxt <= seg.getSeqNum()) {
                network.send(seg);
                log.printRED("Retransmissió.");
                startRTO(seg);
                printSndSeg(seg);
            }
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        lock.lock();
        try {
            while (rcvQueue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int gottenBytes = 0;
            while (gottenBytes < length && !rcvQueue.empty()) {
                gottenBytes += consumeSegment(buf, offset + gottenBytes, length - gottenBytes);
            }
            return gottenBytes;
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcvQueue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcvQueue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment seg = new TCPSegment();
        seg.setAck(true);
        seg.setAckNum(rcv_rcvNxt);
        seg.setWnd(rcvQueue.free());
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        network.send(seg);
        printSndSeg(seg);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            printRcvSeg(rseg);
            if (rseg.isAck()) {
                if (rseg.getAckNum() > snd_rcvNxt) {
                    snd_rcvNxt = rseg.getAckNum();
                    snd_rcvWnd = rseg.getWnd();
                    appCV.signalAll();
                }
            } else if (rseg.isPsh()) {
                if (rcvQueue.full() || rseg.getSeqNum() != rcv_rcvNxt) {
                    log.printRED("Paquet perdut en recepció.");
                    sendAck();
                } else {
                    rcvQueue.put(rseg);
                    rcv_rcvNxt++;
                    sendAck();
                    appCV.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
