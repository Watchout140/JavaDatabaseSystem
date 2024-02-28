package org.example.datastructures;

import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.function.Predicate;

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
    public List<T> findByPredicate(Predicate predicate) {
        List<T> matches = new ArrayList<>();
        LinkedList.Node<?, ?> node = index.getHead();
        while(node.getNext() != null) {
            T data = (T) node.getData();
            if(predicate.test(data)) {
                matches.add(data);
            }
        }
        return matches;
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
