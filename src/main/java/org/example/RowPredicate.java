package org.example;

import org.example.entities.Row;

@FunctionalInterface
public interface RowPredicate {
    boolean test(Row row);
}
