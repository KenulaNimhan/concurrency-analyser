package analyser.structure.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class BasicStack<T> implements Stack<T>{
    private int size; // actual size of the stack - number of elements
    private final T[] array;

    @SuppressWarnings("unchecked") // to support generic array creation
    public BasicStack(int capacity) {
        size = 0;
        array = (T[]) new Object[capacity];
    }

    public int cap() {
        return this.array.length;
    }

    public int size() {
        return this.size;
    }

    public void push(T val){
        if (size == array.length) throw new IllegalStateException("Cannot push. stack is full.");
        array[size] = val;
        size++;
    }

    public T pop() {
        if (this.isEmpty()) throw new NoSuchElementException("Cannot pop. stack is empty.");
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
