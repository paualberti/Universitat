package practica2.P0CZ;

public class CounterThread extends Thread {

    public static int x;
    static final int I = 10000;

    @Override
    public void run() {
        for (int i = 0; i < I; i++) {
            x = x + 1;
        }
    }
}
