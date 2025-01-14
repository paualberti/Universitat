package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitorSync {

    private final int N;
    private ReentrantLock lock;
    private int lastId;
    private Condition c;
    
    
    //Completar...

    public MonitorSync(int N) {
        this.N = N;
        lock = new ReentrantLock();
        c = lock.newCondition();
        lastId = -1;
        
    }

    public void waitForTurn(int id){
        lock.lock();
        while (lastId == id){
            try {
                c.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(MonitorSync.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lastId = id;
    }

    public void transferTurn() {
        c.signal();
        lock.unlock();
    }
}
