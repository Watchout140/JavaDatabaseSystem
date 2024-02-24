package org.example.entities;

import org.example.datastructures.IndexStrategy;
import org.example.datastructures.IndexStrategyStream;
import org.example.datastructures.SpeedStreamInterface;
import org.example.enums.DataType;

import java.util.*;

import java.util.function.Predicate;

public class Table {
    private final Map<String, Column<?, ?>> columns;
    private final String primaryKey;
    Map<String, String> relationShips;

    private Table(Map<String, Column<?,?>> columns, String primaryKey, Map<String, String> relationsShips) {
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.relationShips = relationsShips;
    }

    public void create(Row row) {
        Map<String, Object> record = row.getData();
        validateRecord(record);
        updateIndexes(record, row);
    }

    private void validateRecord(Map<String, Object> record) {
        for (String columnName : columns.keySet()) {
            if (!record.containsKey(columnName)) {
                throw new IllegalArgumentException("Missing value for column: " + columnName);
            }
            // Additional type checks can be added here
        }
    }

    private void updateIndexes(Map<String, Object> record, Row row) {
        for (Map.Entry<String, Column<?, ?>> entry : columns.entrySet()) {
            String columnName = entry.getKey();
            Column<?,?> column = entry.getValue();
            Object key = record.get(columnName);
            Object value = record.get(primaryKey);

            column.indexStrategy.ifPresent(strategy -> {
                IndexStrategy<Object, Object> objectIndexStrategy = (IndexStrategy<Object,Object>) strategy;
                if (column.isPrimary) {
                    if (!objectIndexStrategy.contains(key))
                        objectIndexStrategy.insert(key, row);
                    else System.out.println("DUE SÄMST: " + key + " " + row);
                }
                else objectIndexStrategy.insert(key, value);
            });
        }
    }

    public List<Row> find(Predicate<Row> predicate) {
        List<Row> matchingRows = new ArrayList<>();
        for (Column<?, ?> column : columns.values()) {
            if (column.indexStrategy.isPresent()) {
                IndexStrategy<Object, Object> strategy = (IndexStrategy<Object, Object>) column.indexStrategy.get();
                strategy.getAllRecords().stream()
                        .filter(obj -> obj instanceof Row)
                        .map(obj -> (Row) obj)
                        .filter(predicate)
                        .forEach(matchingRows::add);
            }
        }
        return matchingRows;
    }

    public Row findFirst(Predicate<Row> predicate) {
        for (Column<?, ?> column : columns.values()) {
            if (column.indexStrategy.isPresent()) {
                IndexStrategy<Object, Object> strategy = (IndexStrategy<Object, Object>) column.indexStrategy.get();
                Optional<Row> firstMatch = strategy.getAllRecords().stream()
                        .filter(obj -> obj instanceof Row)
                        .map(obj -> (Row) obj)
                        .filter(predicate)
                        .findFirst();
                if (firstMatch.isPresent()) {
                    return firstMatch.get();
                }
            }
        }
        return null;
    }

    public Row find(Object primaryKeyValue) {
        Column<?, ?> primaryKeyColumn = columns.get(primaryKey);
        if (primaryKeyColumn != null && primaryKeyColumn.indexStrategy.isPresent()) {
            IndexStrategy<Object, Object> strategy = (IndexStrategy<Object, Object>) primaryKeyColumn.indexStrategy.get();
            Object row = strategy.find(primaryKeyValue);
            if (row instanceof Row) {
                return (Row) row;
            }
        }
        return null;
    }

    public Row findFirst(String column, Object value) {
        Column<?,?> col = getColumn(column);
        Object foundVal = col.findByVal(value);
        if (column.equals(primaryKey)) {
            return (Row)foundVal;
        }
        Column<?,?> col2 = getColumn(primaryKey);
        return (Row)col2.findByVal(foundVal);
    }
    public List<Row> findAll(String column, Object value) {
        Column<?,?> col = getColumn(column);
        Object foundVal = col.findAllByVal(value);
        List<Row> list = new ArrayList<>();
        List<?> obj = (List<?>) foundVal;
        if (column.equals(primaryKey)) {
            return (List<Row>)foundVal;
        }

        Column<?,?> col2 = getColumn(primaryKey);
        obj.forEach(o -> list.add((Row)col2.findByVal(o)));
        return list;
    }

    public Column<?,?> getColumn(String column) {
        return columns.get(column);
    }

    // Other CRUD methods

    // Nested Column class
    public static class Column<V, T> {
        String name;
        DataType type;
        Optional<IndexStrategy<V, T>> indexStrategy;
        boolean isPrimary;


        Column(String name, DataType type, IndexStrategy<V, T> indexStrategy, boolean isPrimaryKey) {
            this.name = name;
            this.type = type;
            this.indexStrategy = Optional.ofNullable(indexStrategy);
            this.isPrimary = isPrimaryKey;
        }
        public SpeedStreamInterface<V,T> Stream() {
            if (indexStrategy.isPresent()) {
                return new IndexStrategyStream<>(indexStrategy.get());
            } else {
                throw new IllegalStateException("Index strategy not available.");
            }
        }
        public Object findByVal(Object val) {
            V test = (V) val;
            Object value = indexStrategy.get().find(test);

            return value;
        }
        public Object findAllByVal(Object val) {
            V test = (V) val;
            Object value = indexStrategy.get().findAll(test);
            return value ;
        }

        public String getName() {
            return name;
        }
    }

    // Table builder static inner class
    public static class Builder {
        private final Map<String, Column<?,?>> columns = new HashMap<>();
        private Map<String, String> relations = new HashMap<>();

        private String primaryKey;
        // Add a column with an index
        public <V, T> Builder addColumn(String name, DataType type, IndexStrategy<V, T> indexStrategy) {
            columns.put(name, new Column<>(name, type, indexStrategy, false));
            return this;
        }
        public <V, T> Builder addColumn(String name, DataType type, IndexStrategy<V, T> indexStrategy, String relationShip) {
            relations.put(name, relationShip);
            columns.put(name, new Column<>(name, type, indexStrategy, false));
            return this;
        }
        public <V, T> Builder addColumn(String name, DataType type, IndexStrategy<V, T> indexStrategy, boolean isPrimaryKey) {
            if (primaryKey != null && isPrimaryKey) return null; //trow
            if (isPrimaryKey) {
                this.primaryKey = name;
            }
            columns.put(name, new Column<>(name, type, indexStrategy, isPrimaryKey));
            return this;
        }


        // Add a column without an index
        public Builder addColumn(String name, DataType type) {
            columns.put(name, new Column<>(name, type, null, false));
            return this;
        }

        public Table build() {
            return new Table(new HashMap<>(columns), primaryKey, relations);
        }
    }

    public String toString() {
        return null;
    }
}
