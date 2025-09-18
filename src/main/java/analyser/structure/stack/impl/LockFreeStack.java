package analyser.structure.stack.impl;

import analyser.structure.stack.Stack;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

// uses atomic references and compare and swap technique (CAS)
// bounded nature is achieved by using Semaphore
public class LockFreeStack<S> implements Stack<S> {

    private static class Node<T> {
        private T val;
        private Node<T> next;

        public Node(T val) {
            this.val = val;
        }
    }

    private final AtomicReference<Node<S>> topNode = new AtomicReference<>();
    private final Semaphore space;

    public LockFreeStack(int capacity) {
        this.space = new Semaphore(capacity);
    }

    public int size() {
        int size = 0;
        var curr = topNode.get();
        while (curr != null) {
            size++;
            curr = curr.next;
        }
        return size;
    }

    public void push(S element) throws InterruptedException {
        space.acquire();
        Node<S> newNode = new Node<>(element);
        Node<S> current;
        do {
            current = topNode.get();
            newNode.next = current;
        } while (!topNode.compareAndSet(current, newNode));
    }


    public S pop() {
        Node<S> currentTop;
        Node<S> newTop;
        do {
            currentTop = topNode.get();
            if (currentTop == null) return null;
            newTop = currentTop.next;
        } while (!topNode.compareAndSet(currentTop, newTop));
        space.release();
        return currentTop.val;
    }

    public boolean isEmpty() {
        return topNode.get() == null;
    }
}
