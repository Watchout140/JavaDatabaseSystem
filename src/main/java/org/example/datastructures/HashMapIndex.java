package org.example.datastructures;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMapIndex<V, T> implements IndexStrategy<V, T> {
    private Map<V, Object> index = new HashMap<>();


    @Override
    public void insert(V key, T value) {
        Object currentValue = index.get(key);

        if (currentValue instanceof List) {
            ((List<T>) currentValue).add(value);
        } else if (currentValue != null) {
            List<T> newList = new ArrayList<>();
            newList.add((T) currentValue);
            newList.add(value);
            index.put(key, newList);
        } else {
            index.put(key, value);
        }
    }

    @Override
    public T find(V key) {
        Object val = index.get(key);
        if (val instanceof List) {
            List<T> list = (List<T>) index.get(key);
            return list.getFirst();
        }
        return (T) val;
    }

    @Override
    public List<T> getSorted(Comparator<V> comparator) {
        List<T> sortedList = new ArrayList<>();

        List<Map.Entry<V, Object>> sortedEntries = index.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(comparator))
                .collect(Collectors.toList());

        for (Map.Entry<V, Object> entry : sortedEntries) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<T> listValue = (List<T>) value;
                sortedList.addAll(listValue);
            } else {
                sortedList.add((T) value);
            }
        }

        return sortedList;
    }

    @Override
    public List<T> getSortedAscending() {
        return getSorted((Comparator<V>) Comparator.naturalOrder().reversed());
    }

    @Override
    public List<T> findByPredicate(Predicate<V> predicate) {
        List<T> list = new ArrayList<>();
        for (Map.Entry<V, Object> entry: index.entrySet()) {
            list.add((T) entry.getValue());
        }
        return list;
    }

    @Override
    public boolean contains(V key) {
        return index.containsKey(key);
    }

    @Override
    public List<T> findAll(V key) {
        Object val = index.get(key);
        if (!(val instanceof List<?>)) {
            List<T> list = new ArrayList<>();
            list.add((T) val);
            return list;
        }
        return (List<T>) val;
    }

    @Override
    public void delete(V key) {

    }

    @Override
    public Collection<Object> getAllRecords() {
        return index.values();
    }

    @Override
    public <R> Stream<R> map(Function<? super V, ? extends R> mapper) {
        return index.keySet().stream().map(mapper);
    }
}
