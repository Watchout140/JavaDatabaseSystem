package org.example.datastructures;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
public class HashSpeedStream<K, V> implements SpeedStreamInterface<K, V> {

    private final Map<K, V> data;

    public HashSpeedStream(Map<K, V> data) {
        this.data = data;
    }

    @Override
    public V search(Predicate<K> condition) {
        return null;
    }
    /*@Override
    public HashSpeedStream<K, V> filter(BiPredicate<K, V> condition) {
        HashMap<K, V> filtered = new HashMap<>();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            if (condition.test(entry.getKey(), entry.getValue())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return new HashSpeedStream<>(filtered);
    }*/

    // Implement other stream-like methods (map, reduce) similarly

    public Stream<V> toStream() {
        // Convert the current state to a standard Java Stream for operations that are better suited for it
        return data.values().stream();
    }

}
