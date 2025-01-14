package practica3;

import util.Receiver;
import util.Sender;
import util.SimNet;

public class Test {

    public static void main(String[] args) {
        SimNet net = new SimNet_Loss(0.5);
        new Sender(new TSocketSend(net), 20, 5, 100).start();
        new Receiver(new TSocketRecv(net), 20, 10).start();
    }
}
