package practica2.Protocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

    protected CircularQueue<TCPSegment> queue;
    //Completar
    protected ReentrantLock lock;
    protected Condition full, empty;

    public SimNet_Monitor() {
        queue = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
        //Completar
        lock = new ReentrantLock();
        full = lock.newCondition();
        empty = lock.newCondition();
    }

    @Override
    public void send(TCPSegment seg) {
        lock.lock();
        try {
            while (queue.full()) {
                full.awaitUninterruptibly();
            }
            queue.put(seg);
            empty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TCPSegment receive() {
        lock.lock();
        try {
            while (queue.empty()) {
                empty.awaitUninterruptibly();
            }
            return queue.get();
        } finally {
            full.signal();
            lock.unlock();
        }
    }

    @Override
    public int getMTU() {
        throw new UnsupportedOperationException("Not supported yet. NO cal completar fins a la pràctica 3...");
    }

}
