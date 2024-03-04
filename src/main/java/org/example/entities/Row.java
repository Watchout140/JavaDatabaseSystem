package org.example.entities;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private final Map<String, Object> data = new HashMap<>();

    public Row set(String columnName, Object value) {
        data.put(columnName, value);
        return this;
    }

    public int getInt(String col) {
        return (int)data.get(col);
    }

    public Object get(String col) {
        return data.get(col);
    }

    Map<String, Object> getData() {
        return data;
    }
    public Row copy() {
        Row newRow = new Row();
        for (Map.Entry<String, Object> entry : this.data.entrySet()) {
            newRow.set(entry.getKey(), entry.getValue());
        }
        return newRow;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
