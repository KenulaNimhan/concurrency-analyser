package analyser.structure.queue.impl;

import analyser.structure.queue.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class BasicQueue<T> implements Queue<T> {
    private final List<T> array;
    private final int cap;

    public BasicQueue(int capacity) {
        cap = capacity;
        array = new ArrayList<>(cap);
    }

    public void enqueue(T val) {
        if (size() == cap) throw new IllegalStateException("Cannot enqueue. queue is full");
        array.addFirst(val);
    }

    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Cannot dequeue. queue is empty");
        return array.removeLast();
    }

    public int size() {
        return array.size();
    }

    public int cap() {
        return cap;
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }
}
