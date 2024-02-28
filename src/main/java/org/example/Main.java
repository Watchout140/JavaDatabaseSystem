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
                .addColumn("id", DataType.INT, "HASH_INT_ROW", true)
                .addColumn("name", DataType.STRING, "HASH_STR_INT")
                .addColumn("grade", DataType.INT, "BTREE_INT_INT")
                .addColumn("class_id", DataType.INT, null, "Class");
        Table studentTable = db.createTable("Student_t", studentTableBuilder);

        Table.Builder classTableBuilder = new Table.Builder()
                .addColumn("id", DataType.INT, "HASH_INT_ROW", true)
                .addColumn("class_name", DataType.STRING, "HASH_STR_INT")
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
                .set("grade", 90)
                .set("class_id", null);
        Row newStudent5 = new Row()
                .set("id", 6)
                .set("name", "Patrik Lind")
                .set("grade", 100)
                .set("class_id", null);
        Row newStudent6 = new Row()
                .set("id", 7)
                .set("name", "Mohammad")
                .set("grade", 71)
                .set("class_id", null);
        Row newStudent7 = new Row()
                .set("id", 8)
                .set("name", "RAZZAC")
                .set("grade", 200)
                .set("class_id", null);
        Row newStudent8 = new Row()
                .set("id", 9)
                .set("name", "LEHME")
                .set("grade", 99)
                .set("class_id", null);
        Row newStudent9 = new Row()
                .set("id", 10)
                .set("name", "ABDULLA")
                .set("grade", 85)
                .set("class_id", null);
        Row newStudent10 = new Row()
                .set("id", 11)
                .set("name", "ABDULLA")
                .set("grade", 91)
                .set("class_id", null);
        Row newStudent11 = new Row()
                .set("id", 12)
                .set("name", "HASBULLA")
                .set("grade", 95)
                .set("class_id", null);
        Row newStudent12 = new Row()
                .set("id", 13)
                .set("name", "WALLA")
                .set("grade", 92)
                .set("class_id", null);
        Row newStudent13 = new Row()
                .set("id", 14)
                .set("name", "WALLA")
                .set("grade", 60)
                .set("class_id", null);
        Row newStudent14 = new Row()
                .set("id", 15)
                .set("name", "WALLA")
                .set("grade", 93)
                .set("class_id", null);
        Row newStudent15 = new Row()
                .set("id", 16)
                .set("name", "WALLA")
                .set("grade", 94)
                .set("class_id", null);
        Row newStudent16 = new Row()
                .set("id", 17)
                .set("name", "WALLA")
                .set("grade", 100)
                .set("class_id", null);
        Row newStudent17 = new Row()
                .set("id", 18)
                .set("name", "WALLA")
                .set("grade", 96)
                .set("class_id", null);
        Row newStudent18 = new Row()
                .set("id", 19)
                .set("name", "WALLA")
                .set("grade", 97)
                .set("class_id", null);
        Row newStudent19 = new Row()
                .set("id", 20)
                .set("name", "WALLA")
                .set("grade", 98)
                .set("class_id", null);
        Row newStudent20 = new Row()
                .set("id", 21)
                .set("name", "WALLA")
                .set("grade", 101)
                .set("class_id", null);
        Row newStudent21 = new Row()
                .set("id", 22)
                .set("name", "WALLA")
                .set("grade", 102)
                .set("class_id", null);
        Row newStudent22 = new Row()
                .set("id", 23)
                .set("name", "WALLA")
                .set("grade", 103)
                .set("class_id", null);
        Row newStudent23 = new Row()
                .set("id", 24)
                .set("name", "WALLA")
                .set("grade", 104)
                .set("class_id", null);
        Row newStudent24 = new Row()
                .set("id", 25)
                .set("name", "WALLA")
                .set("grade", 102)
                .set("class_id", null);

        studentTable.create(newStudent);
        studentTable.create(newStudent2);
        studentTable.create(newStudent3);
        studentTable.create(newStudent4);
        studentTable.create(newStudent5);
        studentTable.create(newStudent6);
        studentTable.create(newStudent7);
        studentTable.create(newStudent8);
        studentTable.create(newStudent9);
        studentTable.create(newStudent10);
        studentTable.create(newStudent11);
        studentTable.create(newStudent12);
        studentTable.create(newStudent13);
        studentTable.create(newStudent14);
        studentTable.create(newStudent15);
        studentTable.create(newStudent16);
        studentTable.create(newStudent17);
        studentTable.create(newStudent18);
        studentTable.create(newStudent19);
        studentTable.create(newStudent20);
        studentTable.create(newStudent21);
        studentTable.create(newStudent22);
        studentTable.create(newStudent23);
        studentTable.create(newStudent24);
        System.out.println("WALLA: " + db.find("Student_t", 2));
        Row matchingRows = studentTable.findFirst("id", 2);
        List<Row> list = studentTable.findAll("name", "Patrik Lind");
        System.out.println("LIST: " + list);
        System.out.println("MATCHINGROWS: " + matchingRows);

        List<Row> rows = studentTable.getColumn("grade", Integer.class).filter(grade -> grade > 101);
        System.out.println("ROWS: " + rows);

        Row student = studentTable.findFirst(row -> row.get("name").equals("Vincent Hedblom"));
        System.out.println("STUIDENT: " + student);

        db.save(studentTable);



        /*Table studentTable = db.read("Student_t");
        System.out.println("WALLA: " + db.find("Student_t", 2));
        Row matchingRows = studentTable.findFirst("id", 2);
        List<Row> list = studentTable.findAll("name", "Patrik Lind");
        System.out.println("LIST: " + list);
        System.out.println("MATCHINGROWS: " + matchingRows);

        List<Row> rows = studentTable.find(row -> row.getInt("grade") > 70);
        System.out.println("ROWS: " + rows);

        Row student = studentTable.findFirst(row -> row.get("name").equals("Vincent Hedblom"));
        System.out.println("STUIDENT: " + student);*/
    }
}