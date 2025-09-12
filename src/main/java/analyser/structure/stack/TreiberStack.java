package analyser.structure.stack;

import java.util.NoSuchElementException;

// atomic operations
public class TreiberStack<S> {
    private static class Node<T> {
        private T val;
        private Node<T> next;

        public Node(T val) {
            this.val = val;
        }
    }

    private Node<S> topNode;
    private int size;
    private final int cap;

    public TreiberStack(int cap) {
        this.cap = cap;
        this.size = 0;
    }

    public void push(S element) {
        if (size == cap) throw new NoSuchElementException("cannot push. stack full");
        var newNode = new Node<S>(element);
        newNode.next = topNode;
        topNode = newNode;

        size++;
    }

    public S pop() {
        if (size == 0) throw new NoSuchElementException("cannot pop. stack empty");
        var returnVal = topNode.val;
        topNode = topNode.next;
        size--;

        return returnVal;
    }
}
