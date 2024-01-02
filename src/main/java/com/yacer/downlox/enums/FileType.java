package com.yacer.downlox.enums;

public enum FileType {
    DOC("doc", "/icons/png/doc.png"), PDF("pdf", "/icons/png/pdf.png"), PNG("png", "/icons/png/image.png"),
    JPEG("jpeg", "/icons/png/image.png"), MP4("mp4", "/icons/png/video.png"), MP3("mp3", "/icons/png/music.png"),
    EXE("exe","/icons/png/exe.png"), TXT("txt", "/icons/png/doc.png"), RAR("rar", "/icons/png/zip.png"),
    ZIP("zip", "/icons/png/zip.png");

    FileType(String type, String iconPath) {
        this.type = type;
        this.iconPath = iconPath;
    }

    private String iconPath;
    private String type;

    @Override
    public String toString() {
        return type;
    }

    public String getIconPath(){
        return iconPath;
    }
}
