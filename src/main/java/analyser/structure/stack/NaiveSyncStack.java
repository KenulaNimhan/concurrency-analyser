package analyser.structure.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class NaiveSyncStack<T> implements Stack<T>{
    // actual size of the stack - number of elements
    private int size;
    private final T[] array;

    @SuppressWarnings("unchecked") // to support generic array creation
    public NaiveSyncStack(int capacity) {
        size = 0;
        array = (T[]) new Object[capacity];
    }

    public int cap() {
        return this.array.length;
    }

    public int size() {
        return this.size;
    }

    public synchronized void push(T val){
        if (size == array.length) throw new IllegalStateException("Cannot push. stack is full.");
        array[size] = val;
        size++;
    }

    public synchronized T pop() {
        if (this.isEmpty()) throw new NoSuchElementException("Cannot pop. stack is empty.");
        var valToReturn = array[size-1];
        array[size-1] = null;
        size--;
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
