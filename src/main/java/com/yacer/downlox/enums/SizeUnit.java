package com.yacer.downlox.enums;

public enum SizeUnit {
    BYTE("B"),
    KILOBYTE("KB"),
    MEGABYTE("MB"),
    GIGABYTE("GB"),
    TERABYTE("TB");
    private final String name;
    SizeUnit(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
