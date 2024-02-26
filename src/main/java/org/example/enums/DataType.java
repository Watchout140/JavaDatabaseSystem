package org.example.enums;

public enum DataType {
    INT("INT"),
    STRING("STR");

    private final String name;

    DataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
