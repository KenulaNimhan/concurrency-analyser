package analyser.structure.core;

import analyser.structure.stack.Stack;

import java.util.NoSuchElementException;

public class NodeBasedStack<S> implements Stack<S> {

    private static class Node<T> {
        private T val;
        private Node<T> next;

        public Node(T val) {
            this.val = val;
        }
    }

    private Node<S> topNode;
    private final int cap;

    public NodeBasedStack(int cap) {
        this.cap = cap;
    }

    public int size() {
        int size = 0;
        var curr = topNode;
        while (curr != null) {
            size++;
            curr = curr.next;
        }
        return size;
    }

    public void push(S element) {
        if (this.size() == cap) throw new IllegalStateException("cannot push. stack is full");
        var newNode = new Node<S>(element);
        newNode.next = topNode;
        topNode = newNode;

    }


    public S pop() {
        if (this.isEmpty()) throw new NoSuchElementException("cannot pop. stack is empty");
        var returnVal = topNode.val;
        topNode = topNode.next;

        return returnVal;
    }

    public boolean isEmpty() {
        return topNode == null;
    }
}
