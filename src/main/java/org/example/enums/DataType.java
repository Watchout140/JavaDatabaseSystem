package org.example.enums;

public enum DataType {
    INT(Integer.class),
    STRING(String.class);

    private final Class<?> typeClass;

    DataType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<?> getTypeClass() {
        return this.typeClass;
    }
}
