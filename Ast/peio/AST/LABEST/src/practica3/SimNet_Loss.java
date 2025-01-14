package practica3;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Const;
import util.Log;
import util.TCPSegment;

public class SimNet_Loss extends practica2.Protocol.SimNet_Monitor {

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
        lock.lock();
        try {
            while (queue.full()) {
                full.awaitUninterruptibly();
            }
            double numAleat = rand.nextDouble();
            if (numAleat >= lossRate) {
                queue.put(seg);
                empty.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getMTU() {
        return Const.MTU_ETHERNET;
    }
}
