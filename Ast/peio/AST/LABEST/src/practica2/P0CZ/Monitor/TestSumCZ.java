package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
        MonitorCZ mon = new MonitorCZ();
        CounterThreadCZ t1, t2;
        t1 = new CounterThreadCZ(mon);
        t2 = new CounterThreadCZ(mon);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(mon.getX());
    }
}
