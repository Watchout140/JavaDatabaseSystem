package org.example.datastructures;

import java.util.*;

public class LinkedListIndex<V, T> implements IndexStrategy<V, T> {
    private LinkedList<V, T> index = new LinkedList<>();

    @Override
    public void insert(V key, T value) {
        index.add(key, value);
    }

    @Override
    public T find(V key) {
        System.out.println(index.toString());
        System.out.println("VAL: " + index.getByKey(key) + " Key: " + key);
        return index.getByKey(key);
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

    }

    @Override
    public Collection<Object> getAllRecords() {
        return (Collection<Object>)index.toCollection();
    }

    // Implement methods
}
