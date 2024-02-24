package org.example.datastructures;

import java.util.ArrayList;
import java.util.List;

public class LinkedList<V, T> {
    private static class Node<V, T> {
        private final V key;
        private final T data;
        private Node<V, T> next;

        public Node(V key, T data, Node<V, T> next) {
            this.data = data;
            this.next = next;
            this.key = key;
        }
    }
    private Node<V, T> head;
    private Node<V, T> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    public void add(int index, V key, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        if (index == 0) {
            addFirst(key, item);
        } else if (index == size) {
            addLast(key, item);
        }else {
            Node<V, T> node = getNode(index - 1);
            addAfter(node, key, item);
        }
    }
    public T getByKey(V key) {
        Node<V, T> node = head;
        while (node != null) {
            if (node.key.equals(key)) return node.data;
            node = node.next;
        }
        return null;
    }
    private Node<V, T> getNode(int index) {
        Node<V, T> node = head;
        if (index == size-1) {
            node = tail;
        }else {
            for (int i = 0; i < index && node != null; i++) {
                node = node.next;
            }
        }
        return node;
    }
    public boolean add(V key, T item) {
        add(size, key, item);
        return true;
    }
    private void addAfter(Node<V, T> node, V key, T item) {
        node.next = new Node<>(key, item, node.next);
        size++;
    }
    public void addFirst(V key, T item) {
        Node<V, T> node = new Node<>(key, item, head);
        if (size == 0) head = tail = node;
        else {
            head = node;
        }
        size++;
    }
    private void addLast(V key, T item) {
        Node<V, T> node = new Node<>(key, item, null);
        if (size == 0) head = tail = node;
        else {
            tail.next = node;
            tail = node;
        }
        size++;
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        Node<V, T> node;
        if (index == size-1) {
            node = tail;
        }else {
            node = getNode(index);
        }

        return node.data;
    }
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        Node<V, T> deletedNode = head;
        if(size == 1) {
            head = tail = null;
        } else if (index == 0) {
            head = head.next;
        } else {
            Node<V, T> node = getNode(index - 1);
            deletedNode = node.next;
            node.next = node.next.next;
            if (index == size - 1) {
                tail = node;
            }
        }
        size--;
        return deletedNode.data;
    }

    public List<T> toCollection() {
        List<T> list = new ArrayList<>();
        Node<V, T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        Node<V, T> p = head;
        if (p != null) {
            while (p.next != null) {
                str.append(p.data.toString());
                str.append(" ==> ");
                p = p.next;
            }
            str.append(p.data.toString());
        }
        str.append("]");
        return str.toString() + size + "\nHead: " + head.data + "\nTail: " + tail.data;
    }
}
