package com.yacer.downlox.models;

public class FileMetaData {
    private Long file_length;
    private String content_type;
    private String fileName;

    public FileMetaData(Long file_length, String content_type, String fileName) {
        this.file_length = file_length;
        this.content_type = content_type;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFile_length() {
        return file_length;
    }

    public void setFile_length(Long file_length) {
        this.file_length = file_length;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}
