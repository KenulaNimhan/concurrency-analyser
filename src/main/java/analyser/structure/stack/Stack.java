package analyser.structure.stack;

public interface Stack<T> {
    public T pop() throws InterruptedException;
    public void push(T val) throws InterruptedException;
    public int size();
    public boolean isEmpty();
}
