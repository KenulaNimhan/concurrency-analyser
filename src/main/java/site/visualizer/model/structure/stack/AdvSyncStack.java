package site.visualizer.model.structure.stack;


import java.util.Arrays;
import java.util.NoSuchElementException;

public class AdvSyncStack<T> implements Stack<T>{
    // actual size of the stack - number of elements
    private int size;
    private final T[] array;

    @SuppressWarnings("unchecked")
    public AdvSyncStack(int capacity) {
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
