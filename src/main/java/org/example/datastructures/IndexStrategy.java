package org.example.datastructures;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface IndexStrategy<V, T> {
    void insert(V key, T value);
    T find(V key);
    List<T> findByPredicate(Predicate<V> predicate);
    boolean contains(V key);
    List<T> findAll(V key);
    void delete(V key);
    Collection<Object> getAllRecords();
}

