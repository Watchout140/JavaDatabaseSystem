package org.example.dao;

import org.example.entities.Row;
import org.example.entities.Table;

import java.util.Map;

public interface DaoAccessMethods {
    boolean saveTable(Table table);
    boolean delete(String table, Object id);
    Table read(String tableName);
}
