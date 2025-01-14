package practica1.LinkedQ;

//import java.util.Arrays;
import java.util.Random;

public class TestLQ {

  public static void main(String[] args) {
    LinkedQueue<Integer> q = new LinkedQueue<>();
    // throw new RuntimeException("//Completar...");
    System.out.println("contents of queue: " + q);
    for (int i = 0; i < 10; i++) {
      q.put(i);
    }
    System.out.println("contents of queue: " + q);
    System.out.print("removed: ");
    for (int i = 0; i < 10; i++) {
      System.out.print(q.get() + " ");
    }
    System.out.println();
    Random rand = new Random();
    System.out.print("added: ");
    for (int i = 0; i < 100; i++) {
      int ran = rand.nextInt(0, 100);
      q.put(ran);
      System.out.print(ran + " ");
    }
    System.out.println();
    System.out.println("contents of queue: " + q);
  }
}
