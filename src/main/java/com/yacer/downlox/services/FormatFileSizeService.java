package com.yacer.downlox.services;

public class FormatFileSizeService {
    public String execute(long fileSizeInBytes) {
        if (fileSizeInBytes < 1024) {
            return fileSizeInBytes + " B";
        } else if (fileSizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", fileSizeInBytes / 1024.0);
        } else if (fileSizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSizeInBytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", fileSizeInBytes / (1024.0 * 1024 * 1024));
        }
    }
}
