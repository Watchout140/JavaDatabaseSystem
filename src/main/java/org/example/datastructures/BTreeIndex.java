package org.example.datastructures;

import com.sun.source.doctree.BlockTagTree;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BTreeIndex<V extends Comparable<V>,T> implements IndexStrategy<V, T> {
    private BTree<V, T> bTree = new BTree<>();
    @Override
    public void insert(V key, T value) {
        bTree.put(key, value);
        System.out.println(bTree.toString());
    }

    @Override
    public T find(V key) {
        return null;//bTree.get(key);
    }

    @Override
    public List<T> findByPredicate(Predicate<V> predicate) {
        List<T> list = bTree.find(predicate);
        System.out.println("LIST: "+  list);
        return list;
    }

    @Override
    public List<T> getSorted(Comparator<V> c) {
        List<T> sortedList = bTree.getSortedComp(c);
        return sortedList;
    }

    @Override
    public List<T> getSortedAscending() {
        return bTree.getSorted();
    }

    @Override
    public boolean contains(V key) {
        return false;
    }

    @Override
    public List<T> findAll(V key) {
        return null;
    }

    @Override
    public void delete(V key) {
        bTree.remove(key);
    }

    @Override
    public Collection<Object> getAllRecords() {
        return null;
    }
}
