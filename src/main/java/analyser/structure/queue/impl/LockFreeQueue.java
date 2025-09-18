package analyser.structure.queue.impl;

import analyser.structure.queue.Queue;

import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> implements Queue<T> {

    private static class Node<S> {
        private S val;
        private AtomicReference<Node<S>> next;

        public Node(S val) {
            this.val = val;
            this.next = new AtomicReference<>(null);
        }
    }

    private final AtomicReference<Node<T>> frontNode = new AtomicReference<>();
    private final AtomicReference<Node<T>> lastNode = new AtomicReference<>();
    private final Semaphore space;
    private final int cap;

    public LockFreeQueue(int cap) {
        this.cap = cap;
        this.space = new Semaphore(cap);
        Node<T> dummy = new Node<>(null);
        frontNode.set(dummy);
        lastNode.set(dummy);
    }

    public int cap() {
        return cap;
    }

    public int size() {
        int size = 0;
        var curr = frontNode.get();
        while (curr != null) {
            size++;
            curr = curr.next.get();
        }
        return size-1;
    }

    public void enqueue(T element) throws InterruptedException{
        space.acquire();
        Node<T> newNode = new Node<>(element);
        while (true) {
            Node<T> last = lastNode.get();
            Node<T> next = last.next.get();
            if (last == lastNode.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(null, newNode)) {
                        lastNode.compareAndSet(last, newNode);
                        return;
                    }
                } else {
                    lastNode.compareAndSet(last, next);
                }
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> first = frontNode.get();
            Node<T> last = lastNode.get();
            Node<T> next = first.next.get();
            try {
                if (first == frontNode.get()) {
                    if (next == null) {
                        return null;
                    }
                    T val = next.val;
                    if (frontNode.compareAndSet(first, next)) {
                        if (first == last) {
                            lastNode.compareAndSet(last, next);
                        }
                        return val;
                    }
                }
            } finally {
                space.release();
            }

        }
    }

    public boolean isEmpty() {
        Node<T> frontDummy = frontNode.get();
        Node<T> firstElement = frontDummy.next.get();
        return firstElement == null;
    }
}
