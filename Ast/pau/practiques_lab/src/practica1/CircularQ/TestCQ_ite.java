package practica1.CircularQ;

import practica1.LinkedQ.LinkedQueue;

import java.util.Iterator;
import java.util.Random;

public class TestCQ_ite {

    public static void main(String[] args) {

        // CircularQueue<Integer> cQueue = new CircularQueue<Integer>(30);
        // Random random = new Random();
        // while (!cQueue.full()) {
        // int rand = random.nextInt(1, 366);
        // cQueue.put(rand);
        // }
        // Iterator<Integer> iterator = cQueue.iterator();

        // LinkedQueue<Integer> lQueue = new LinkedQueue<Integer>();
        // Iterator<Integer> iterator2 = lQueue.iterator();
        // for (int i = 0; iterator.hasNext(); i++) {
        // int value = iterator.next();
        // iterator2 = lQueue.iterator();
        // for (int j = 0; iterator2.hasNext(); j++) {
        // int value2 = iterator2.next();
        // if (value == value2) {
        // System.out.println(value + " at pos: " + j + " and " + i);
        // break;
        // }
        // }
        // lQueue.put(value);
        // }
        // System.out.println();
        // System.out.println("Queue content: " + cQueue);
        // iterator = cQueue.iterator();
        // iterator2 = lQueue.iterator();
        // while (iterator.hasNext() && iterator2.hasNext()) {
        // iterator.next();
        // iterator2.next();
        // iterator.remove();
        // iterator2.remove();
        // }
        // System.out.println("Queue content: " + cQueue);

        CircularQueue<Integer> q = new CircularQueue<>(100000);
        for (int i = 0; i < 100000; i++) {
            q.put(i);
        }
        //System.out.println("Queue content: " + q);

        Iterator<Integer> ite = q.iterator();
        System.out.println("removing");

        //System.out.println("we iterate over the queue elements to take 0,4,5 and 9, if present:");
        while (ite.hasNext()) {
            int valor = ite.next();
            //if (valor == 0 || valor == 4 || valor == 5 || valor == 9) {
            //    ite.remove();
            //    System.out.println("taken: " + valor);
            //}
            if (valor % 3 == 0) {
                ite.remove();
            }
        }
        if (!q.empty()) {
            System.out.println("gotten: " + q.get());
        }
        System.out.println("numElem: " + q.numElem);
        //System.out.println("present content of the queue: " + q);
    }
}
