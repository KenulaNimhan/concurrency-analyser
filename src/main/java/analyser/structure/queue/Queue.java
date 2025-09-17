package analyser.structure.queue;

public interface Queue<T> {
    public void enqueue(T val) throws InterruptedException;
    public T dequeue() throws InterruptedException;
    public int size();
    public boolean isEmpty();
}
