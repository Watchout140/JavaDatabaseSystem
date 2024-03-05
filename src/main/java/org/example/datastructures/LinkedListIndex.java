package org.example.datastructures;


import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
    public List<T> findByPredicate(Predicate<V> predicate) {
        List<T> matches = new ArrayList<>();
        LinkedList.Node<?, ?> node = index.getHead();
        while(node.getNext() != null) {
            if(predicate.test((V)node.getKey())) {
                matches.add((T)node.getData());
            }
            node = node.getNext();
        }
        return matches;
    }

    @Override
    public List<T> getSorted(Comparator<V> c) {
        return index.mergeSort(c);
    }

    @Override
    public List<T> getSortedAscending() {
        return getSorted((Comparator<V>) Comparator.naturalOrder().reversed());
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
        return (Collection<Object>)index.toCollectionData();
    }

    @Override
    public <R> Stream<R> map(Function<? super V, ? extends R> mapper) {
        return index.toCollectionKey().stream().map(mapper);
    }
}
