package org.bumbibjornarna.jds.utilities;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.bumbibjornarna.jds.enums.DataType;
import org.bumbibjornarna.jds.datastructures.BTreeIndex;
import org.bumbibjornarna.jds.datastructures.HashMapIndex;
import org.bumbibjornarna.jds.datastructures.IndexStrategy;
import org.bumbibjornarna.jds.datastructures.LinkedListIndex;
import org.bumbibjornarna.jds.entities.Row;

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
            case "BTREE_INT_STR" -> new BTreeIndex<Integer, String>();
            case "BTREE_STR_INT" -> new BTreeIndex<String, Integer>();
            case "BTREE_STR_STR" -> new BTreeIndex<String, String>();
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
