package org.example.enums;

public enum DataType {
    INT(Integer.class),
    STRING(String.class);

    private final Class<?> _class;

    DataType(Class<?> _class) {
        this._class = _class;
    }

    public Class<?> getDataClass() {
        return _class;
    }
}
