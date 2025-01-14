package practica1.CircularQ;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int head, tail, numElem;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
    }

    @Override
    public int size() {
        return numElem;
    }

    @Override
    public int free() {
        return N - numElem;
    }

    @Override
    public boolean empty() {
        return numElem == 0;
    }

    @Override
    public boolean full() {
        return numElem == N;
    }

    @Override
    public E peekFirst() {
        return queue[head];
    }

    @Override
    public E get() {
        if (empty()) {
            throw new IllegalStateException();
        }
        E first = peekFirst();
        queue[head] = null;
        head = (head + 1) % N;
        numElem--;
        return first;
    }

    @Override
    public void put(E e) {
        if (full()) {
            throw new IllegalStateException();
        }
        queue[tail] = e;
        tail = (tail + 1) % N;
        numElem++;
    }

    @Override
    public String toString() {
        String str;
        str = "N=" + N + "\nhead=" + head + "\ntail=" + tail + "\nnumElem=" + numElem + "\nqueue=\n";
        for (int i = head; i < head + numElem; i++) {
            str += queue[i % N] + "\n";
        }
        return str;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        private int cursor, count;
        private boolean removable;

        public MyIterator(){
            this.cursor = head;
            this.count = 0;
            this.removable = false;
        }

        @Override
        public boolean hasNext() {
            return count < numElem;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            E e = queue[cursor];
            cursor = (cursor + 1) % N;
            count++;
            removable = true;
            return e;
        }

        @Override
        public void remove() {
            if (!removable) {
                throw new IllegalStateException();
            }
            for (int i = head + count; i < head + numElem; i++) {
                queue[(i - 1) % N] = queue[i % N];
            }
            tail = (tail - 1 + N) % N;
            queue[tail] = null;
            numElem--;
            cursor = (cursor - 1 + N) % N;
            count--;
            removable = false;
        }

    }
}
