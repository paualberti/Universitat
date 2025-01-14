package practica6;

import java.util.HashMap;
import java.util.Map;
import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvNxt;
    protected int snd_rcvWnd;
    protected int snd_cngWnd;
    protected int snd_minWnd;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected int rcv_rcvNxt;
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;
    protected Map<Integer, TCPSegment> out_of_order_segs;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);

        // init sender variables:
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        //MSS = 10;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 3;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);

        // init receiver variables:
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        out_of_order_segs = new HashMap<>();
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
                while (snd_sndNxt >= snd_rcvNxt + snd_minWnd && snd_cngWnd == 0) {
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
                snd_rcvWnd--;
                snd_cngWnd--;
                snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
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
            if (snd_rcvNxt == seg.getSeqNum()) {
                network.send(seg);
                log.printRED("Retransmissió.");
            }
            if (snd_rcvNxt <= seg.getSeqNum()) {
                startRTO(seg);
            }
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            while (rcv_Queue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int gottenBytes = 0;
            while (gottenBytes < maxlen && !rcv_Queue.empty()) {
                gottenBytes += consumeSegment(buf, offset + gottenBytes, maxlen - gottenBytes);
            }
            return gottenBytes;
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcv_Queue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcv_Queue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment seg = new TCPSegment();
        seg.setAck(true);
        seg.setAckNum(rcv_rcvNxt);
        seg.setWnd(rcv_Queue.free());
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        network.send(seg);
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
                    snd_cngWnd++;
                    snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
                    appCV.signalAll();
                }
            } else if (rseg.isPsh()) {
                if (rcv_Queue.full() || rseg.getSeqNum() < rcv_rcvNxt) {
                    log.printRED("Paquet perdut en recepció.");
                    sendAck();
                } else {
                    if (rseg.getSeqNum() == rcv_rcvNxt) {
                        rcv_Queue.put(rseg);
                        log.printPURPLE("receiver - introduit el: " + rseg.getSeqNum());
                        rcv_rcvNxt++;
                        while (out_of_order_segs.containsKey(rcv_rcvNxt)) {
                            rcv_Queue.put(out_of_order_segs.get(rcv_rcvNxt));
                            log.printPURPLE("receiver - introduit en ordre el: " + rcv_rcvNxt);
                            out_of_order_segs.remove(rcv_rcvNxt);
                            rcv_rcvNxt++;
                        }
                        sendAck();
                        appCV.signalAll();
                    } else {
                        out_of_order_segs.put(rseg.getSeqNum(), rseg);
                        log.printPURPLE("receiver - guardat fora d'ordre el: " + rseg.getSeqNum());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
