package practica3;

import java.util.Random;
import util.Const;
import util.Log;
import util.TCPSegment;

public class SimNet_Loss extends SimNet_Monitor {

    private double lossRate;
    private Random rand;
    private Log log;

    public SimNet_Loss(double lossRate) {
        this.lossRate = lossRate;
        rand = new Random(Const.SEED);
        log = Log.getLog();
    }

    @Override
    public void send(TCPSegment seg) {
        // throw new RuntimeException("//Completar...");
        if (lossRate > rand.nextDouble(0, 1)) {
            // throw new RuntimeException("//Completar...");
            try {
                lock.lock();
                while (queue.full()) {
                    c2.await();
                }
                queue.put(seg);
                c1.signal();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                lock.unlock();
            }
        } else {
            try {
                lock.lock();
                byte[] data = seg.getData();
                int start = Const.IP_HEADER + Const.TCP_HEADER;
                int len = data.length - start;
                byte[] send = new byte[len];
                for (int i = 0; i < len - start; i++) {
                    send[i] = data[len - i - 1];
                }
                TCPSegment tcpSegment = new TCPSegment();
                tcpSegment.setData(send, 0, len);
                tcpSegment.setPsh(true);
                queue.put(tcpSegment);
                c1.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
