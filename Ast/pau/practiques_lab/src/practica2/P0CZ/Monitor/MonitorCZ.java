package practica2.P0CZ.Monitor;

import java.util.concurrent.locks.ReentrantLock;

public class MonitorCZ {

    private int x = 0;
    // Completar...
    ReentrantLock lock = new ReentrantLock();

    public void inc() {
        // Incrementa en una unitat el valor d'x
        // throw new RuntimeException("//Completar...");
        lock.lock();
        x++;
        lock.unlock();
    }

    public int getX() {
        // Ha de retornar el valor d'x
        // throw new RuntimeException("//Completar...");
        return x;
    }

}
