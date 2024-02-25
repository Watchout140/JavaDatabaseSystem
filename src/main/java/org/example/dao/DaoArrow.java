package org.example.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.util.Text;
import org.example.datastructures.HashMapIndex;
import org.example.entities.Row;
import org.example.entities.Table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.util.List;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.types.pojo.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DaoArrow implements DaoAccessMethods {

    private final String DATABASE_FOLDER_PATH = "database/";

    @Override
    public boolean saveTable(Table table) {
        Schema arrowSchema = convertToArrowSchema(table);
        System.out.println("Arrow schema: " + arrowSchema);

        try (BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            try (VectorSchemaRoot vectorSchemaRoot = VectorSchemaRoot.create(arrowSchema, allocator)) {
                // Retrieve all rows from the table
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
                                ((VarCharVector) vector).setSafe(rowIndex, new Text((String) value).getBytes());
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
                    System.out.println("Record batches written: " + writer.getRecordBlocks().size() + ". Number of rows written: " + vectorSchemaRoot.getRowCount());
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
            ArrowType arrowType = switch (column.getType()) {
                case INT -> new ArrowType.Int(32, true);
                case STRING -> ArrowType.Utf8.INSTANCE;
                /*case FLOAT:
                    arrowType = new ArrowType.FloatingPoint(ArrowType.FloatingPointPrecision.SINGLE);
                    break;
                case DOUBLE:
                    arrowType = new ArrowType.FloatingPoint(ArrowType.FloatingPointPrecision.DOUBLE);
                    break;
                case BOOLEAN:
                    arrowType = ArrowType.Bool.INSTANCE;
                    break;*/
                default -> throw new IllegalArgumentException("Unsupported column type: " + column.getType());
            };

            Field field = new Field(column.getName(), FieldType.nullable(arrowType), null);
            fields.add(field);

            if (column.isPrimaryKey()) {
                primaryColumn = column.getName();
            }
        }

        if (primaryColumn != null) {
            schemaMetadata.put("primaryKeyColumn", primaryColumn);
        }

        return new Schema(fields, schemaMetadata);
    }

    @Override
    public boolean delete(String table, Object id) {
        // Implementation for deleting a row based on id
        return false;
    }

    @Override
    public Table read(String tableName) {
        // Initialize allocator for Arrow operations
        try (BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            File file = new File(DATABASE_FOLDER_PATH + tableName + ".arrow");

            // Open Arrow file for reading
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 ArrowFileReader reader = new ArrowFileReader((SeekableReadChannel) Channels.newChannel(fileInputStream), allocator)) {

                Number a1 = null;
                String arg1 = "STR";
                if(arg1.equals("STR")) {
                    a1 = Integer.class;
                }

                switch(datastructure) {
                    "HASH_INT_STR": HashMapIndex<Integer, String> s;
                }


                VectorSchemaRoot root = reader.getVectorSchemaRoot();
                Schema schema = root.getSchema();
                /*List<Row> rows = new ArrayList<>();

                // Read batches from the Arrow file
                while (reader.loadNextBatch()) {
                    int rowCount = root.getRowCount();

                    // Convert each row in the batch to a Row object
                    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                        Row row = new Row();
                        int finalRowIndex = rowIndex;
                        schema.getFields().forEach(field -> {
                            Object value = root.getVector(field.getName()).getObject(finalRowIndex);
                            row.set(field.getName(), value);
                            System.out.println(value);
                        });
                        rows.add(row);
                    }
                }*/

                // Create and return the Table object populated with rows
                // Assuming Table has a constructor that takes a List<Row>
                //return new Table(tableName, rows, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}