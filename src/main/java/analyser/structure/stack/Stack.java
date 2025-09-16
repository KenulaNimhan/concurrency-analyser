package analyser.structure.stack;

public interface Stack<T> {
    public void push(T val) throws InterruptedException;
    public T pop() throws InterruptedException;
    public int size();
    public boolean isEmpty();
}
