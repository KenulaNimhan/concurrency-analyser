package analyser.structure.stack.impl;

import analyser.structure.stack.Stack;

import java.util.Arrays;

public class SyncStack<T> implements Stack<T> {
    private int size; // actual size of the stack - number of elements
    private final T[] array;

    @SuppressWarnings("unchecked")
    public SyncStack(int capacity) {
        size = 0;
        array = (T[]) new Object[capacity];
    }

    public int cap() { return  array.length;}

    public int size() {
        return this.size;
    }

    public synchronized void push(T val) throws InterruptedException{
        while (size == array.length) {
            wait();
        }
        array[size] = val;
        size++;

        notifyAll();
    }

    public synchronized T pop() throws InterruptedException {
        while(this.isEmpty()) {
            wait();
        }
        var valToReturn = array[size-1];
        array[size-1] = null;
        size--;

        notifyAll();
        return valToReturn;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
