package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        // throw new RuntimeException("//Completar...");
        int x = 0;
        do {
            System.out.println(x);
            CounterThread.x = 0;
            CounterThread aThread = new CounterThread();
            CounterThread bThread = new CounterThread();
            aThread.start();
            bThread.start();
            aThread.join();
            bThread.join();
            x = CounterThread.x;
        } while (x != 20000);
        System.out.println(CounterThread.x);
    }
}
