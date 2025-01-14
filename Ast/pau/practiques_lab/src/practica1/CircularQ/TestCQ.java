package practica1.CircularQ;

//import java.util.Arrays;

public class TestCQ {

  public static void main(String[] args) {
    // throw new RuntimeException("//Completar...");
    CircularQueue<Integer> q = new CircularQueue<>(20);
    for (int i = 0; i < 10; i++) {
      q.put(i);
    }
    System.out.println("Queue content: \n" + q);
  }
}
