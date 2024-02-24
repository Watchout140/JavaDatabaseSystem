package org.example.entities;

import org.example.entities.Row;
import org.example.entities.Table;

import java.util.*;
import java.util.function.Predicate;

public class Database {
    private final Map<String, Table> tables;
    private Row row;

    public Database() {
        this.tables = new HashMap<>();
    }

    public Table createTable(String name, Table.Builder builder) {
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException("Table already exists: " + name);
        }
        Table table = builder.build();
        tables.put(name, table);
        return table;
    }
    public Row find(String tableStr, Object primaryKeyValue) {
        Table table = tables.get(tableStr);
        Row row = table.find(primaryKeyValue).copy();
        if (!table.relationShips.isEmpty()) {
            for (Map.Entry<String, String> r : table.relationShips.entrySet()) {
                Object primkey = row.get(r.getKey());
                Table relTable = tables.get(r.getValue());
                Row relRow = relTable.find(primkey);
                row.set(r.getKey(), relRow);
            }
        }
        return row;
    }
    public List<Row> find(String tableStr, Predicate<Row> predicate) {

        return tables.get(tableStr).find(predicate);
    }
    public Row findFirst(String tableStr, Predicate<Row> predicate) {

        return tables.get(tableStr).findFirst(predicate);
    }
    public Row findFirst(String tableStr, String column, Object value) {

        return tables.get(tableStr).findFirst(column, value);
    }
    public Table getTable(String name) {
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("Table does not exist: " + name);
        }
        return tables.get(name);
    }

    public void deleteTable(String name) {
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("Table does not exist: " + name);
        }
        tables.remove(name);
    }
}