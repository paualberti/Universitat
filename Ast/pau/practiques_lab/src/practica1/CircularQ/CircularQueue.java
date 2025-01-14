package practica1.CircularQ;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.Queue;

public class CircularQueue<E> implements Queue<E> {

  private final E[] queue;
  private final int N;
  // Completar...
  private int head;
  private int tail;
  public int numElem;

  public CircularQueue(int N) {
    this.N = N;
    queue = (E[]) (new Object[N]);
    head = 0;
    tail = 0;
    numElem = 0;
  }

  @Override
  public int size() {
    // throw new RuntimeException("//Completar...");
    return numElem;
  }

  @Override
  public int free() {
    // throw new RuntimeException("//Completar...");
    return N - size();
  }

  @Override
  public boolean empty() {
    // throw new RuntimeException("//Completar...");
    return size() == 0;
  }

  @Override
  public boolean full() {
    // throw new RuntimeException("//Completar...");
    return size() == N;
  }

  @Override
  public E peekFirst() {
    // throw new RuntimeException("//Completar...");
    return queue[head];
  }

  @Override
  public E get() {
    // throw new RuntimeException("//Completar...");
    if (empty()) {
      throw new IllegalStateException();
    }
    E e = queue[head];
    queue[head] = null;
    head = (head + 1) % N;
    numElem--;
    return e;
  }

  @Override
  public void put(E e) {
    // throw new RuntimeException("//Completar...");
    if (full()) {
      throw new IllegalStateException();
    }
    queue[tail] = e;
    tail = (tail + 1) % N;
    numElem++;
  }

  @Override
  public String toString() {
    // throw new RuntimeException("//Completar...");
    String string = "head: " + head + " tail: " + tail + " numElem: " + numElem;
    for (int i = 0; i < N; i++) {
      if (queue[(head + i) % N] == null) {
        break;
      }
      string += "\n" + (head + i) % N + ": " + queue[(head + i) % N];
    }
    return string;
  }

  @Override
  public Iterator<E> iterator() {
    return new MyIterator();
  }

  class MyIterator implements Iterator {

    // Completar...
    private int current = head;
    private boolean nextCall = false;

    @Override
    public boolean hasNext() {
      // throw new RuntimeException("//Completar...");
      if (current == head && nextCall) {
        return false;
      }
      E e = queue[current];
      return e != null;
    }

    @Override
    public E next() {
      // throw new RuntimeException("//Completar...");
      E e = queue[current];
      if (e == null) {
        throw new NoSuchElementException();
      }
      current = (current + 1) % N;
      nextCall = true;
      return e;
    }

    @Override
    public void remove() {
      // throw new RuntimeException("//Completar...");
      if (!nextCall) {
        throw new IllegalStateException();
      }
      nextCall = false;
      current--;
      if (current == -1) {
        current = N - 1;
      }
      for (int i = 0; i < size(); i++) {
        if ((current + i + 1) % N == tail) {
          break;
        }
        queue[(current + i) % N] = queue[(current + i + 1) % N];
      }
      numElem--;
      tail = (head + numElem) % N;
      queue[tail] = null;
    }
  }
}
