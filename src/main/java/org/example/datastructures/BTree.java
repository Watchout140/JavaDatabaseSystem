package org.example.datastructures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BTree<V extends Comparable<V>, T>  {
    // max children per B-tree node = M-1
    // (must be even and greater than 2)
    private int M = 4;

    private Node<V, T> root;       // root of the B-tree
    private int height;      // height of the B-tree
    private int n;           // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private class Node<V extends Comparable<V>, T> {
        private int m;                             // number of children
        private ArrayList<Entry<V, T>> children;   // the array of children
        private List<Node<V, T>> childrenNodes;
        // create a node with k children
        private Node(int k) {
            m = k;
            this.children = new ArrayList<>(M - 1);
            this.childrenNodes = new ArrayList<>(M);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "m=" + m +
                    ", children=" + children +
                    ", childrenNodes=" + childrenNodes +
                    '}';
        }
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value
    private class Entry<V extends Comparable<V>, T> {
        private V key;
        private ArrayList<T> val;
        //private Node<V, T> next;     // helper field to iterate over array entries
        public Entry(V key, ArrayList<T> val) {
            this.key  = key;
            this.val  = val;
            //this.next = next;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", val=" + val +

                    '}';
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTree() {
        root = new Node<>(0);
        height = 0;
        n = 0;
    }

    /**
     * Returns true if this symbol table is empty.
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Returns the height of this B-tree (for debugging).
     *
     * @return the height of this B-tree
     */
    public int height() {
        return height;
    }


    /**
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is {@code null}, this effectively deletes the key from the symbol table.
     *
     //* @param key the key
     //* @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    private void find(Node<V, T> x, Predicate<V> predicate, List<T> results) {
        int i = 0;

        while (i < x.children.size()){
            if (x.children.isEmpty()) return;
            //System.out.println("TEST: " + x.children.get(i).key);
            if (predicate.test(x.children.get(i).key)) results.addAll(x.children.get(i).val);
            else if (!x.childrenNodes.isEmpty()){
                find(x.childrenNodes.getLast(), predicate, results);
                break;
            }
            i++;
        }
        if (!x.childrenNodes.isEmpty()) {
            for (int j = 0; j <= i; j++) {
                System.out.println("JJJ: " + j + " " + i + " " + x.children);
                if (predicate.test(x.children.get(0).key)) find(x.childrenNodes.get(j), predicate, results);
            }
        }
    }
    public List<T> find(Predicate<V> predicate) {
        List<T> results = new ArrayList<>();
        find(root, predicate, results);
        System.out.println("RESULTSSSSS: " + results);
        return results;
    }
    public void put(V key, T val) {
        if (key == null) throw new IllegalArgumentException("argument key to put() is null");
        System.out.println("FIRST: " + root.children);
        Node<V, T> u = null;

        if (!equals(root, key, val)) u = insert(root, key, val, true);
        n++;
        if (u == null) return;
        System.out.println("KEY: " + root.children +" WALLA: "+ root.childrenNodes);
        insert(u, key, val, false);

        System.out.println("ROOTCHILDREN: " + root.children);

    }
    boolean equals(Node<V, T> x, V k, T val) {
        int i = 0;
        while (i < x.m && k.compareTo(x.children.get(i).key) > 0){
            i++;
        }
        if (i < x.m && eq(k, x.children.get(i).key)) {
            x.children.get(i).val.add(val);
            return true;
        }
        if (x.childrenNodes.isEmpty()) {
            return false;
        }
        return equals(x.childrenNodes.get(i), k, val);
    }
    private Node<V, T> insert(Node<V, T> x, V k, T val, boolean split) {
        int i = 0;
        ArrayList<T> list = new ArrayList<>();
        list.add(val);
        Entry<V, T> t = new Entry<>(k, list);
        while (i < x.m && k.compareTo(x.children.get(i).key) > 0){
            i++;
        }
        if (x.m + 1 == M && split) {
            return split(x,k, val, i, false);
        } else if (!x.childrenNodes.isEmpty() && x.childrenNodes.get(i).m + 1 == M) {
            if (split)return split(x,k, val, i, false);
            return split(x,k, val, i, true);
        } else if (x.m < M && x.childrenNodes.isEmpty()){
            x.children.add(i, t);
            x.m++;
            return null;
        }
        System.out.println("x.children: " + x.childrenNodes +  " key: " + k);
        return insert(x.childrenNodes.get(i), k, val, split);
    }

    // split node in half
    private Node<V, T> split(Node<V, T> h, V key, T val, int index, boolean splitAgain) {
        Node<V, T> k = h;
        if (root.children.size() != 3) {
            k = h.childrenNodes.get(index);
        }
        Node<V, T> u = new Node<>(0);
        k.m = M/2;
        System.out.println("SPLIT: " + k.children);
        System.out.println("SPLITH: " + h.children);
        for (int j = 0; j < M/2 -1; j++) {
            System.out.println("J: " + (M / 2 + j));
            u.children.add(j, k.children.get(M / 2 + j));
            u.m++;
        }
        System.out.println("SPLIT2: " + u.children);
        System.out.println("ROOTINSPLIT: " + k.children);
        Node<V, T> t = new Node<>(0);
        Node<V, T> n = new Node<>(0);
        if (root.children.size() == 3) {
            //System.out.println("ROOTINSPLIT2: " + root.children + " " + root.childrenNodes.getFirst().childrenNodes);
            for (int i = 0; i < root.childrenNodes.size(); i++) {
                if (i < root.childrenNodes.size()/2) {
                    n.childrenNodes.add(root.childrenNodes.get(i));
                } else u.childrenNodes.add(root.childrenNodes.get(i));
            }
            t.children.add(0, new Entry<>(h.children.get(1).key, h.children.get(1).val));
            t.m++;
            n.children.add(root.children.getFirst());
            n.m++;
            t.childrenNodes.add(n);
            t.childrenNodes.add(u);
            root = t;
            height++;
        } else {
            int i;
            for (i = 0; i < h.children.size(); i++) {
                if (less(k.children.get(1).key,h.children.get(i).key)) break;
            }
            for (int j = 0; j < k.childrenNodes.size(); j++) {
                if (j < k.childrenNodes.size()/2) {
                    n.childrenNodes.add(k.childrenNodes.get(j));
                } else u.childrenNodes.add(k.childrenNodes.get(j));
            }
            h.children.add(i, new Entry<>(k.children.get(1).key, k.children.get(1).val));
            h.m++;
            n.children.add(k.children.getFirst());
            n.m++;
            h.childrenNodes.remove(index);
            for (int j = 0; j < h.childrenNodes.size(); j++) {
                if (less(u.children.getFirst().key, h.childrenNodes.get(j).children.getFirst().key)) {
                    h.childrenNodes.add(j, n);
                    h.childrenNodes.add(j+1, u);
                    return h;
                }
            }
            h.childrenNodes.add(n);
            h.childrenNodes.add(u);
            System.out.println("CHILDRENINSPLIT: " + h.children + " ChildrenNoes: " + h.childrenNodes);
            if (splitAgain) return insert(h, key, val, false);
            return h;
        }

        return t;
    }
    private boolean merge(Node<V, T> parent, int childIndex, boolean mergeWithLeft) {
        Node<V, T> targetNode;
        Node<V, T> siblingNode;
        int parentKeyIndex;

        if (mergeWithLeft) {
            targetNode = parent.childrenNodes.get(childIndex - 1);
            siblingNode = parent.childrenNodes.get(childIndex);
            parentKeyIndex = childIndex - 1;
        } else {
            targetNode = parent.childrenNodes.get(childIndex);
            siblingNode = parent.childrenNodes.get(childIndex + 1);
            parentKeyIndex = childIndex;
        }

        // Move the parent key down to the target node
        Entry<V, T> parentEntry = parent.children.get(parentKeyIndex);
        targetNode.children.add(parentEntry);
        targetNode.m++;

        // Move all keys and children from siblingNode to targetNode
        for (int i = 0; i < siblingNode.m; i++) {
            targetNode.children.add(siblingNode.children.get(i));
            if (!siblingNode.childrenNodes.isEmpty()) {
                targetNode.childrenNodes.add(siblingNode.childrenNodes.get(i));
            }
        }
        if (!siblingNode.childrenNodes.isEmpty()) {
            targetNode.childrenNodes.add(siblingNode.childrenNodes.get(siblingNode.m));
        }

        targetNode.m += siblingNode.m;

        // Remove the key and siblingNode from the parent
        parent.children.remove(parentKeyIndex);
        parent.childrenNodes.remove(mergeWithLeft ? childIndex : childIndex + 1);
        parent.m--;

        // Adjust root if necessary
        if (parent == root && parent.m == 0) {
            root = targetNode;
            height--;
        }

        return true;
    }
    public boolean remove(V key) {
        if (key == null) throw new IllegalArgumentException("argument to remove() is null");
        boolean result = remove(null, root, key, 0);
        System.out.println("RÖÖT: \n" + this);
        return result;
    }

    private void merge(Node<V, T> parent, Node<V, T> x, int deleteIndex, int parentChildNodeIndex, V key) {
        //Entry<V, T> deletedEntry = x.children.remove(deleteIndex);
        if (parent == null) {
            for (int i = 0; i < x.childrenNodes.size(); i++) {
                ArrayList<Entry<V,T>> children = x.childrenNodes.get(i).children;
                if (children.size() > 1) {
                    if (less(children.getFirst().key, key)) {
                        x.children.remove(deleteIndex);
                        x.children.add(deleteIndex, children.getLast());
                        children.removeLast();
                    } else {
                        x.children.remove(deleteIndex);
                        x.children.add(deleteIndex, children.getFirst());
                        children.removeFirst();
                    }
                    break;
                }
            }
            return;
        }
        if (parent.children.size() > 1) {
            if (parentChildNodeIndex == parent.childrenNodes.size() - 1 && parentChildNodeIndex - 1 >= 0 &&  parent.childrenNodes.get(parentChildNodeIndex - 1).children.size() > 1) {
                ArrayList<Entry<V, T>> children = parent.childrenNodes.get(parentChildNodeIndex - 1).children;
                Entry<V, T> childEntry = children.getLast();
                Entry<V, T> parentSwapEntry = parent.children.removeLast();
                x.children.add(parentSwapEntry);
                x.children.removeFirst();
                parent.children.add(childEntry);
            } else if (parentChildNodeIndex == 0 && parentChildNodeIndex + 1 < parent.childrenNodes.size() && parent.childrenNodes.get(parentChildNodeIndex + 1).children.size() > 1) {
                ArrayList<Entry<V, T>> children = parent.childrenNodes.get(parentChildNodeIndex + 1).children;
                Entry<V, T> childEntry = children.getFirst();
                Entry<V, T> parentSwapEntry = parent.children.removeFirst();
                x.children.add(parentSwapEntry);
                x.children.removeFirst();
                parent.children.addFirst(childEntry);
            } else {
                if (parentChildNodeIndex + 1 != parent.childrenNodes.size()) {
                    Node<V, T> combineNode = parent.childrenNodes.get(parentChildNodeIndex + 1);
                    x.childrenNodes.addAll(combineNode.childrenNodes);
                    x.children.addAll(combineNode.children);
                    Entry<V, T> removeFromParent = parent.children.remove(deleteIndex);
                    x.children.removeFirst();
                    x.children.addFirst(removeFromParent);

                } else if (parentChildNodeIndex == parent.childrenNodes.size() - 1 && parentChildNodeIndex - 1 >= 0) {
                    Node<V, T> combineNode = parent.childrenNodes.get(parentChildNodeIndex - 1);
                    x.childrenNodes.addAll(combineNode.childrenNodes);
                    x.children.addAll(combineNode.children);
                    Entry<V, T> removeFromParent = parent.children.removeLast();
                    x.children.removeFirst();
                    x.children.addLast(removeFromParent);
                } else if(parentChildNodeIndex == 0 && parentChildNodeIndex + 1 < parent.childrenNodes.size()) {
                    Node<V, T> combineNode = parent.childrenNodes.get(parentChildNodeIndex + 1);
                    x.childrenNodes.addAll(combineNode.childrenNodes);
                    x.children.addAll(combineNode.children);
                    Entry<V, T> removeFromParent = parent.children.removeFirst();
                    x.children.removeFirst();
                    x.children.addFirst(removeFromParent);
                }
            }
        } else {
            System.out.println("JAG KOMMER JU GHIT: " + parentChildNodeIndex);
            if (parentChildNodeIndex == 0 && parent.childrenNodes.get(1).children.size() > 1) {
                System.out.println("SEDANHÄr");
                x.children.removeFirst();
                x.children.add(parent.children.getFirst());
                parent.children.removeFirst();
                parent.children.add(parent.childrenNodes.get(1).children.removeFirst());
            } else if (parentChildNodeIndex == 1 && parent.childrenNodes.get(0).children.size() > 1) {
                x.children.removeFirst();
                x.children.add(parent.children.getFirst());
                parent.children.removeFirst();
                parent.children.add(parent.childrenNodes.getFirst().children.removeLast());
            } else {
                if (parentChildNodeIndex == 0) {
                    System.out.println("HEJSAN");
                    Node<V, T> addToParentNode =  parent.childrenNodes.get(1);
                    parent.children.add(addToParentNode.children.getFirst());
                    System.out.println("PARWENRT: " + parent.children);
                    parent.childrenNodes.addAll(addToParentNode.childrenNodes);
                    parent.childrenNodes.remove(1);
                    parent.childrenNodes.removeFirst();
                    System.out.println("CHIDNODES: " + this);

                } else {
                    System.out.println("HEJSA2");
                    Node<V, T> addToParentNode =  parent.childrenNodes.get(0);
                    parent.children.add(addToParentNode.children.getFirst());
                    parent.childrenNodes.addAll(addToParentNode.childrenNodes);
                    parent.childrenNodes.clear();
                }
                height--;
            }
        }

    }
    private boolean remove(Node<V, T> parent, Node<V, T> x, V k, int parentChildNodeIndex) {
        int i = 0;
        while (i < x.m && k.compareTo(x.children.get(i).key) > 0) {
            i++;
        }
        if (i < x.m && eq(k, x.children.get(i).key)) {
            if (x.children.size() > 1 && x.childrenNodes.isEmpty()) {
                if (x.children.get(i).val.size() > 1) x.children.get(i).val.removeFirst();
                else x.children.remove(i);
            } else {
                merge(parent, x, i, parentChildNodeIndex, k);
            }

            return true;
        }
        if (x.childrenNodes.isEmpty()) {
            return false;
        }

        return remove(x, x.childrenNodes.get(i), k, i);
    }

    /**
     * Returns a string representation of this B-tree (for debugging).
     *
     * @return a string representation of this B-tree.
     */
    public String toString() {
        return toString(root, height, "") + "\n";
    }
    private String toString(Node<V, T> h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        ArrayList<Entry<V, T>> children = h.children;
        System.out.println("CHILDREN: " + children);
        if (ht == 0) {
            for (int j = 0; j < children.size(); j++) {
                s.append(indent).append(children.get(j).key).append(" ").append(children.get(j).val).append("\n");
            }
        }
        else {
            for (int j = 0; j < h.childrenNodes.size(); j++) {
                s.append(toString(h.childrenNodes.get(j), ht-1, indent + "     "));
                if (j < children.size())s.append(indent).append("(").append(children.get(j).key).append(" ").append(children.get(j).val).append(")\n");
            }
        }
        return s.toString();
    }


    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(V k1, V k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(V k1, V k2) {
        return k1.compareTo(k2) == 0;
    }

    public List<T> getSortedComp(Comparator<V> comparator) {
        List<Pair<V, T>> keyValuePairs = new ArrayList<>();
        collectKeyValuePairs(root, keyValuePairs);
        keyValuePairs.sort(Comparator.comparing(Pair::getKey, comparator));
        return keyValuePairs.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    private void collectKeyValuePairs(Node<V, T> node, List<Pair<V, T>> keyValuePairs) {
        if (node == null) return;

        for (int i = 0; i < node.children.size(); i++) {
            Entry<V, T> entry = node.children.get(i);
            for (T value : entry.val) {
                keyValuePairs.add(new Pair<>(entry.key, value));
            }
            if (i < node.childrenNodes.size()) {
                collectKeyValuePairs(node.childrenNodes.get(i), keyValuePairs);
            }
        }
        if (node.children.size() < node.childrenNodes.size()) {
            collectKeyValuePairs(node.childrenNodes.get(node.childrenNodes.size() - 1), keyValuePairs);
        }
    }

    // Utility class to hold key-value pairs
    private static class Pair<V, T> {
        private final V key;
        private final T value;

        public Pair(V key, T value) {
            this.key = key;
            this.value = value;
        }

        public V getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }
    }

    public List<T> getSorted() {
        List<T> sortedValues = new ArrayList<>();
        traverseAndCollect(root, sortedValues);
        return sortedValues;
    }

    private void traverseAndCollect(Node<V, T> node, List<T> sortedValues) {
        if (node == null) return;

        for (int i = 0; i < node.children.size(); i++) {
            if (i < node.childrenNodes.size()) {
                traverseAndCollect(node.childrenNodes.get(i), sortedValues);
            }
            sortedValues.addAll(node.children.get(i).val);
        }
        if (node.children.size() < node.childrenNodes.size()) {
            traverseAndCollect(node.childrenNodes.get(node.childrenNodes.size() - 1), sortedValues);
        }
    }
}