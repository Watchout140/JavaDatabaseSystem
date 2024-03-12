package org.example.entities;

import org.example.dao.DaoAccessMethods;
import org.example.dao.DaoArrow;
import org.example.entities.Row;
import org.example.entities.Table;

import java.util.*;
import java.util.function.Predicate;

public class Database {
    protected static Map<String, Table> tables = new HashMap<>();
    private final DaoAccessMethods dao = new DaoArrow();
    private final Set<String> savedTables = new HashSet<>();

    public Database() { }

    public boolean save(Table table) {
        savedTables.clear();
        return saveTable(table);
    }
    public boolean readAll() {
        tables = dao.readAllTables(this);
        return true;
    }
    private boolean saveTable(Table table) {
        if(table.relationShips != null) {
            for(String tableName: table.relationShips.values()) {
                if(!savedTables.contains(tableName)) {
                    savedTables.add(tableName);
                    saveTable(tables.get(tableName));
                }
            }
        }
        return dao.saveTable(table);
    }

    public Table read(String tableName) {
        return dao.read(tableName, this);
    }

    public Table createTable(String name, Table.Builder builder) {
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException("Table already exists: " + name);
        }
        Table table = builder.build();
        table.setName(name);
        tables.put(name, table);
        return table;
    }
    public Row find(String tableStr, Object primaryKeyValue) {
        Table table = tables.get(tableStr);
        if (table == null) return null;
        Row row = table.find(primaryKeyValue).copy();
        if (!table.relationShips.isEmpty()) {
            for (Map.Entry<String, String> r : table.relationShips.entrySet()) {
                Object primaryKey = row.get(r.getKey());
                Table relTable = tables.get(r.getValue());
                Row relRow = relTable.find(primaryKey);
                row.set(r.getKey(), relRow);
            }
        }
        return row;
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