package practica3;

import java.util.Random;
import util.Const;
import util.TCPSegment;

public class SimNet_Loss extends practica2.Protocol.SimNet_Monitor {

    private double lossRate;
    private Random rand;

    public SimNet_Loss(double lossRate) {
        this.lossRate = lossRate;
        rand = new Random(Const.SEED);
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
