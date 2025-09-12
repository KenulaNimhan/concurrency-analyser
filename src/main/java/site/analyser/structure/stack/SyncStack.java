package site.analyser.structure.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class SyncStack {
    // actual size of the stack - number of elements
    private int size;
    private final int[] array;

    public SyncStack(int capacity) {
        size = 0;
        array = new int[capacity];
    }

    public int cap() {
        return this.array.length;
    }

    public int size() {
        return this.size;
    }

    public synchronized void push(int val) throws InterruptedException{
        if (size == array.length) throw new IllegalStateException("Cannot push. stack is full.");
        array[size] = val;
        size++;
    }

    public synchronized int pop() throws InterruptedException {
        if (this.isEmpty()) throw new NoSuchElementException("Cannot pop. stack empty.");
        var valToReturn = array[size-1];
        array[size-1] = 0;
        size--;
        return valToReturn;
    }

    public int peek() {
        if (this.isEmpty()) throw new NoSuchElementException("stack is empty. no element to peek.");
        return array[size-1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
