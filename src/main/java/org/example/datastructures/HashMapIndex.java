package org.example.datastructures;

import org.example.enums.DataType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public List<T> findByPredicate(Predicate predicate) {
        return (List<T>) index.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
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


    // Implement methods
}
