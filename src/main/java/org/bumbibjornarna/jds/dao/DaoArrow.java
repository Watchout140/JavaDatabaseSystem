package org.bumbibjornarna.jds.dao;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.bumbibjornarna.jds.entities.Database;
import org.bumbibjornarna.jds.entities.Row;
import org.bumbibjornarna.jds.entities.Table;
import org.bumbibjornarna.jds.enums.DataType;
import org.bumbibjornarna.jds.utilities.DataStructureUtilities;
import org.bumbibjornarna.jds.utilities.HashMapConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class DaoArrow implements DaoAccessMethods {

    private final String DATABASE_FOLDER_PATH = "database/";

    @Override
    public boolean saveTable(Table table) {
        Schema arrowSchema = convertToArrowSchema(table);

        try (BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            try (VectorSchemaRoot vectorSchemaRoot = VectorSchemaRoot.create(arrowSchema, allocator)) {
                List<Row> allRows = table.getAllRows();

                for (Field field : arrowSchema.getFields()) {
                    FieldVector vector = vectorSchemaRoot.getVector(field.getName());
                    vector.allocateNew();

                    for (int rowIndex = 0; rowIndex < allRows.size(); rowIndex++) {
                        Row row = allRows.get(rowIndex);
                        Object value = row.get(field.getName());

                        if (value != null) {
                            if (vector instanceof IntVector) {
                                ((IntVector) vector).setSafe(rowIndex, (Integer) value);
                            } else if (vector instanceof VarCharVector) {
                                ((VarCharVector) vector).setSafe(rowIndex, ((String)value).getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                    vector.setValueCount(allRows.size());
                }
                vectorSchemaRoot.setRowCount(allRows.size());

                File file = new File(DATABASE_FOLDER_PATH + table.getName() + ".arrow");
                try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                     ArrowFileWriter writer = new ArrowFileWriter(vectorSchemaRoot, null, fileOutputStream.getChannel())) {
                    writer.start();
                    writer.writeBatch();
                    writer.end();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Schema convertToArrowSchema(Table table) {
        List<Field> fields = new ArrayList<>();
        String primaryColumn = null;
        Map<String, String> schemaMetadata = new HashMap<>();
        for (Table.Column<?, ?> column : table.getColumns()) {
            ArrowType arrowType = DataStructureUtilities.DataTypeToArrowType(column.getType());
            String dataStruct = column.getDataStructTypes();
            if (dataStruct != null) schemaMetadata.put(column.getName(), dataStruct);

            Field field = new Field(column.getName(), FieldType.nullable(arrowType), null);
            fields.add(field);

            if (column.isPrimaryKey()) {
                primaryColumn = column.getName();
            }
        }

        if (primaryColumn != null) {
            schemaMetadata.put("primaryKeyColumn", primaryColumn);
        }

        schemaMetadata.put("relationship", HashMapConverter.hashMapToString(table.getRelationShips()));

        return new Schema(fields, schemaMetadata);
    }

    @Override
    public boolean delete(String table, Object id) {
        // Implementation for deleting a row based on id
        return false;
    }

    @Override
    public Table read(String tableName, Database database) {
        Table.Builder tableBuilder = new Table.Builder();
        Table table = null;
        try (RootAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            File file = new File(DATABASE_FOLDER_PATH + tableName);
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileChannel fileChannel = fileInputStream.getChannel();
                 ArrowFileReader reader = new ArrowFileReader(fileChannel, allocator)) {

                VectorSchemaRoot root = reader.getVectorSchemaRoot();
                Map<String, String> metaData = root.getSchema().getCustomMetadata();

                while (reader.loadNextBatch()) {
                    List<FieldVector> fieldVectors = root.getFieldVectors();
                    for (FieldVector vector : fieldVectors) {
                        String colName = vector.getField().getName();
                        String colIndexStrategy = null;
                        boolean isPrimaryKey = metaData.get("primaryKeyColumn").equals(colName);
                        DataType dataType = DataStructureUtilities.ArrowTypeToDataType(vector.getField().getType());

                        if(metaData.containsKey(colName)) colIndexStrategy = metaData.get(colName);

                        tableBuilder.addColumn(colName, dataType, colIndexStrategy, isPrimaryKey);
                    }
                    table = database.createTable(tableName.replace(".arrow", ""), tableBuilder);

                    for (int i = 0; i < fieldVectors.getFirst().getValueCount(); i++) {
                        Row row = new Row();
                        for (FieldVector vector : fieldVectors) {
                            row.set(vector.getField().getName(), getValueFromVector(vector, i));
                        }
                        table.create(row);
                    }
                }

                assert table != null;
                table.setRelationShips(HashMapConverter.stringToHashMap(metaData.get("relationship")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Table> readAllTables(Database db) {
        Map<String, Table> tables = new HashMap<>();
        Set<String> set = listFilesUsingJavaIO("database");
        set.forEach(file -> {
            Table table = read(file, db);
            tables.put(table.getName(), table);
        });
        return tables;
    }

    private Object getValueFromVector(FieldVector vector, int index) {
        if (vector instanceof IntVector) {
            if (vector.isNull(index)) return null;
            return ((IntVector) vector).get(index);
        } else if (vector instanceof VarCharVector) {
            if (vector.isNull(index)) return null;
            byte[] bytes = ((VarCharVector) vector).get(index);
            return new String(bytes, StandardCharsets.UTF_8); // Specify charset
        }
        return null;
    }

}