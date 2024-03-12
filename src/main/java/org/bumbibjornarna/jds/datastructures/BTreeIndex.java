package org.bumbibjornarna.jds.datastructures;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BTreeIndex<V extends Comparable<V>,T> implements IndexStrategy<V, T> {
    private BTree<V, T> bTree = new BTree<>();
    @Override
    public void insert(V key, T value) {
        bTree.put(key, value);
    }

    @Override
    public T find(V key) {
        return bTree.find(key);
    }

    @Override
    public List<T> findByPredicate(Predicate<V> predicate) {
        List<T> list = bTree.find(predicate);
        return list;
    }

    @Override
    public List<T> getSorted(Comparator<V> c) {
        return bTree.getSortedComp(c);
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

    @Override
    public <R> Stream<R> map(Function<? super V, ? extends R> mapper) {
        return bTree.getKeys().stream().map(mapper);
    }
}
