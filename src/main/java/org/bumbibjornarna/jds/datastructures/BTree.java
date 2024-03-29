package org.bumbibjornarna.jds.datastructures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BTree<V extends Comparable<V>, T>  {

    private int M = 4;

    private Node<V, T> root;
    private int height;
    private int n;

    private class Node<V extends Comparable<V>, T> {
        private int m;
        private ArrayList<Entry<V, T>> children;
        private List<Node<V, T>> childrenNodes;

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

    private class Entry<V extends Comparable<V>, T> {
        private V key;
        private ArrayList<T> val;
        public Entry(V key, ArrayList<T> val) {
            this.key  = key;
            this.val  = val;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", val=" + val +

                    '}';
        }
    }

    public BTree() {
        root = new Node<>(0);
        height = 0;
        n = 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return n;
    }

    public int height() {
        return height;
    }

    private void find(Node<V, T> x, Predicate<V> predicate, List<T> results) {
        int i = 0;

        while (i < x.children.size()){
            if (x.children.isEmpty()) return;
            if (predicate.test(x.children.get(i).key)) results.addAll(x.children.get(i).val);
            else if (!x.childrenNodes.isEmpty()){
                find(x.childrenNodes.getLast(), predicate, results);
                break;
            }
            i++;
        }
        if (!x.childrenNodes.isEmpty()) {
            for (int j = 0; j <= i; j++) {
                if (predicate.test(x.children.get(0).key)) find(x.childrenNodes.get(j), predicate, results);
            }
        }
    }
    public T find(V key) {
        List<T> results = new ArrayList<>();
        return find(root, key);
    }
    private T find(Node<V, T> x, V k) {
        int i = 0;
        while (i < x.m && k.compareTo(x.children.get(i).key) > 0){
            i++;
        }
        if (i < x.m && eq(k, x.children.get(i).key)) {
            return x.children.get(i).val.getFirst();
        }
        if (x.childrenNodes.isEmpty()) {
            return null;
        }
        return find(x.childrenNodes.get(i), k);
    }
    public List<T> find(Predicate<V> predicate) {
        List<T> results = new ArrayList<>();
        find(root, predicate, results);
        return results;
    }
    public void put(V key, T val) {
        if (key == null) throw new IllegalArgumentException("argument key to put() is null");
        Node<V, T> u = null;

        if (!equals(root, key, val)) u = insert(root, key, val, true);
        n++;
        if (u == null) return;
        insert(u, key, val, false);
    }
    private boolean equals(Node<V, T> x, V k, T val) {
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
        return insert(x.childrenNodes.get(i), k, val, split);
    }

    private Node<V, T> split(Node<V, T> h, V key, T val, int index, boolean splitAgain) {
        Node<V, T> k = h;
        if (root.children.size() != 3) {
            k = h.childrenNodes.get(index);
        }
        Node<V, T> u = new Node<>(0);
        k.m = M/2;
        for (int j = 0; j < M/2 -1; j++) {
            u.children.add(j, k.children.get(M / 2 + j));
            u.m++;
        }
        Node<V, T> t = new Node<>(0);
        Node<V, T> n = new Node<>(0);
        if (root.children.size() == 3) {
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
            if (splitAgain) return insert(h, key, val, false);
            return h;
        }

        return t;
    }

    public boolean remove(V key) {
        if (key == null) throw new IllegalArgumentException("argument to remove() is null");
        boolean result = remove(null, null, root, key, 0, 0);
        return result;
    }

    private boolean remove(Node<V, T> parentsParent, Node<V, T> parent, Node<V, T> x, V k, int parentChildNodeIndex, int grandParentsChildNodeIndex) {
        int i = 0;
        while (i < x.m && k.compareTo(x.children.get(i).key) > 0) {
            i++;
        }
        if (i < x.m && eq(k, x.children.get(i).key)) {
            if (x.children.size() > 1 && x.childrenNodes.isEmpty()) {
                if (x.children.get(i).val.size() > 1) x.children.get(i).val.removeFirst();
                else x.children.remove(i);
            } else if (parent.children.size() == 1 && parent.childrenNodes.get(0).children.size() == 1 && parent.childrenNodes.get(1).children.size() == 1) {
                if (grandParentsChildNodeIndex == 0 && parentsParent.children.size() == 1 && parentsParent.childrenNodes.get(grandParentsChildNodeIndex + 1).children.size() > 1) {
                    List<Node<V, T>> otherSideChildNodes = parentsParent.childrenNodes.get(grandParentsChildNodeIndex + 1).childrenNodes;
                    otherSideChildNodes.getFirst().children.addAll(parentsParent.childrenNodes.get(grandParentsChildNodeIndex + 1).childrenNodes.get(1).children);
                    otherSideChildNodes.get(1).children.clear();
                    otherSideChildNodes.getFirst().childrenNodes.addAll(parentsParent.childrenNodes.get(grandParentsChildNodeIndex + 1).childrenNodes.get(1).childrenNodes);
                    parentsParent.children.add(parentsParent.childrenNodes.get(grandParentsChildNodeIndex + 1).children.removeFirst());
                    parent.children.add(parentsParent.children.removeFirst());
                    x.children.add(parent.children.remove(parentChildNodeIndex));
                    x.children.removeFirst();
                } else if (grandParentsChildNodeIndex == parentsParent.childrenNodes.size() - 1 && parentsParent.children.size() == 1 && parentsParent.childrenNodes.get(grandParentsChildNodeIndex - 1).children.size() > 1) {
                    parentsParent.children.addFirst(parentsParent.childrenNodes.get(grandParentsChildNodeIndex - 1).children.removeFirst());
                    parent.children.addFirst(parentsParent.children.removeLast());
                    x.children.add(parent.children.remove(parentChildNodeIndex));
                    x.children.removeFirst();
                }
            } else {
                merge(parent, x, i, parentChildNodeIndex, k);
            }

            return true;
        }
        if (x.childrenNodes.isEmpty()) {
            return false;
        }

        return remove(parent, x, x.childrenNodes.get(i), k, i, parentChildNodeIndex);
    }
    private void merge(Node<V, T> parent, Node<V, T> x, int deleteIndex, int parentChildNodeIndex, V key) {
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
            if (parentChildNodeIndex == 0 && parent.childrenNodes.get(1).children.size() > 1) {
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
                    Node<V, T> addToParentNode =  parent.childrenNodes.get(1);
                    parent.children.add(addToParentNode.children.getFirst());
                    parent.childrenNodes.addAll(addToParentNode.childrenNodes);
                    parent.childrenNodes.remove(1);
                    parent.childrenNodes.removeFirst();

                } else {
                    Node<V, T> addToParentNode =  parent.childrenNodes.get(0);
                    parent.children.add(addToParentNode.children.getFirst());
                    parent.childrenNodes.addAll(addToParentNode.childrenNodes);
                    parent.childrenNodes.clear();
                }
                height--;
            }
        }

    }

    public String toString() {
        return toString(root, height, "") + "\n";
    }
    private String toString(Node<V, T> h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        ArrayList<Entry<V, T>> children = h.children;
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


    private boolean less(V k1, V k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(V k1, V k2) {
        return k1.compareTo(k2) == 0;
    }

    public List<V> getKeys() {
        List<Pair<V, T>> keyValuePairs = new ArrayList<>();
        collectKeyValuePairs(root, keyValuePairs);
        return keyValuePairs.stream().map(Pair::getKey).collect(Collectors.toList());
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