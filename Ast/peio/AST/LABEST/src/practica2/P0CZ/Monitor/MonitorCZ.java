package practica2.P0CZ.Monitor;

import java.util.concurrent.locks.ReentrantLock;

public class MonitorCZ {

    private int x = 0;
    ReentrantLock l = new ReentrantLock();

    public void inc() {
        l.lock();
        try {
            //Incrementa en una unitat el valor d'x
            x++;
        } finally {
            l.unlock();
        }
    }

    public int getX() {
        //Ha de retornar el valor d'x
        return x;
    }

}
