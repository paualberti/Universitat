package practica3;

import java.util.concurrent.locks.*;
import practica3.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

    protected CircularQueue<TCPSegment> queue;
    // Completar
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition c1 = lock.newCondition();
    protected Condition c2 = lock.newCondition();

    public SimNet_Monitor() {
        queue = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
        // Completar
    }

    @Override
    public void send(TCPSegment seg) {
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
    }

    @Override
    public TCPSegment receive() {
        // throw new RuntimeException("//Completar...");
        try {
            lock.lock();
            while (queue.empty()) {
                c1.await();
            }
            TCPSegment tcp = queue.get();
            c2.signal();
            return tcp;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getMTU() {
        // throw new UnsupportedOperationException("Not supported yet. NO cal completar
        // fins a la pràctica 3...");
        return Const.MTU_ETHERNET;
    }

}
