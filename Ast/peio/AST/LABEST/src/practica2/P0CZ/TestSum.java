package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        CounterThread t1, t2;
        t1 = new CounterThread();
        t2 = new CounterThread();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(CounterThread.x+" El resultat esperat és: "+(CounterThread.I)*2);
    }
}
