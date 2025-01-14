package practica1.LinkedQ;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.Queue;

public class LinkedQueue<E> implements Queue<E> {

  // Completar
  public int numElem;
  private Node first = new Node<>();
  private Node last = new Node();

  @Override
  public int size() {
    // throw new RuntimeException("//Completar...");
    return numElem;
  }

  @Override
  public int free() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean empty() {
    // throw new RuntimeException("//Completar...");
    return size() == 0;
  }

  @Override
  public boolean full() {
    return false;
  }

  @Override
  public E peekFirst() {
    // throw new RuntimeException("//Completar...");
    if (empty()) {
      return null;
    }
    return (E) first.getNext().getValue();
  }

  @Override
  public E get() {
    // throw new RuntimeException("//Completar...");
    if (size() == 0) {
      throw new IllegalStateException();
    }
    E e = (E) first.getNext().getValue();
    first.setNext(first.getNext().getNext());
    numElem--;
    return e;
  }

  @Override
  public void put(E e) {
    // throw new RuntimeException("//Completar...");
    Node node = new Node<>();
    node.setValue(e);
    if (size() == 0) {
      first.setNext(node);
    } else {
      last.getNext().setNext(node);
    }
    last.setNext(node);
    numElem++;
  }

  @Override
  public String toString() {
    // throw new RuntimeException("//Completar...");
    Node node = first;
    String string = "numElem: " + size();
    while (node.getNext() != null) {
      node = node.getNext();
      string += "\n" + node.getValue();
    }
    return string;
  }

  @Override
  public Iterator<E> iterator() {
    return new MyIterator();
  }

  class MyIterator implements Iterator {

    // Completar...
    private Node current = first;
    private Node previous = first;
    private boolean nextCall = false;

    @Override
    public boolean hasNext() {
      // throw new RuntimeException("//Completar...");
      return (E) current.getNext() != null;
    }

    @Override
    public E next() {
      // throw new RuntimeException("//Completar...");
      if (current.getNext() == null) {
        throw new NoSuchElementException();
      }
      E e = (E) current.getNext().getValue();
      if (nextCall) {
        previous = current;
      }
      current = current.getNext();
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

      previous.setNext(current.getNext());

      if (current.getNext() == last.getNext()) {
        last.setNext(previous);
      }
      numElem--;
    }
  }
}
