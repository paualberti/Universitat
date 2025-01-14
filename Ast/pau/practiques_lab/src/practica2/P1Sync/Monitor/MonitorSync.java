package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {

    private final int N;

    // Completar...
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private static int actual = 0;

    public MonitorSync(int N) {
        this.N = N;
    }

    public void waitForTurn(int id) {
        // throw new RuntimeException("//Completar...");
        try {
            lock.lock();
            while (id == actual) {
                condition.await();
            }
            actual = (actual + 1 ) % N;
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public void transferTurn() {
        // throw new RuntimeException("//Completar...");
        condition.signal();
        lock.unlock();
    }
}
