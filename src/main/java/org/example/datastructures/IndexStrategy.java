package org.example.datastructures;

import java.util.Collection;
import java.util.List;

public interface IndexStrategy<V, T> {
    void insert(V key, T value);
    T find(V key);
    boolean contains(V key);
    List<T> findAll(V key);
    void delete(V key);
    Collection<Object> getAllRecords();
}

