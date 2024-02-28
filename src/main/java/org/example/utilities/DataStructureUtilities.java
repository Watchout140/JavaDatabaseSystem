package org.example.utilities;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.example.datastructures.BTreeIndex;
import org.example.datastructures.HashMapIndex;
import org.example.datastructures.IndexStrategy;
import org.example.datastructures.LinkedListIndex;
import org.example.entities.Row;
import org.example.enums.DataType;

public class DataStructureUtilities {

    public static IndexStrategy<?, ?> getDataStructure(String dataStruct) {
        return switch (dataStruct) {
            case "HASH_INT_ROW" -> new HashMapIndex<Integer, Row>();
            case "HASH_STR_ROW" -> new HashMapIndex<String, Row>();
            case "HASH_INT_STR" -> new HashMapIndex<Integer, String>();
            case "HASH_STR_INT" -> new HashMapIndex<String, Integer>();
            case "HASH_INT_INT" -> new HashMapIndex<Integer, Integer>();
            case "HASH_STR_STR" -> new HashMapIndex<String, String>();
            case "LINKED_INT_STR" -> new LinkedListIndex<Integer, String>();
            case "LINKED_STR_INT" -> new LinkedListIndex<String, Integer>();
            case "LINKED_INT_INT" -> new LinkedListIndex<Integer, Integer>();
            case "LINKED_STR_STR" -> new LinkedListIndex<String, String>();
            case "BTREE_INT_INT" -> new BTreeIndex<Integer, Integer>();
            default -> null;
        };
    }

    public static ArrowType DataTypeToArrowType(DataType dataType) {
        return switch (dataType) {
            case INT -> new ArrowType.Int(32, true);
            case STRING -> ArrowType.Utf8.INSTANCE;
            default -> null;
        };
    }

    public static DataType ArrowTypeToDataType(ArrowType arrowType) {
        if (arrowType instanceof ArrowType.Int) {
            return DataType.INT;
        } else if (arrowType.equals(ArrowType.Utf8.INSTANCE)) {
            return DataType.STRING;
        }

        return null;
    }
}
