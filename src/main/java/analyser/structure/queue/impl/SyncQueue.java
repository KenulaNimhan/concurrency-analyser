package analyser.structure.queue.impl;

import analyser.structure.queue.Queue;

import java.util.ArrayList;
import java.util.List;

public class SyncQueue<T> implements Queue<T> {
    private final List<T> array;
    private final int cap;

    public SyncQueue(int capacity) {
        cap = capacity;
        array = new ArrayList<>(cap);
    }

    public synchronized void enqueue(T val) throws InterruptedException {
        while (size() == cap) wait();
        array.addFirst(val);
        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (isEmpty()) wait();
        var val = array.removeLast();
        notifyAll();
        return val;
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
