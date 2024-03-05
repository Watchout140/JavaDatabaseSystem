package org.example.entities;

import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.example.datastructures.IndexStrategy;
import org.example.datastructures.IndexStrategyStream;
import org.example.datastructures.SpeedStreamInterface;
import org.example.enums.DataType;

import org.apache.arrow.vector.*;
import org.example.utilities.DataStructureUtilities;

import java.util.List;
import java.util.Map;

import java.util.*;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final Map<String, Column<?, ?>> columns;
    private final String primaryKey;
    private String tableName = "Unknown";
    Map<String, String> relationShips;

     private Table(Map<String, Column<?,?>> columns, String primaryKey, Map<String, String> relationsShips) {
         this.columns = columns;
         this.primaryKey = primaryKey;
         this.relationShips = relationsShips;
    }

    public void setName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return tableName;
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

    public List<Column<?, ?>> getColumns() {
        return columns.values().stream().toList();
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
            if (column.indexStrategy.isPresent() && column.isPrimary) {
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
            if (column.indexStrategy.isPresent() && column.isPrimary) {
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

    private Column<?,?> getColumn(String column) {
         return columns.get(column);
    }

    public <T> Column<T, ?> getColumn(String columnName, Class<T> type) {
        Column<?, ?> rawColumn = columns.get(columnName);
        if (rawColumn != null && type.isAssignableFrom(rawColumn.type.getDataClass())) {
            return (Column<T, ?>) rawColumn;
        } else {
            throw new ClassCastException("Incompatible column type.");
        }
    }

    public List<Row> getAllRows() {
        List<Row> matchingRows = new ArrayList<>();
        for (Column<?, ?> column : columns.values()) {
            if (column.indexStrategy.isPresent() && column.isPrimary) {
                IndexStrategy<Object, Object> strategy = (IndexStrategy<Object, Object>) column.indexStrategy.get();
                strategy.getAllRecords().stream()
                        .filter(obj -> obj instanceof Row)
                        .map(obj -> (Row) obj)
                        .forEach(matchingRows::add);
            }
        }
        return matchingRows;
    }


    // Other CRUD methods

    // Nested Column class
    public static class Column<V, T> {
        String name;
        DataType type;
        Optional<IndexStrategy<V, T>> indexStrategy = Optional.empty();
        boolean isPrimary;
        String dataStructTypes;
        Table table;

        Column(String name, DataType type, String indexStrategyCode, boolean isPrimaryKey) {
            this.name = name;
            this.type = type;
            this.isPrimary = isPrimaryKey;
            this.dataStructTypes = indexStrategyCode;
            IndexStrategy<?, ?> indexStrategy = null;
            if(indexStrategyCode != null) indexStrategy = DataStructureUtilities.getDataStructure(indexStrategyCode);
            if(indexStrategy != null) this.indexStrategy = Optional.of((IndexStrategy<V, T>) indexStrategy);
        }

        public Row getRow(Object index) {
            return table.columns.get(table.primaryKey).indexStrategy.map(
                    strategy -> (Row) strategy.findByPredicate(
                            id -> id == index
                    ).getFirst()
            ).orElse(null);
        }

        public List<Row> getRows(Set<Object> indexes) {
            List<Row> matchingRows = new ArrayList<>();
            for(Object rowObj: table.columns.get(table.primaryKey).indexStrategy.get().getAllRecords()) {
                Row row = ((Row)rowObj).copy();
                for(Map.Entry<String, String> relation: table.relationShips.entrySet()) {
                    Object relationId = row.get(relation.getKey());
                    if(relationId == null) continue;
                    Table relationTable = Database.tables.get(relation.getValue());
                    Row relationRow = relationTable.findFirst(relationTable.primaryKey, row.get(relation.getKey()));
                    row.set(relation.getKey(), relationRow);
                }
                if(indexes.contains((row).get(table.primaryKey))) {
                    matchingRows.add(row);
                }
            }
            return matchingRows;
        }

        public List<Row> getRowsSorted(List<T> indexes) {

            List<Row> allRows = table.columns.get(table.primaryKey).indexStrategy.get().getAllRecords().stream()
                    .map(rowObj -> (Row) rowObj)
                    .toList();

            List<Row> sortedRows = new ArrayList<>();

            for (Object index : indexes) {
                allRows.stream()
                        .filter(row -> index.equals(row.get(table.primaryKey)))
                        .findFirst()
                        .ifPresent(sortedRows::add);
            }

            return sortedRows;
        }

        public boolean isPrimaryKey() {
            return this.isPrimary;
        }
        public SpeedStreamInterface<V,T> Stream() {
            if (indexStrategy.isPresent()) {
                return new IndexStrategyStream<>(indexStrategy.get());
            } else {
                throw new IllegalStateException("Index strategy not available.");
            }
        }

        private List<Row> getRowsFromIndex(List<T> indexes) {
            return null;
        }

        public List<Row> filter(Predicate<V> predicate) {
            return getRows(new HashSet<>(indexStrategy.get().findByPredicate(predicate)));
        }

        public Object findByVal(Object val) {
            V test = (V) val;
            Object value = indexStrategy.get().find(test);

            return value;
        }

        public List<Row> sort(Comparator<V> c) {
            return getRowsSorted(indexStrategy.get().getSorted(c));
        }

        public <R> Stream<R> map(Function<? super V, ? extends R> mapper) {
            return indexStrategy.get().map(mapper);
        }

        public List<Row> sortAsc() {
            return getRowsSorted(indexStrategy.get().getSortedAscending());
        }

        public List<Row> sortDec() {
            return getRowsSorted(indexStrategy.get().getSortedAscending()).reversed();
        }

        public void delete(Object val) {
            V test = (V) val;
            indexStrategy.get().delete(test);

        }
        public Object findAllByVal(Object val) {
            return indexStrategy.get().findAll((V) val) ;
        }
        public String getDataStructTypes() {
            return dataStructTypes;
        }

        public String getName() {
            return name;
        }

        public DataType getType() {
            return type;
        }
    }

    public static class Builder {
        private final Map<String, Column<?,?>> columns = new HashMap<>();
        private final Map<String, String> relations = new HashMap<>();

        private String primaryKey;

        public <V, T> Builder addColumn(String name, DataType type, String indexStrategy) {
            columns.put(name, new Column<>(name, type, indexStrategy, false));
            return this;
        }
        public <V, T> Builder addColumn(String name, DataType type, String indexStrategy, String relationShip) {
            relations.put(name, relationShip);
            columns.put(name, new Column<>(name, type, indexStrategy, false));
            return this;
        }
        public <V, T> Builder addColumn(String name, DataType type, String indexStrategy, boolean isPrimaryKey) {
            if (isPrimaryKey) {
                this.primaryKey = name;
            }
            columns.put(name, new Column<>(name, type, indexStrategy, isPrimaryKey));
            return this;
        }

        public Builder addColumn(String name, DataType type) {
            columns.put(name, new Column<>(name, type, null, false));
            return this;
        }

        public Table build() {
            Table table = new Table(new HashMap<>(columns), primaryKey, relations);
            for(Column<?, ?> column: table.getColumns()) {
                column.table = table;
            }
            return table;
        }
    }

    @Override
    public String toString() {
         StringBuilder out = new StringBuilder(tableName + "\n");
         for(String colName: columns.keySet()) {
             out.append(colName).append(",");
         }
         for(Row row: getAllRows()) {
             for(String colName: columns.keySet()) {
                 out.append("\n").append(row.get(colName)).append(",");
             }
         }
         return out.toString();
    }
}
