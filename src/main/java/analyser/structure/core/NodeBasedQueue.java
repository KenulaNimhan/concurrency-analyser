package analyser.structure.core;

import analyser.structure.queue.Queue;

import java.util.NoSuchElementException;

public class NodeBasedQueue<S> implements Queue<S> {

    private static class Node<T> {
        private T val;
        private Node<T> next;

        public Node(T val) {this.val = val;}
    }

    private Node<S> frontNode;
    private Node<S> lastNode;
    private final int cap;

    public NodeBasedQueue(int cap) {
        this.cap = cap;
    }

    public int cap() {
        return cap;
    }

    public int size() {
        int size = 0;
        var curr = frontNode;
        while (curr != null) {
            size++;
            curr = curr.next;
        }
        return size;
    }

    public void enqueue(S element) {
        if (size() == cap) throw new IllegalStateException("cannot enqueue. queue is full");
        Node<S> newLastNode = new Node<>(element);
        if (isEmpty()) {
            frontNode = lastNode = newLastNode;
        } else {
            lastNode.next = newLastNode;
            lastNode = newLastNode;
        }

    }

    public S dequeue() {
        if (this.isEmpty()) throw new NoSuchElementException("cannot dequeue. queue is empty");
        var returnVal = frontNode.val;
        frontNode = frontNode.next;

        if (isEmpty()) lastNode = null;

        return returnVal;
    }

    public boolean isEmpty() {
        return frontNode == null;
    }

}
