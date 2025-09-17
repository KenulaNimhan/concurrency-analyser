package analyser.structure.queue.impl;

import analyser.structure.queue.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedQueue<T> implements Queue<T> {
    private final List<T> array;
    private final int cap;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition consumers = lock.newCondition();
    private final Condition producers = lock.newCondition();

    public LockBasedQueue(int capacity) {
        cap = capacity;
        array = new ArrayList<>(cap);
    }

    public void enqueue(T val) throws InterruptedException {
        lock.lock();
        try {
            while (size() == cap) producers.await();
            array.addFirst(val);
            consumers.signal();
        } finally {
            lock.unlock();
        }

    }

    public T dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (isEmpty()) consumers.await();
            var val = array.removeLast();
            producers.signal();
            return val;
        } finally {
            lock.unlock();
        }
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
