package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
        // throw new RuntimeException("//Completar...");
        MonitorCZ mCz = new MonitorCZ();
        CounterThreadCZ aCz = new CounterThreadCZ(mCz);
        CounterThreadCZ bCz = new CounterThreadCZ(mCz);
        aCz.start();
        bCz.start();
        aCz.join();
        bCz.join();
        System.out.println(mCz.getX());
    }
}
