package org.example;

import org.example.datastructures.HashMapIndex;
import org.example.entities.Database;
import org.example.entities.Row;
import org.example.entities.Table;
import org.example.enums.DataStructure;
import org.example.enums.DataType;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();

        Table.Builder studentTableBuilder = new Table.Builder()
                .addColumn("id", DataType.INT, new HashMapIndex<Integer, Row>(), true)
                .addColumn("name", DataType.STRING, new HashMapIndex<String, Integer>())
                .addColumn("grade", DataType.INT)
                .addColumn("class_id", DataType.INT, null, "Class");
        Table studentTable = db.createTable("Student_t", studentTableBuilder);

        Table.Builder classTableBuilder = new Table.Builder()
                .addColumn("id", DataType.INT, new HashMapIndex<Integer, Row>(), true)
                .addColumn("class_name", DataType.STRING, new HashMapIndex<String, Integer>())
                .addColumn("teacher", DataType.STRING);
        Table classTable = db.createTable("Class", classTableBuilder);
        classTable.create(new Row().set("id", 1).set("class_name", "Svenska").set("teacher", "Björn Johansson"));
        System.out.println("CLASS: " + classTable.find(1));

        Row newStudent = new Row()
                .set("id", 1)
                .set("name", "Alice Johnson")
                .set("grade", 80)
                .set("class_id", null);
        Row newStudent2 = new Row()
                .set("id", 2)
                .set("name", "Patrik Lind")
                .set("grade", 80)
                .set("class_id", 1);
        Row newStudent3 = new Row()
                .set("id", 3)
                .set("name", "Vincent Hedblom")
                .set("grade", 10)
                .set("class_id", null);
        Row newStudent4 = new Row()
                .set("id", 4)
                .set("name", "Haris Hadziabdic")
                .set("grade", 80)
                .set("class_id", null);
        Row newStudent5 = new Row()
                .set("id", 6)
                .set("name", "Patrik Lind")
                .set("grade", 100)
                .set("class_id", null);

        studentTable.create(newStudent);
        studentTable.create(newStudent2);
        studentTable.create(newStudent3);
        studentTable.create(newStudent4);
        studentTable.create(newStudent5);
        System.out.println("WALLA: " + db.find("Student_t", 2));
        Row matchingRows = studentTable.findFirst("id", 2);
        List<Row> list = studentTable.findAll("name", "Patrik Lind");
        System.out.println("LIST: " + list);
        System.out.println("MATCHINGROWS: " + matchingRows);

        List<Row> rows = studentTable.find(row -> row.getInt("grade") > 70);
        System.out.println("ROWS: " + rows);

        Row student = studentTable.findFirst(row -> row.get("name").equals("Vincent Hedblom"));
        System.out.println("STUIDENT: " + student);

        db.save(studentTable);
        db.read("Student_t");
    }
}