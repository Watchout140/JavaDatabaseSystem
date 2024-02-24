package org.example.dao;

import org.example.entities.Row;
import org.example.entities.Table;

import java.util.Map;

public interface DaoAccessMethods {
    boolean save(String table, Row row);
    boolean delete(String table, Object id);
    boolean update(String table, Row row);
    Map<String, Table> read();
}
