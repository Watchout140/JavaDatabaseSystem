package org.bumbibjornarna.jds.dao;

import org.bumbibjornarna.jds.entities.Table;
import org.bumbibjornarna.jds.entities.Database;

import java.util.Map;

public interface DaoAccessMethods {
    boolean saveTable(Table table);
    boolean delete(String table, Object id);
    Table read(String tableName, Database dataBase);
    Map<String, Table> readAllTables(Database db);
}
