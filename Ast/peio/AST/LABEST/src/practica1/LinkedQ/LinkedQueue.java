package practica1.LinkedQ;

import java.util.Iterator;
import util.Queue;

public class LinkedQueue<E> implements Queue<E> {

    private Node first, last;
    private int numElem;

    @Override
    public int size() {
        return numElem;
    }

    @Override
    public int free() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean empty() {
        return numElem == 0;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public E peekFirst() {
        if (empty()) {
            return null;
        }
        return (E) first.getValue();
    }

    @Override
    public E get() {
        if (empty()) {
            throw new IllegalStateException();
        }
        E e = peekFirst();
        first = first.getNext();
        numElem--;
        if (empty()) {
            last = null;
        }
        return e;
    }

    @Override
    public void put(E e) {
        Node node = new Node();
        if (empty()) {
            first = node;
        } else {
            last.setNext(node);
        }
        last = node;
        node.setValue(e);
        numElem++;
    }

    @Override
    public String toString() {
        String str = "numElem=" + numElem + "\nqueue=\n";
        for (Node node = first; node != null; node = node.getNext()) {
            str += node.getValue() + "\n";
        }
        return str;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        private Node current, prev, prev2;
        private boolean removable;

        public MyIterator() {
            this.current = first;
            this.removable = false;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            if (removable) {
                prev2 = prev;
            }
            prev = current;
            current = current.getNext();
            removable = true;
            return (E) prev.getValue();
        }

        @Override
        public void remove() {
            if (!removable) {
                throw new IllegalStateException();
            }
            if (prev2 == null) {
                first = current;
            } else {
                prev2.setNext(current);
            }
            if (current == null) {
                last = prev2;
            }
            prev = null;
            numElem--;
            removable = false;
        }

    }
}
