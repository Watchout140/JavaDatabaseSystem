package org.example.datastructures;

import java.util.function.Predicate;

public class IndexStrategyStream<K, V> implements SpeedStreamInterface<K, V> {
    private IndexStrategy<K, V> indexStrategy;

    public IndexStrategyStream(IndexStrategy<K, V> indexStrategy) {
        this.indexStrategy = indexStrategy;
    }

    @Override
    public V search(Predicate<K> condition) {
        return null;
    }

    /*@Override
    public SpeedStreamInterface<K, V> filter(BiPredicate<K, V> condition) {
        // Implementation depends on how you can filter using IndexStrategy
        return this;
    }
     public Stream<V> toStream() {
        // Convert the current state to a standard Java Stream for operations that are better suited for it
        return indexStrategy.get().values().stream();
    }*/

    // Implement other methods as required
}
