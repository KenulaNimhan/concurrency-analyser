package analyser.structure.stack.impl;

import analyser.structure.stack.Stack;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedStack<T> implements Stack<T> {
    private int size; // actual size of the stack - number of elements
    private final T[] array;

    // defining locks and conditions
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition consumers = lock.newCondition();
    private final Condition producers = lock.newCondition();

    @SuppressWarnings("unchecked")
    public LockBasedStack(int capacity) {
        size = 0;
        array = (T[]) new Object[capacity];
    }

    public int cap() { return  array.length;}

    public int size() {
        return this.size;
    }

    public void push(T val) throws InterruptedException{
        lock.lock();
        try {
            while (size == array.length) {
                // tells producer threads to wait until size is reduced
                producers.await();
            }
            array[size] = val;
            size++;
            // signals consumer threads that items are added so they can resume consuming
            consumers.signal();
        } finally {
            lock.unlock();
        }
    }

    public T pop() throws InterruptedException {
        lock.lock();
        try {
            while(this.isEmpty()) {
                // tells consumer threads to wait until items are added
                consumers.await();
            }
            var valToReturn = array[size-1];
            array[size-1] = null;
            size--;
            // signals producer threads that they can resume adding items
            producers.signal();
            return valToReturn;

        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
