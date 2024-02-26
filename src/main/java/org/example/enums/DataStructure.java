package org.example.enums;

public enum DataStructure {
    HASH("HASH"), LINKED("LINKED"), BTREE("BTREE");

    private final String name;

    DataStructure(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
