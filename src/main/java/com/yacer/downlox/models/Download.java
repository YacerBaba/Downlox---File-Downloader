package com.yacer.downlox.models;

import com.yacer.downlox.enums.FileType;
import com.yacer.downlox.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "downloads",
        indexes = {@Index(name = "index_title", columnList = "title")}
)
@NamedQueries({
        @NamedQuery(name = "Download.findAll", query = "SELECT d FROM Download d where d.status <> :deletedStatus"),
})
public class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "title", length = 16380)
    private String title;
    @Column(name = "size")
    private Long size;
    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private FileType type;
    @Column(name = "downloaded_bytes")
    private Long downloadedBytes;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Lob
    @Column(name = "download_url", length = 16380)
    private String downloadUrl;
    @Column(name = "destination_path")
    private String destination_path;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "resumable")
    private Boolean isResumable;

    public Download() {

    }

    public Download(String title, FileType type, Long size, Long downloadedBytes, Status status, String downloadUrl, String destination_path, Boolean isResumable) {
        this.title = title;
        this.type = type;
        this.size = size;
        this.downloadedBytes = downloadedBytes;
        this.status = status;
        this.downloadUrl = downloadUrl;
        this.destination_path = destination_path;
        this.isResumable = isResumable;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public Boolean getResumable() {
        return isResumable;
    }

    public void setResumable(Boolean resumable) {
        isResumable = resumable;
    }

    public String getDestination_path() {
        return destination_path;
    }

    public void setDestination_path(String destination_path) {
        this.destination_path = destination_path;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(Long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return title;
    }
}
