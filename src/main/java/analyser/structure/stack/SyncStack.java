package analyser.structure.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class SyncStack<T> implements Stack<T>{
    // actual size of the stack - number of elements
    private int size;
    private final T[] array;

    @SuppressWarnings("unchecked") // to support generic array creation
    public SyncStack(int capacity) {
        size = 0;
        array = (T[]) new Object[capacity];
    }

    public int cap() {
        return this.array.length;
    }

    public int size() {
        return this.size;
    }

    public synchronized void push(T val) throws InterruptedException{
        if (size == array.length) throw new IllegalStateException("Cannot push. stack is full.");
        array[size] = val;
        size++;
    }

    public synchronized T pop() throws InterruptedException {
        if (this.isEmpty()) throw new NoSuchElementException("Cannot pop. stack empty.");
        var valToReturn = array[size-1];
        array[size-1] = null;
        size--;
        return valToReturn;
    }

    public T peek() {
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
