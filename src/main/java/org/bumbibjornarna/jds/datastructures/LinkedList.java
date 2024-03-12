package org.bumbibjornarna.jds.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class LinkedList<V, T> {
    public static class Node<V, T> {
        private final V key;
        private final T data;
        private Node<V, T> next;

        public Node(V key, T data, Node<V, T> next) {
            this.data = data;
            this.next = next;
            this.key = key;
        }

        public Node<V, T> getNext() {
            return next;
        }

        public T getData() {
            return data;
        }

        public V getKey() {
            return key;
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
    public int size() {
        return size;
    }
    public Node<V, T> getHead() {
        return head;
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

    public List<T> toCollectionData() {
        List<T> list = new ArrayList<>();
        Node<V, T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    public List<V> toCollectionKey() {
        List<V> list = new ArrayList<>();
        Node<V, T> current = head;
        while (current != null) {
            list.add(current.key);
            current = current.next;
        }
        return list;
    }

    public List<T> mergeSort(Comparator<V> comparator) {
        if (size > 1) {
            head = mergeSortRec(head, comparator);
            tail = head;
            while (tail.next != null) {
                tail = tail.next;
            }
        }
        return toCollectionData();
    }

    private Node<V, T> mergeSortRec(Node<V, T> head, Comparator<V> comparator) {
        if (head == null || head.next == null) {
            return head;
        }

        // Split the list into two halves
        Node<V, T> middle = getMiddle(head);
        Node<V, T> nextOfMiddle = middle.next;
        middle.next = null;

        // Apply mergeSort on both halves
        Node<V, T> left = mergeSortRec(head, comparator);
        Node<V, T> right = mergeSortRec(nextOfMiddle, comparator);

        // Merge the sorted halves
        return sortedMerge(left, right, comparator);
    }

    private Node<V, T> sortedMerge(Node<V, T> a, Node<V, T> b, Comparator<V> comparator) {
        Node<V, T> result;
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }

        if (comparator.compare(a.key, b.key) <= 0) {
            result = a;
            result.next = sortedMerge(a.next, b, comparator);
        } else {
            result = b;
            result.next = sortedMerge(a, b.next, comparator);
        }
        return result;
    }

    private Node<V, T> getMiddle(Node<V, T> head) {
        if (head == null) {
            return head;
        }
        Node<V, T> slow = head, fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
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
