package com.yilnz.surfing.core.basic;

public class Header {
    private String name;
    private String value;

    public Header(String key, String value) {
        this.name = key;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
