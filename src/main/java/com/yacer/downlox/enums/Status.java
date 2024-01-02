package com.yacer.downlox.enums;

public enum Status {
    IN_PROGRESS("In progress"),
    COMPLETED("Completed"),
    CANCELED("Canceled"),
    FAILED("Failed"),
    PAUSED("Paused"),
    DELETED("Deleted");
    private String displayedName;

    Status(String name) {
        displayedName = name;
    }

    @Override
    public String toString() {
        return displayedName;
    }
}
