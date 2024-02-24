package org.example.datastructures;

import java.util.function.Predicate;

public interface SpeedStreamInterface<K, V> {

    V search(Predicate<K> predicate);
    //SpeedStreamInterface<K> filter(BiPredicate<K> condition);
}

